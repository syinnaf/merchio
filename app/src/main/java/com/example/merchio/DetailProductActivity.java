package com.example.merchio;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.merchio.models.Product;

public class DetailProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Product product = getIntent().getParcelableExtra("product");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 60, 40, 40);

        TextView title = new TextView(this);
        title.setTextSize(22);
        title.setText(product != null ? product.getName() : "Detail Product");

        TextView price = new TextView(this);
        price.setTextSize(18);
        price.setText(product != null ? "Rp " + product.getPrice() : "");

        root.addView(title);
        root.addView(price);

        setContentView(root);
    }
}