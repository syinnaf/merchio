package com.example.merchio;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentMethodActivity extends AppCompatActivity {

    public static final String PREF_PAYMENT = "payment_pref";
    public static final String KEY_DEFAULT_PAYMENT = "default_payment";

    public static final String PAYMENT_BANK = "Bank Transfer";
    public static final String PAYMENT_DANA = "DANA";
    public static final String PAYMENT_GOPAY = "GoPay";
    public static final String PAYMENT_SHOPEEPAY = "ShopeePay";
    public static final String PAYMENT_COD = "COD";

    private TextView btnBack;
    private RadioGroup rgPayment;
    private Button btnSavePayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        initViews();
        loadSavedPayment();
        setupClicks();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        rgPayment = findViewById(R.id.rg_payment);
        btnSavePayment = findViewById(R.id.btn_save_payment);
    }

    private void setupClicks() {
        btnBack.setOnClickListener(v -> finish());

        btnSavePayment.setOnClickListener(v -> saveDefaultPayment());
    }

    private void loadSavedPayment() {
        SharedPreferences prefs = getSharedPreferences(PREF_PAYMENT, MODE_PRIVATE);
        String payment = prefs.getString(KEY_DEFAULT_PAYMENT, PAYMENT_COD);

        if (PAYMENT_BANK.equals(payment)) {
            rgPayment.check(R.id.rb_bank);
        } else if (PAYMENT_DANA.equals(payment)) {
            rgPayment.check(R.id.rb_dana);
        } else if (PAYMENT_GOPAY.equals(payment)) {
            rgPayment.check(R.id.rb_gopay);
        } else if (PAYMENT_SHOPEEPAY.equals(payment)) {
            rgPayment.check(R.id.rb_shopeepay);
        } else {
            rgPayment.check(R.id.rb_cod);
        }
    }

    private void saveDefaultPayment() {
        String selectedPayment = getSelectedPayment();

        SharedPreferences prefs = getSharedPreferences(PREF_PAYMENT, MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_DEFAULT_PAYMENT, selectedPayment)
                .apply();

        Toast.makeText(
                this,
                selectedPayment + " dijadikan payment default",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    private String getSelectedPayment() {
        int checkedId = rgPayment.getCheckedRadioButtonId();

        if (checkedId == R.id.rb_bank) {
            return PAYMENT_BANK;
        } else if (checkedId == R.id.rb_dana) {
            return PAYMENT_DANA;
        } else if (checkedId == R.id.rb_gopay) {
            return PAYMENT_GOPAY;
        } else if (checkedId == R.id.rb_shopeepay) {
            return PAYMENT_SHOPEEPAY;
        } else {
            return PAYMENT_COD;
        }
    }
}