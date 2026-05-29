package com.example.merchio;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentMethodActivity extends AppCompatActivity {

    private static final String PREF_PAYMENT = "payment_pref";
    private static final String KEY_DEFAULT_PAYMENT = "default_payment";

    private TextView btnBack;
    private RadioGroup rgPayment;
    private Button btnSavePayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        btnBack = findViewById(R.id.btn_back);
        rgPayment = findViewById(R.id.rg_payment);
        btnSavePayment = findViewById(R.id.btn_save_payment);

        loadSavedPayment();

        btnBack.setOnClickListener(v -> finish());

        btnSavePayment.setOnClickListener(v -> saveDefaultPayment());
    }

    private void loadSavedPayment() {
        SharedPreferences prefs = getSharedPreferences(PREF_PAYMENT, MODE_PRIVATE);
        String payment = prefs.getString(KEY_DEFAULT_PAYMENT, "COD");

        if (payment.equals("Bank Transfer")) {
            rgPayment.check(R.id.rb_bank);
        } else if (payment.equals("DANA")) {
            rgPayment.check(R.id.rb_dana);
        } else if (payment.equals("GoPay")) {
            rgPayment.check(R.id.rb_gopay);
        } else if (payment.equals("ShopeePay")) {
            rgPayment.check(R.id.rb_shopeepay);
        } else {
            rgPayment.check(R.id.rb_cod);
        }
    }

    private void saveDefaultPayment() {
        int checkedId = rgPayment.getCheckedRadioButtonId();

        String selectedPayment;

        if (checkedId == R.id.rb_bank) {
            selectedPayment = "Bank Transfer";
        } else if (checkedId == R.id.rb_dana) {
            selectedPayment = "DANA";
        } else if (checkedId == R.id.rb_gopay) {
            selectedPayment = "GoPay";
        } else if (checkedId == R.id.rb_shopeepay) {
            selectedPayment = "ShopeePay";
        } else {
            selectedPayment = "COD";
        }

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
}