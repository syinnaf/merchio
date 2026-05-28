package com.example.merchio;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchio.adapters.CheckoutAdapter;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.CartItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    RecyclerView rvCheckout;

    TextView txtAddress,
            txtSubtotal,
            txtShipping,
            txtTax,
            txtTotal;

    RadioButton rbExpress,
            rbStandard,
            rbCard,
            rbWallet;

    Button btnBuy;

    DbHelper dbHelper;

    List<CartItem> checkoutItems =
            new ArrayList<>();

    int userId = 1;

    int subtotal = 0;
    int shippingCost = 15000;
    int tax = 0;
    int total = 0;

    int addressId = 0;
    String fullAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initViews();

        dbHelper = new DbHelper(this);

        rvCheckout.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadAddress();
        loadCheckoutItems();

        setupShipping();

        btnBuy.setOnClickListener(v -> processOrder());
    }

    private void initViews() {

        rvCheckout = findViewById(R.id.rvCheckout);

        txtAddress = findViewById(R.id.txtAddress);

        txtSubtotal = findViewById(R.id.txtSubtotal);
        txtShipping = findViewById(R.id.txtShipping);
        txtTax = findViewById(R.id.txtTax);
        txtTotal = findViewById(R.id.txtTotal);

        rbExpress = findViewById(R.id.rbExpress);
        rbStandard = findViewById(R.id.rbStandard);

        rbCard = findViewById(R.id.rbCard);
        rbWallet = findViewById(R.id.rbWallet);

        btnBuy = findViewById(R.id.btnBuy);

        rbExpress.setChecked(true);
        rbCard.setChecked(true);
    }

    private void loadAddress() {

        Cursor cursor =
                dbHelper.getDefaultAddress(userId);

        if(cursor.moveToFirst()) {

            addressId =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("id")
                    );

            String recipient =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("recipient_name")
                    );

            String phone =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("phone")
                    );

            String address =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("address")
                    );

            String city =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("city")
                    );

            String postal =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("postal_code")
                    );

            fullAddress =
                    recipient + "\n"
                            + phone + "\n"
                            + address + ", "
                            + city + " "
                            + postal;

            txtAddress.setText(fullAddress);
        }

        cursor.close();
    }

    private void loadCheckoutItems() {

        Cursor cursor =
                dbHelper.getCheckedCartByUser(userId);

        while(cursor.moveToNext()) {

            CartItem item = new CartItem();

            item.setId(
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("id")
                    )
            );

            item.setProductId(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("product_id")
                    )
            );

            item.setProductName(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("product_name")
                    )
            );

            item.setProductImage(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("product_image")
                    )
            );

            item.setProductPrice(
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("product_price")
                    )
            );

            item.setQuantity(
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("quantity")
                    )
            );

            item.setType(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("type")
                    )
            );

            checkoutItems.add(item);

            subtotal +=
                    item.getProductPrice()
                            * item.getQuantity();
        }

        cursor.close();

        CheckoutAdapter adapter =
                new CheckoutAdapter(
                        this,
                        checkoutItems
                );

        rvCheckout.setAdapter(adapter);

        calculateTotal();
    }

    private void setupShipping() {

        rbExpress.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked) {

                shippingCost = 15000;
                calculateTotal();
            }
        });

        rbStandard.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked) {

                shippingCost = 10000;
                calculateTotal();
            }
        });
    }

    private void calculateTotal() {

        tax = (int)(subtotal * 0.1);

        total =
                subtotal
                        + shippingCost
                        + tax;

        txtSubtotal.setText(rupiah(subtotal));
        txtShipping.setText(rupiah(shippingCost));
        txtTax.setText(rupiah(tax));
        txtTotal.setText(rupiah(total));
    }

    private void processOrder() {

        String paymentMethod =
                rbCard.isChecked()
                        ? "Credit Card"
                        : "Digital Wallet";

        String shippingMethod =
                rbExpress.isChecked()
                        ? "Express"
                        : "Standard";

        String estimatedArrival =
                rbExpress.isChecked()
                        ? "1-2 Days"
                        : "4-5 Days";

        long orderId =
                dbHelper.checkoutFromCart(
                        userId,
                        paymentMethod,
                        shippingMethod,
                        addressId,
                        fullAddress,
                        shippingCost,
                        tax,
                        estimatedArrival
                );

        if(orderId != -1) {

            Toast.makeText(
                    this,
                    "Order berhasil",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Order gagal",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private String rupiah(int amount) {

        NumberFormat format =
                NumberFormat.getCurrencyInstance(
                        new Locale("id","ID")
                );

        return format.format(amount)
                .replace(",00","");
    }
}