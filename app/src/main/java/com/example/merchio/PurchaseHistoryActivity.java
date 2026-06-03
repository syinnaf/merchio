package com.example.merchio;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchio.adapters.HistoryAdapter;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.OrderHistory;
import com.example.merchio.models.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class PurchaseHistoryActivity extends AppCompatActivity {

    private TextView btnBack;

    private RecyclerView rvActiveOrders;
    private RecyclerView rvPastOrders;

    private DbHelper dbHelper;
    private SessionManager sessionManager;

    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        btnBack = findViewById(R.id.btn_back);

        rvActiveOrders =
                findViewById(R.id.rvActiveOrders);

        rvPastOrders =
                findViewById(R.id.rvPastOrders);

        rvActiveOrders.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvPastOrders.setLayoutManager(
                new LinearLayoutManager(this)
        );

        dbHelper = new DbHelper(this);
        sessionManager = new SessionManager(this);

        userId = sessionManager.getUserId();

        btnBack.setOnClickListener(v -> finish());

        loadOrders();
    }

    private void loadOrders() {

        if(userId == -1){
            return;
        }

        List<OrderHistory> activeList =
                new ArrayList<>();

        List<OrderHistory> pastList =
                new ArrayList<>();

        Cursor activeCursor =
                dbHelper.getActiveOrders(userId);

        while(activeCursor.moveToNext()){

            long orderId =
                    activeCursor.getLong(
                            activeCursor.getColumnIndexOrThrow("id")
                    );

            String status =
                    activeCursor.getString(
                            activeCursor.getColumnIndexOrThrow("status")
                    );

            int totalPrice =
                    activeCursor.getInt(
                            activeCursor.getColumnIndexOrThrow("total_price")
                    );

            boolean isReceived =
                    activeCursor.getInt(
                            activeCursor.getColumnIndexOrThrow("is_received")
                    ) == 1;

            OrderItem firstItem =
                    dbHelper.getFirstOrderItem(orderId);

            int itemCount =
                    dbHelper.getOrderItemCount(orderId);

            if(firstItem != null){

                activeList.add(
                        new OrderHistory(
                                orderId,
                                firstItem.getProductName(),
                                firstItem.getProductImage(),
                                status,
                                totalPrice,
                                isReceived,
                                itemCount
                        )
                );
            }
        }

        activeCursor.close();

        Cursor pastCursor =
                dbHelper.getPastOrders(userId);

        while(pastCursor.moveToNext()){

            long orderId =
                    pastCursor.getLong(
                            pastCursor.getColumnIndexOrThrow("id")
                    );

            String status =
                    pastCursor.getString(
                            pastCursor.getColumnIndexOrThrow("status")
                    );

            int totalPrice =
                    pastCursor.getInt(
                            pastCursor.getColumnIndexOrThrow("total_price")
                    );

            boolean isReceived =
                    pastCursor.getInt(
                            pastCursor.getColumnIndexOrThrow("is_received")
                    ) == 1;

            OrderItem firstItem =
                    dbHelper.getFirstOrderItem(orderId);

            int itemCount =
                    dbHelper.getOrderItemCount(orderId);

            if(firstItem != null){

                pastList.add(
                        new OrderHistory(
                                orderId,
                                firstItem.getProductName(),
                                firstItem.getProductImage(),
                                status,
                                totalPrice,
                                isReceived,
                                itemCount
                        )
                );
            }
        }

        pastCursor.close();

        rvActiveOrders.setAdapter(
                new HistoryAdapter(
                        this,
                        activeList,
                        orderId -> {

                            dbHelper.confirmOrderReceived(
                                    orderId
                            );

                            Toast.makeText(
                                    this,
                                    "Order confirmed",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadOrders();
                        }
                )
        );

        rvPastOrders.setAdapter(
                new HistoryAdapter(
                        this,
                        pastList,
                        null
                )
        );
    }
}