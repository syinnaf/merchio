package com.example.merchio;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchio.adapters.OrderSuccessAdapter;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.CartItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PurchaseHistoryDetailActivity
        extends AppCompatActivity {

    private TextView btnBack;

    private TextView txtOrderCode;
    private TextView txtEstimatedArrival;

    private TextView txtShippingMethod;
    private TextView txtAddress;

    private TextView txtSubtotal;
    private TextView txtShipping;
    private TextView txtPaymentMethod;
    private TextView txtTax;
    private TextView txtTotal;

    private RecyclerView rvOrderItems;

    private DbHelper dbHelper;

    private long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_purchase_history_detail
        );

        btnBack = findViewById(R.id.btnBack);

        txtOrderCode =
                findViewById(R.id.txtOrderCode);

        txtEstimatedArrival =
                findViewById(R.id.txtEstimatedArrival);

        txtShippingMethod =
                findViewById(R.id.txtShippingMethod);

        txtPaymentMethod =
                findViewById(R.id.txtPaymentMethod);

        txtAddress =
                findViewById(R.id.txtAddress);

        txtSubtotal =
                findViewById(R.id.txtSubtotal);

        txtShipping =
                findViewById(R.id.txtShipping);

        txtTax =
                findViewById(R.id.txtTax);

        txtTotal =
                findViewById(R.id.txtTotal);

        rvOrderItems =
                findViewById(R.id.rvOrderItems);

        dbHelper = new DbHelper(this);

        orderId =
                getIntent().getLongExtra(
                        "order_id",
                        -1
                );

        btnBack.setOnClickListener(v -> finish());

        loadOrderDetail();
        loadOrderItems();
    }

    private void loadOrderDetail(){

        Cursor cursor =
                dbHelper.getOrderById(orderId);

        if(cursor != null && cursor.moveToFirst()){

            String orderCode = getStringColumn(cursor, "order_code", "MRC" + orderId);
            String estimatedArrival = getStringColumn(cursor, "estimated_arrival", "-");
            String paymentMethod = getStringColumn(cursor, "payment_method", "-");
            String shippingMethod = getStringColumn(cursor, "shipping_method", "-");
            String savedAddress = getStringColumn(cursor, "address", "");
            int savedAddressId = getIntColumn(cursor, "address_id", -1);

            int shipping = getIntColumn(cursor, "shipping_price", 0);
            int tax = getIntColumn(cursor, "tax", 0);
            int total = getIntColumn(cursor, "total_price", 0);
            int subtotal = total - shipping - tax;

            if(savedAddress == null || savedAddress.trim().isEmpty()){
                savedAddress = buildAddressTextFromAddressId(savedAddressId);
            }

            if(savedAddress == null || savedAddress.trim().isEmpty()){
                savedAddress = "Alamat belum tersedia";
            }

            txtOrderCode.setText("#" + orderCode);
            txtEstimatedArrival.setText(estimatedArrival);
            txtPaymentMethod.setText(paymentMethod);
            txtShippingMethod.setText(shippingMethod);
            txtAddress.setText(savedAddress);
            txtSubtotal.setText(rupiah(subtotal));
            txtShipping.setText(rupiah(shipping));
            txtTax.setText(rupiah(tax));
            txtTotal.setText(rupiah(total));
        }

        if(cursor != null){
            cursor.close();
        }
    }

    private void loadOrderItems(){

        List<CartItem> itemList =
                new ArrayList<>();

        Cursor cursor =
                dbHelper.getOrderItems(orderId);

        if(cursor != null && cursor.moveToFirst()){

            do{

                CartItem item =
                        new CartItem();

                item.setProductId(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "product_id"
                                )
                        )
                );

                item.setProductName(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "product_name"
                                )
                        )
                );

                item.setProductImage(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "product_image"
                                )
                        )
                );

                item.setProductPrice(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "price"
                                )
                        )
                );

                item.setQuantity(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "quantity"
                                )
                        )
                );

                item.setType(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "type"
                                )
                        )
                );

                itemList.add(item);

            }while(cursor.moveToNext());
        }

        if(cursor != null){
            cursor.close();
        }

        rvOrderItems.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvOrderItems.setNestedScrollingEnabled(false);

        rvOrderItems.setAdapter(
                new OrderSuccessAdapter(
                        this,
                        itemList
                )
        );
    }

    private String buildAddressTextFromAddressId(int addressId){
        if(addressId == -1){
            return "";
        }

        Cursor addressCursor = null;

        try{
            addressCursor = dbHelper.getAddressById(addressId);

            if(addressCursor != null && addressCursor.moveToFirst()){
                String recipient = getStringColumn(addressCursor, "recipient_name", "");
                String phone = getStringColumn(addressCursor, "phone", "");
                String address = getStringColumn(addressCursor, "address", "");
                String city = getStringColumn(addressCursor, "city", "");
                String postalCode = getStringColumn(addressCursor, "postal_code", "");

                StringBuilder builder = new StringBuilder();

                appendLine(builder, recipient);
                appendLine(builder, phone);

                StringBuilder addressLine = new StringBuilder();
                appendInline(addressLine, address);
                appendInline(addressLine, city);
                appendInline(addressLine, postalCode);

                appendLine(builder, addressLine.toString());

                return builder.toString().trim();
            }

        }finally{
            if(addressCursor != null){
                addressCursor.close();
            }
        }

        return "";
    }

    private void appendLine(StringBuilder builder, String value){
        if(value == null || value.trim().isEmpty()){
            return;
        }

        if(builder.length() > 0){
            builder.append("\n");
        }

        builder.append(value.trim());
    }

    private void appendInline(StringBuilder builder, String value){
        if(value == null || value.trim().isEmpty()){
            return;
        }

        if(builder.length() > 0){
            builder.append(", ");
        }

        builder.append(value.trim());
    }

    private String getStringColumn(Cursor cursor, String columnName, String fallback){
        int index = cursor.getColumnIndex(columnName);

        if(index == -1 || cursor.isNull(index)){
            return fallback;
        }

        String value = cursor.getString(index);
        return value == null ? fallback : value;
    }

    private int getIntColumn(Cursor cursor, String columnName, int fallback){
        int index = cursor.getColumnIndex(columnName);

        if(index == -1 || cursor.isNull(index)){
            return fallback;
        }

        return cursor.getInt(index);
    }

    private String rupiah(int amount){

        NumberFormat format =
                NumberFormat.getCurrencyInstance(
                        new Locale("id","ID")
                );

        return format.format(amount)
                .replace(",00","");
    }
}