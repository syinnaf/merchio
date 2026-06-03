package com.example.merchio.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.merchio.R;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.Product;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.merchio.SessionManager;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductActionBottomSheetDialogFragment
        extends BottomSheetDialogFragment {

    public static final int MODE_ADD_TO_CART = 0;
    public static final int MODE_BUY_NOW = 1;

    private static final String KEY_PRODUCT = "product";
    private static final String KEY_MODE = "mode";

    private Product product;
    private int mode;

    private int quantity = 1;
    private String selectedType = "A";

    private Listener listener;

    public interface Listener {
        void onBuyNowRequested(
                Product product,
                String selectedType,
                int quantity
        );
    }

    public static ProductActionBottomSheetDialogFragment newInstance(
            Product product,
            int mode
    ) {

        ProductActionBottomSheetDialogFragment fragment =
                new ProductActionBottomSheetDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable(KEY_PRODUCT, product);
        args.putInt(KEY_MODE, mode);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Listener) {
            listener = (Listener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            product = args.getParcelable(KEY_PRODUCT);
            mode = args.getInt(KEY_MODE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.bottom_sheet_product_action,
                container,
                false
        );

        ImageView imgPreview = view.findViewById(R.id.imgPreview);

        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvPrice = view.findViewById(R.id.tvPrice);
        TextView tvStock = view.findViewById(R.id.tvStock);
        TextView tvQty = view.findViewById(R.id.tvQty);

        Button btnTypeA = view.findViewById(R.id.btnTypeA);
        Button btnTypeB = view.findViewById(R.id.btnTypeB);
        Button btnTypeC = view.findViewById(R.id.btnTypeC);
        Button btnTypeD = view.findViewById(R.id.btnTypeD);

        Button btnMinus = view.findViewById(R.id.btnMinus);
        Button btnPlus = view.findViewById(R.id.btnPlus);

        Button btnPrimary = view.findViewById(R.id.btnPrimary);

        if (product == null) {
            dismiss();
            return view;
        }

        tvName.setText(product.getName());
        tvPrice.setText(formatRupiah(product.getPrice()));
        tvStock.setText("Stock Left : " + product.getStock());

        Glide.with(requireContext())
                .load(product.getImageUrl())
                .into(imgPreview);

        if (!TextUtils.isEmpty(product.getType())) {
            selectedType = product.getType();
        }

        btnTypeA.setOnClickListener(v -> selectedType = "A");
        btnTypeB.setOnClickListener(v -> selectedType = "B");
        btnTypeC.setOnClickListener(v -> selectedType = "C");
        btnTypeD.setOnClickListener(v -> selectedType = "D");

        btnMinus.setOnClickListener(v -> {

            if (quantity > 1) {
                quantity--;
                tvQty.setText(String.valueOf(quantity));
            }
        });

        btnPlus.setOnClickListener(v -> {

            if (quantity < product.getStock()) {
                quantity++;
                tvQty.setText(String.valueOf(quantity));
            }
        });

        if (mode == MODE_ADD_TO_CART) {
            btnPrimary.setText("Add To Cart");
        } else {
            btnPrimary.setText("Buy Now");
        }

        btnPrimary.setOnClickListener(v -> {

            if (mode == MODE_ADD_TO_CART) {

                DbHelper dbHelper =
                        new DbHelper(requireContext());

                SessionManager sessionManager =
                        new SessionManager(requireContext());

                int userId = sessionManager.getUserId();

                if (userId == -1) {
                    Toast.makeText(
                            requireContext(),
                            "Silakan login dulu",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                boolean success = dbHelper.addToCart(
                        userId,
                        product.getId(),
                        product.getName(),
                        product.getImageUrl(),
                        product.getPrice(),
                        selectedType,
                        quantity,
                        product.getStock()
                );

                if (success) {
                    Toast.makeText(
                            requireContext(),
                            "Added to cart",
                            Toast.LENGTH_SHORT
                    ).show();

                    dismiss();

                } else {

                    Toast.makeText(
                            requireContext(),
                            "Failed to add cart",
                            Toast.LENGTH_SHORT
                    ).show();
                }

            } else {

                if (listener != null) {

                    listener.onBuyNowRequested(
                            product,
                            selectedType,
                            quantity
                    );
                }

                dismiss();
            }
        });

        return view;
    }

    private String formatRupiah(int price) {

        NumberFormat formatter =
                NumberFormat.getCurrencyInstance(
                        new Locale("id", "ID")
                );

        return formatter.format(price)
                .replace(",00", "");
    }
}