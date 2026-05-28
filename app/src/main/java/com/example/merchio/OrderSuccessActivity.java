package com.example.merchio;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchio.adapters.OrderSuccessAdapter;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class OrderSuccessActivity extends AppCompatActivity {

    TextView txtOrderCode, txtEstimatedArrival;
    RecyclerView rvOrderItems;
    Button btnContinueShopping;

    DbHelper dbHelper;

    long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        txtOrderCode        = findViewById(R.id.txtOrderCode);
        txtEstimatedArrival = findViewById(R.id.txtEstimatedArrival);
        rvOrderItems        = findViewById(R.id.rvOrderItems);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);

        dbHelper = new DbHelper(this);

        // Terima orderId dari CheckoutActivity
        orderId = getIntent().getLongExtra("order_id", -1);

        if (orderId != -1) {
            loadOrderDetail();
            loadOrderItems();
        }

        btnContinueShopping.setOnClickListener(v -> goToHome());
    }

    private void loadOrderDetail() {
        Cursor cursor = dbHelper.getOrderById(orderId);

        if (cursor.moveToFirst()) {
            String orderCode = cursor.getString(
                    cursor.getColumnIndexOrThrow("order_code"));

            String estimatedArrival = cursor.getString(
                    cursor.getColumnIndexOrThrow("estimated_arrival"));

            txtOrderCode.setText("#" + orderCode);
            txtEstimatedArrival.setText(estimatedArrival);
        }

        cursor.close();
    }

    private void loadOrderItems() {
        List<CartItem> itemList = new ArrayList<>();

        Cursor cursor = dbHelper.getOrderItems(orderId);

        while (cursor.moveToNext()) {
            CartItem item = new CartItem();

            item.setProductId(cursor.getString(
                    cursor.getColumnIndexOrThrow("product_id")));

            item.setProductName(cursor.getString(
                    cursor.getColumnIndexOrThrow("product_name")));

            item.setProductImage(cursor.getString(
                    cursor.getColumnIndexOrThrow("product_image")));

            item.setProductPrice(cursor.getInt(
                    cursor.getColumnIndexOrThrow("price")));

            item.setQuantity(cursor.getInt(
                    cursor.getColumnIndexOrThrow("quantity")));

            item.setType(cursor.getString(
                    cursor.getColumnIndexOrThrow("type")));

            itemList.add(item);
        }

        cursor.close();

        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rvOrderItems.setNestedScrollingEnabled(false);
        rvOrderItems.setAdapter(new OrderSuccessAdapter(this, itemList));
    }

    private void goToHome() {
        // Clear back stack — user tidak bisa back ke halaman sukses
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}