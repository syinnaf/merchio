package com.example.merchio;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.merchio.db.DbHelper;

public class PurchaseHistoryActivity extends AppCompatActivity {

    private TextView btnBack, tvOrderSummary;

    private DbHelper dbHelper;
    private SessionManager sessionManager;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        btnBack = findViewById(R.id.btn_back);
        tvOrderSummary = findViewById(R.id.tv_order_summary);

        dbHelper = new DbHelper(this);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        btnBack.setOnClickListener(v -> finish());

        loadOrders();
    }

    private void loadOrders() {
        if (userId == -1) {
            tvOrderSummary.setText("Silakan login dulu.");
            return;
        }

        Cursor cursor = dbHelper.getOrdersByUser(userId);

        if (cursor.getCount() == 0) {
            tvOrderSummary.setText("Belum ada order.");
            cursor.close();
            return;
        }

        StringBuilder builder = new StringBuilder();

        while (cursor.moveToNext()) {
            String orderCode = cursor.getString(cursor.getColumnIndexOrThrow("order_code"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            int total = cursor.getInt(cursor.getColumnIndexOrThrow("total_price"));

            builder.append(orderCode)
                    .append("\nStatus: ")
                    .append(status)
                    .append("\nTotal: Rp")
                    .append(total)
                    .append("\n\n");
        }

        cursor.close();
        tvOrderSummary.setText(builder.toString().trim());
    }
}