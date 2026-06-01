package com.example.merchio;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

public class OrderDetailActivity extends AppCompatActivity {

    TextView txtOrderCode;
    TextView txtOrderDate;
    TextView txtEstimatedArrival;

    TextView txtShippingMethod;
    TextView txtAddress;
    TextView txtPaymentMethod;

    TextView txtSubtotal;
    TextView txtShipping;
    TextView txtTax;
    TextView txtTotal;

    RecyclerView rvOrderItems;

    ImageView imgPacking;
    ImageView imgShipping;
    ImageView imgDelivered;

    DbHelper dbHelper;

    long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        dbHelper = new DbHelper(this);

        findViewById(R.id.btnBack)
                .setOnClickListener(v -> finish());

        txtOrderCode = findViewById(R.id.txtOrderCode);
        txtOrderDate = findViewById(R.id.txtOrderDate);
        txtEstimatedArrival = findViewById(R.id.txtEstimatedArrival);

        txtShippingMethod = findViewById(R.id.txtShippingMethod);
        txtAddress = findViewById(R.id.txtAddress);
        txtPaymentMethod = findViewById(R.id.txtPaymentMethod);

        txtSubtotal = findViewById(R.id.txtSubtotal);
        txtShipping = findViewById(R.id.txtShipping);
        txtTax = findViewById(R.id.txtTax);
        txtTotal = findViewById(R.id.txtTotal);

        rvOrderItems = findViewById(R.id.rvOrderItems);

        imgPacking = findViewById(R.id.imgPacking);
        imgShipping = findViewById(R.id.imgShipping);
        imgDelivered = findViewById(R.id.imgDelivered);

        orderId =
                getIntent().getLongExtra(
                        "order_id",
                        -1
                );

        loadOrder();
        loadItems();
    }

    private void loadOrder(){

        Cursor cursor =
                dbHelper.getOrderById(orderId);

        if(cursor.moveToFirst()){

            txtOrderCode.setText(
                    "#" +
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            "order_code"
                                    )
                            )
            );

            txtOrderDate.setText(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "order_date"
                            )
                    )
            );

            txtEstimatedArrival.setText(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "estimated_arrival"
                            )
                    )
            );

            txtShippingMethod.setText(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "shipping_method"
                            )
                    )
            );

            txtAddress.setText(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "address"
                            )
                    )
            );

            txtPaymentMethod.setText(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "payment_method"
                            )
                    )
            );

            int total =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "total_price"
                            )
                    );

            int shipping =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "shipping_price"
                            )
                    );

            int tax =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "tax"
                            )
                    );

            int subtotal =
                    total - shipping - tax;

            txtSubtotal.setText(
                    rupiah(subtotal)
            );

            txtShipping.setText(
                    rupiah(shipping)
            );

            txtTax.setText(
                    rupiah(tax)
            );

            txtTotal.setText(
                    rupiah(total)
            );

            String status =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "status"
                            )
                    );

            setupStatus(status);
        }

        cursor.close();
    }

    private void setupStatus(String status){

        imgPacking.setSelected(false);
        imgShipping.setSelected(false);
        imgDelivered.setSelected(false);

        if(status.equals(DbHelper.STATUS_PACKING)){
            imgPacking.setSelected(true);
        }

        else if(status.equals(DbHelper.STATUS_SHIPPING)){
            imgPacking.setSelected(true);
            imgShipping.setSelected(true);
        }

        else if(status.equals(DbHelper.STATUS_DELIVERED)){
            imgPacking.setSelected(true);
            imgShipping.setSelected(true);
            imgDelivered.setSelected(true);
        }
    }

    private void loadItems(){

        List<CartItem> list =
                new ArrayList<>();

        Cursor cursor =
                dbHelper.getOrderItems(orderId);

        while(cursor.moveToNext()){

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

            list.add(item);
        }

        cursor.close();

        rvOrderItems.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvOrderItems.setNestedScrollingEnabled(false);

        rvOrderItems.setAdapter(
                new OrderSuccessAdapter(
                        this,
                        list
                )
        );
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