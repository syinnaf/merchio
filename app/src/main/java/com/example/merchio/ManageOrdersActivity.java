package com.example.merchio;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchio.adapters.AdminOrderAdapter;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.AdminOrder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManageOrdersActivity extends AppCompatActivity {

    private TextView btnBack, tvEmpty;
    private RecyclerView rvOrders;

    private DbHelper dbHelper;
    private AdminOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);

        dbHelper = new DbHelper(this);

        btnBack = findViewById(R.id.btn_back);
        tvEmpty = findViewById(R.id.tv_empty_orders);
        rvOrders = findViewById(R.id.rv_orders);

        btnBack.setOnClickListener(v -> finish());

        adapter = new AdminOrderAdapter(this, new AdminOrderAdapter.OnOrderActionListener() {
            @Override
            public void onChangeStatus(AdminOrder order) {
                showStatusDialog(order);
            }

            @Override
            public void onDetail(AdminOrder order) {
                showDetailDialog(order.getId());
            }
        });

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);

        loadOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        List<AdminOrder> orders = new ArrayList<>();

        Cursor cursor = dbHelper.getAllOrders();

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String orderCode = cursor.getString(cursor.getColumnIndexOrThrow("order_code"));
            String customerName = cursor.getString(cursor.getColumnIndexOrThrow("customer_name"));
            String customerEmail = cursor.getString(cursor.getColumnIndexOrThrow("customer_email"));
            String orderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));
            int totalPrice = cursor.getInt(cursor.getColumnIndexOrThrow("total_price"));
            int shippingPrice = cursor.getInt(cursor.getColumnIndexOrThrow("shipping_price"));
            int tax = cursor.getInt(cursor.getColumnIndexOrThrow("tax"));
            String paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow("payment_method"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

            orders.add(new AdminOrder(
                    id,
                    orderCode,
                    customerName,
                    customerEmail,
                    orderDate,
                    totalPrice,
                    shippingPrice,
                    tax,
                    paymentMethod,
                    address,
                    status
            ));
        }

        cursor.close();

        adapter.setOrders(orders);
        tvEmpty.setVisibility(orders.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void showStatusDialog(AdminOrder order) {
        android.view.View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_order_status, null, false);

        RadioGroup rgStatus = view.findViewById(R.id.rg_order_status);
        RadioButton rbPacking = view.findViewById(R.id.rb_status_packing);
        RadioButton rbShipping = view.findViewById(R.id.rb_status_shipping);
        RadioButton rbDelivered = view.findViewById(R.id.rb_status_delivered);
        RadioButton rbCancelled = view.findViewById(R.id.rb_status_cancelled);
        TextView btnCancel = view.findViewById(R.id.btn_cancel_status);
        TextView btnSave = view.findViewById(R.id.btn_save_status);

        String status = order.getStatus();
        if (DbHelper.STATUS_SHIPPING.equalsIgnoreCase(status)) {
            rbShipping.setChecked(true);
        } else if (DbHelper.STATUS_DELIVERED.equalsIgnoreCase(status)) {
            rbDelivered.setChecked(true);
        } else if (DbHelper.STATUS_CANCELLED.equalsIgnoreCase(status)) {
            rbCancelled.setChecked(true);
        } else {
            rbPacking.setChecked(true);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String newStatus = DbHelper.STATUS_PACKING;
            int checkedId = rgStatus.getCheckedRadioButtonId();

            if (checkedId == R.id.rb_status_shipping) {
                newStatus = DbHelper.STATUS_SHIPPING;
            } else if (checkedId == R.id.rb_status_delivered) {
                newStatus = DbHelper.STATUS_DELIVERED;
            } else if (checkedId == R.id.rb_status_cancelled) {
                newStatus = DbHelper.STATUS_CANCELLED;
            }

            boolean updated = dbHelper.updateOrderStatus(order.getId(), newStatus);

            if (updated) {
                Toast.makeText(this, "Status order berhasil diupdate", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadOrders();
            } else {
                Toast.makeText(this, "Gagal update status order", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setOnShowListener(d -> resizeDialog(dialog, 0.88f));
        dialog.show();
    }

    private void showDetailDialog(long orderId) {
        android.view.View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_order_detail, null, false);

        TextView btnClose = view.findViewById(R.id.btn_close_detail);
        TextView tvOrderId = view.findViewById(R.id.tv_detail_order_id);
        TextView tvCustomer = view.findViewById(R.id.tv_detail_customer);
        TextView tvDate = view.findViewById(R.id.tv_detail_date);
        TextView tvAddress = view.findViewById(R.id.tv_detail_address);
        TextView tvPayment = view.findViewById(R.id.tv_detail_payment);
        TextView tvSubtotal = view.findViewById(R.id.tv_detail_subtotal);
        TextView tvShipping = view.findViewById(R.id.tv_detail_shipping);
        TextView tvTax = view.findViewById(R.id.tv_detail_tax);
        TextView tvTotal = view.findViewById(R.id.tv_detail_total);
        LinearLayout productContainer = view.findViewById(R.id.layout_detail_products);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnClose.setOnClickListener(v -> dialog.dismiss());

        Cursor orderCursor = dbHelper.getOrderWithUser(orderId);

        if (orderCursor.moveToFirst()) {
            String orderCode = orderCursor.getString(orderCursor.getColumnIndexOrThrow("order_code"));
            String customerName = orderCursor.getString(orderCursor.getColumnIndexOrThrow("customer_name"));
            String customerEmail = orderCursor.getString(orderCursor.getColumnIndexOrThrow("customer_email"));
            String orderDate = orderCursor.getString(orderCursor.getColumnIndexOrThrow("order_date"));
            String address = orderCursor.getString(orderCursor.getColumnIndexOrThrow("address"));
            String payment = orderCursor.getString(orderCursor.getColumnIndexOrThrow("payment_method"));
            int total = orderCursor.getInt(orderCursor.getColumnIndexOrThrow("total_price"));
            int shipping = orderCursor.getInt(orderCursor.getColumnIndexOrThrow("shipping_price"));
            int tax = orderCursor.getInt(orderCursor.getColumnIndexOrThrow("tax"));
            int subtotal = Math.max(0, total - shipping - tax);

            tvOrderId.setText(empty(orderCode) ? String.valueOf(orderId) : orderCode);
            tvCustomer.setText(empty(customerName) ? value(customerEmail) : customerName);
            tvDate.setText(value(orderDate));
            tvAddress.setText(value(address));
            tvPayment.setText(value(payment));
            tvSubtotal.setText(rupiah(subtotal));
            tvShipping.setText(rupiah(shipping));
            tvTax.setText(rupiah(tax));
            tvTotal.setText(rupiah(total));
        }

        orderCursor.close();

        Cursor itemCursor = dbHelper.getOrderItems(orderId);
        int number = 1;

        while (itemCursor.moveToNext()) {
            String productName = itemCursor.getString(itemCursor.getColumnIndexOrThrow("product_name"));
            int price = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("price"));
            int quantity = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("quantity"));

            android.view.View itemView = LayoutInflater.from(this)
                    .inflate(R.layout.item_order_detail_product, productContainer, false);

            TextView tvNumber = itemView.findViewById(R.id.tv_product_number);
            TextView tvProductName = itemView.findViewById(R.id.tv_product_name);
            TextView tvProductQty = itemView.findViewById(R.id.tv_product_qty);
            TextView tvProductTotal = itemView.findViewById(R.id.tv_product_total);

            tvNumber.setText(String.valueOf(number));
            tvProductName.setText(value(productName));
            tvProductQty.setText("x " + quantity);
            tvProductTotal.setText(rupiah(price * quantity));

            productContainer.addView(itemView);
            number++;
        }

        itemCursor.close();

        if (number == 1) {
            TextView empty = new TextView(this);
            empty.setText("Belum ada produk di order ini");
            empty.setTextSize(12);
            empty.setPadding(6, 6, 6, 6);
            productContainer.addView(empty);
        }

        dialog.setOnShowListener(d -> resizeDialog(dialog, 0.90f));
        dialog.show();
    }

    private void resizeDialog(AlertDialog dialog, float widthRatio) {
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * widthRatio),
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private String rupiah(int amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(amount).replace(",00", "");
    }

    private String value(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private boolean empty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
