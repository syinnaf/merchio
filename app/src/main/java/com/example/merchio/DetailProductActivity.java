package com.example.merchio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.merchio.fragments.ProductActionBottomSheetDialogFragment;
import com.example.merchio.models.Product;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailProductActivity extends AppCompatActivity
        implements ProductActionBottomSheetDialogFragment.Listener {

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        product = getIntent().getParcelableExtra("product");

        if (product == null) {
            finish();
            return;
        }

        ImageView imgProduct = findViewById(R.id.imgProduct);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvType = findViewById(R.id.tvType);
        TextView tvDescription = findViewById(R.id.tvDescription);

        ImageButton btnCart = findViewById(R.id.btnCart);
        Button btnBuyNow = findViewById(R.id.btnBuyNow);
        TextView btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        tvName.setText(product.getName());
        tvPrice.setText(formatRupiah(product.getPrice()));
        tvType.setText("Type: " + (product.getType() != null ? product.getType() : "-"));
        tvDescription.setText(product.getDescription());

        Glide.with(this)
                .load(product.getImageUrl())
                .into(imgProduct);

        btnCart.setOnClickListener(v -> {
            ProductActionBottomSheetDialogFragment
                    .newInstance(product, ProductActionBottomSheetDialogFragment.MODE_ADD_TO_CART)
                    .show(getSupportFragmentManager(), "product_action_add");
        });

        btnBuyNow.setOnClickListener(v -> {
            ProductActionBottomSheetDialogFragment
                    .newInstance(product, ProductActionBottomSheetDialogFragment.MODE_BUY_NOW)
                    .show(getSupportFragmentManager(), "product_action_buy");
        });
    }

    @Override
    public void onBuyNowRequested(Product product,
                                  String selectedType,
                                  int quantity) {

        Intent intent =
                new Intent(
                        this,
                        CheckoutActivity.class
                );

        intent.putExtra(
                "buy_now",
                true
        );

        intent.putExtra(
                "product",
                product
        );

        intent.putExtra(
                "qty",
                quantity
        );

        intent.putExtra(
                "type",
                selectedType
        );

        startActivity(intent);
    }

    private String formatRupiah(int price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(price).replace(",00", "");
    }
}