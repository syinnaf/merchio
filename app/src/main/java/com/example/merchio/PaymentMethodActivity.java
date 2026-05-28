package com.example.merchio;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentMethodActivity extends AppCompatActivity {

    private TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
    }
}