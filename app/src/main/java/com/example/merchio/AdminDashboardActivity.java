package com.example.merchio;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.merchio.db.DbHelper;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvTotalUsers, tvTotalOrders, tvPacking, tvShipping, tvDelivered;
    private TextView tvCancelled, tvCancelledSummary;
    private TextView menuManageOrders, menuManageUsers, menuLogout;

    private DbHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        dbHelper = new DbHelper(this);
        sessionManager = new SessionManager(this);

        dbHelper.ensureRoleColumnExists();
        dbHelper.ensureUserActiveColumnExists();
        dbHelper.createDefaultAdminIfNeeded();

        initViews();
        loadDashboardData();
        setupClicks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbHelper != null) {
            loadDashboardData();
        }
    }

    private void initViews() {
        tvTotalUsers = findViewById(R.id.tv_total_users);
        tvTotalOrders = findViewById(R.id.tv_total_orders);
        tvPacking = findViewById(R.id.tv_packing);
        tvShipping = findViewById(R.id.tv_shipping);
        tvDelivered = findViewById(R.id.tv_delivered);
        tvCancelled = findViewById(R.id.tv_cancelled);
        tvCancelledSummary = findViewById(R.id.tv_cancelled_summary);

        menuManageOrders = findViewById(R.id.menu_manage_orders);
        menuManageUsers = findViewById(R.id.menu_manage_users);
        menuLogout = findViewById(R.id.menu_logout);
    }

    private void loadDashboardData() {
        int users = countCustomers();
        int orders = countTable("orders");
        int packing = countOrdersByStatus(DbHelper.STATUS_PACKING);
        int shipping = countOrdersByStatus(DbHelper.STATUS_SHIPPING);
        int delivered = countOrdersByStatus(DbHelper.STATUS_DELIVERED);
        int cancelled = countOrdersByStatus(DbHelper.STATUS_CANCELLED);

        tvTotalUsers.setText(String.valueOf(users));
        tvTotalOrders.setText(String.valueOf(orders));
        tvCancelled.setText(String.valueOf(cancelled));
        tvPacking.setText(packing + "\nPacking");
        tvShipping.setText(shipping + "\nShipping");
        tvDelivered.setText(delivered + "\nDelivered");
        tvCancelledSummary.setText(cancelled + "\nCancelled");
    }

    private int countCustomers() {
        dbHelper.ensureRoleColumnExists();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) AS total FROM users WHERE LOWER(IFNULL(role, 'customer')) != 'admin'",
                null
        );

        int total = 0;

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }

        cursor.close();
        return total;
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
        menuManageOrders.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageOrdersActivity.class);
            startActivity(intent);
        });

        menuManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageUsersActivity.class);
            startActivity(intent);
        });

        menuLogout.setOnClickListener(v -> {
            sessionManager.logout();

            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
