package com.example.merchio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CustomerServiceActivity extends AppCompatActivity {

    private static final String CUSTOMER_SERVICE_PHONE = "081234567890";

    private TextView btnBack;
    private Button btnCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);

        btnBack = findViewById(R.id.btn_back);
        btnCall = findViewById(R.id.btn_call);

        btnBack.setOnClickListener(v -> finish());

        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + CUSTOMER_SERVICE_PHONE));
            startActivity(intent);
        });
    }
}