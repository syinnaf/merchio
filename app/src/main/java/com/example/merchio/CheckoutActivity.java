package com.example.merchio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchio.adapters.CheckoutAdapter;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.CartItem;
import com.example.merchio.models.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView rvCheckout;

    private TextView txtAddress,
            txtSubtotal,
            txtShipping,
            txtTax,
            txtTotal;

    private RadioButton rbExpress,
            rbStandard,
            rbBank,
            rbDana,
            rbGopay,
            rbShopeePay,
            rbCod;

    private RadioGroup rgPaymentCheckout;

    private Button btnBuy;

    private DbHelper dbHelper;
    private SessionManager sessionManager;

    private final List<CartItem> checkoutItems = new ArrayList<>();

    private boolean isBuyNow = false;

    private int userId = -1;

    private int subtotal = 0;
    private int shippingCost = 15000;
    private int tax = 0;
    private int total = 0;

    private int addressId = 0;
    private String fullAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initViews();
        initHelpers();

        if (userId == -1) {
            Toast.makeText(
                    this,
                    "User belum login",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        rvCheckout.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadAddress();
        loadDefaultPayment();

        isBuyNow = getIntent().getBooleanExtra("buy_now", false);

        if (isBuyNow) {
            loadBuyNowItem();
        } else {
            loadCheckoutItems();
        }

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

        rgPaymentCheckout = findViewById(R.id.rgPaymentCheckout);
        rbBank = findViewById(R.id.rbBank);
        rbDana = findViewById(R.id.rbDana);
        rbGopay = findViewById(R.id.rbGopay);
        rbShopeePay = findViewById(R.id.rbShopeePay);
        rbCod = findViewById(R.id.rbCod);

        btnBuy = findViewById(R.id.btnBuy);

        rbExpress.setChecked(true);
    }

    private void initHelpers() {
        dbHelper = new DbHelper(this);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
    }

    private void loadDefaultPayment() {
        SharedPreferences prefs = getSharedPreferences(
                PaymentMethodActivity.PREF_PAYMENT,
                MODE_PRIVATE
        );

        String defaultPayment = prefs.getString(
                PaymentMethodActivity.KEY_DEFAULT_PAYMENT,
                PaymentMethodActivity.PAYMENT_COD
        );

        if (PaymentMethodActivity.PAYMENT_BANK.equals(defaultPayment)) {
            rbBank.setChecked(true);
        } else if (PaymentMethodActivity.PAYMENT_DANA.equals(defaultPayment)) {
            rbDana.setChecked(true);
        } else if (PaymentMethodActivity.PAYMENT_GOPAY.equals(defaultPayment)) {
            rbGopay.setChecked(true);
        } else if (PaymentMethodActivity.PAYMENT_SHOPEEPAY.equals(defaultPayment)) {
            rbShopeePay.setChecked(true);
        } else {
            rbCod.setChecked(true);
        }
    }

    private String getSelectedPaymentMethod() {
        int checkedId = rgPaymentCheckout.getCheckedRadioButtonId();

        if (checkedId == R.id.rbBank) {
            return PaymentMethodActivity.PAYMENT_BANK;
        } else if (checkedId == R.id.rbDana) {
            return PaymentMethodActivity.PAYMENT_DANA;
        } else if (checkedId == R.id.rbGopay) {
            return PaymentMethodActivity.PAYMENT_GOPAY;
        } else if (checkedId == R.id.rbShopeePay) {
            return PaymentMethodActivity.PAYMENT_SHOPEEPAY;
        } else {
            return PaymentMethodActivity.PAYMENT_COD;
        }
    }

    private void loadBuyNowItem() {
        Product product = getIntent().getParcelableExtra("product");
        int qty = getIntent().getIntExtra("qty", 1);
        String type = getIntent().getStringExtra("type");

        if (product == null) {
            Toast.makeText(this, "Produk tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        CartItem item = new CartItem();
        item.setProductId(String.valueOf(product.getId()));
        item.setProductName(product.getName());
        item.setProductImage(product.getImageUrl());
        item.setProductPrice(product.getPrice());
        item.setQuantity(qty);
        item.setType(type);

        checkoutItems.clear();
        checkoutItems.add(item);

        subtotal = product.getPrice() * qty;

        CheckoutAdapter adapter = new CheckoutAdapter(this, checkoutItems);
        rvCheckout.setAdapter(adapter);

        calculateTotal();
    }

    private void loadAddress() {
        Cursor cursor = dbHelper.getDefaultAddress(userId);

        if (cursor != null && cursor.moveToFirst()) {
            addressId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));

            String recipient = cursor.getString(cursor.getColumnIndexOrThrow("recipient_name"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
            String postal = cursor.getString(cursor.getColumnIndexOrThrow("postal_code"));

            fullAddress =
                    recipient + "\n"
                            + phone + "\n"
                            + address + ", "
                            + city + " "
                            + postal;

            txtAddress.setText(fullAddress);

        } else {
            fullAddress = "Alamat demo Merchio\nJakarta, Indonesia";
            txtAddress.setText(fullAddress);
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    private void loadCheckoutItems() {
        checkoutItems.clear();
        subtotal = 0;

        Cursor cursor = dbHelper.getCheckedCartByUser(userId);

        while (cursor.moveToNext()) {
            CartItem item = new CartItem();

            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            item.setProductId(cursor.getString(cursor.getColumnIndexOrThrow("product_id")));
            item.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
            item.setProductImage(cursor.getString(cursor.getColumnIndexOrThrow("product_image")));
            item.setProductPrice(cursor.getInt(cursor.getColumnIndexOrThrow("product_price")));
            item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
            item.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));

            checkoutItems.add(item);

            subtotal += item.getProductPrice() * item.getQuantity();
        }

        cursor.close();

        CheckoutAdapter adapter = new CheckoutAdapter(this, checkoutItems);
        rvCheckout.setAdapter(adapter);

        calculateTotal();
    }

    private void setupShipping() {
        rbExpress.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                shippingCost = 15000;
                calculateTotal();
            }
        });

        rbStandard.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                shippingCost = 10000;
                calculateTotal();
            }
        });
    }

    private void calculateTotal() {
        tax = (int) (subtotal * 0.1);
        total = subtotal + shippingCost + tax;

        txtSubtotal.setText(rupiah(subtotal));
        txtShipping.setText(rupiah(shippingCost));
        txtTax.setText(rupiah(tax));
        txtTotal.setText(rupiah(total));
    }

    private void processOrder() {
        if (checkoutItems.isEmpty()) {
            Toast.makeText(
                    this,
                    "Cart kosong",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (fullAddress == null || fullAddress.trim().isEmpty()) {
            fullAddress = "Alamat demo Merchio\nJakarta, Indonesia";
        }

        String paymentMethod = getSelectedPaymentMethod();

        String shippingMethod =
                rbExpress.isChecked() ? "Express" : "Standard";

        String estimatedArrival =
                rbExpress.isChecked() ? "1-2 Days" : "4-5 Days";

        long orderId;

        if (isBuyNow) {
            CartItem item = checkoutItems.get(0);

            orderId = dbHelper.createBuyNowOrder(
                    userId,
                    item,
                    paymentMethod,
                    shippingMethod,
                    addressId,
                    fullAddress,
                    shippingCost,
                    tax,
                    estimatedArrival
            );

        } else {
            orderId = dbHelper.checkoutFromCart(
                    userId,
                    paymentMethod,
                    shippingMethod,
                    addressId,
                    fullAddress,
                    shippingCost,
                    tax,
                    estimatedArrival
            );
        }

        if (orderId != -1) {
            Intent intent = new Intent(this, OrderSuccessActivity.class);
            intent.putExtra("order_id", orderId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            finish();

        } else {
            Toast.makeText(
                    this,
                    "Order gagal, coba lagi",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private String rupiah(int amount) {
        NumberFormat format =
                NumberFormat.getCurrencyInstance(
                        new Locale("id", "ID")
                );

        return format.format(amount)
                .replace(",00", "");
    }
}