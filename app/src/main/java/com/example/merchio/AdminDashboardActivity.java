package com.example.merchio;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.merchio.db.DbHelper;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvTotalUsers, tvTotalOrders, tvPacking, tvShipping, tvDelivered;
    private TextView menuManageProducts, menuManageOrders, menuManageUsers, menuLogout;

    private DbHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        dbHelper = new DbHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        loadDashboardData();
        setupClicks();
    }

    private void initViews() {
        tvTotalUsers = findViewById(R.id.tv_total_users);
        tvTotalOrders = findViewById(R.id.tv_total_orders);
        tvPacking = findViewById(R.id.tv_packing);
        tvShipping = findViewById(R.id.tv_shipping);
        tvDelivered = findViewById(R.id.tv_delivered);

        menuManageProducts = findViewById(R.id.menu_manage_products);
        menuManageOrders = findViewById(R.id.menu_manage_orders);
        menuManageUsers = findViewById(R.id.menu_manage_users);
        menuLogout = findViewById(R.id.menu_logout);
    }

    private void loadDashboardData() {
        tvTotalUsers.setText(String.valueOf(countTable("users")));
        tvTotalOrders.setText(String.valueOf(countTable("orders")));
        tvPacking.setText(String.valueOf(countOrdersByStatus("packing")));
        tvShipping.setText(String.valueOf(countOrdersByStatus("shipping")));
        tvDelivered.setText(String.valueOf(countOrdersByStatus("delivered")));
    }

    private int countTable(String tableName) {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) AS total FROM " + tableName,
                null
        );

        int total = 0;

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }

        cursor.close();
        return total;
    }

    private int countOrdersByStatus(String status) {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) AS total FROM orders WHERE LOWER(status) = LOWER(?)",
                new String[]{status}
        );

        int total = 0;

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }

        cursor.close();
        return total;
    }

    private void setupClicks() {
        menuManageProducts.setOnClickListener(v ->
                android.widget.Toast.makeText(this, "Manage Products dummy untuk demo", android.widget.Toast.LENGTH_SHORT).show()
        );

        menuManageOrders.setOnClickListener(v ->
                android.widget.Toast.makeText(this, "Manage Orders dummy untuk demo", android.widget.Toast.LENGTH_SHORT).show()
        );

        menuManageUsers.setOnClickListener(v ->
                android.widget.Toast.makeText(this, "Manage Users dummy untuk demo", android.widget.Toast.LENGTH_SHORT).show()
        );

        menuLogout.setOnClickListener(v -> {
            sessionManager.logout();

            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}