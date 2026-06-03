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

        if(cursor.moveToFirst()){

            String orderCode =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "order_code"
                            )
                    );

            String estimatedArrival =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "estimated_arrival"
                            )
                    );

            String paymentMethod =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "payment_method"
                            )
                    );

            txtPaymentMethod.setText(paymentMethod);

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

            int total =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "total_price"
                            )
                    );

            int subtotal = total - shipping - tax;

            txtOrderCode.setText(
                    "#" + orderCode
            );

            txtEstimatedArrival.setText(
                    estimatedArrival
            );

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

            txtShippingMethod.setText(
                    "Express"
            );

            txtAddress.setText(
                    "Customer Address"
            );
        }

        cursor.close();
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

        if(cursor != null && cursor.moveToFirst()){

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
        }

        cursor.close();

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

    private String rupiah(int amount){

        NumberFormat format =
                NumberFormat.getCurrencyInstance(
                        new Locale("id","ID")
                );

        return format.format(amount)
                .replace(",00","");
    }
}