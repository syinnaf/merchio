package com.example.merchio.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchio.R;
import com.example.merchio.SessionManager;
import com.example.merchio.adapters.CartAdapter;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.CartItem;
import com.example.merchio.CheckoutActivity;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment {

    private TextView tvCartTitle, tvTotal, tvEmptyCart;
    private CheckBox cbSelectAll;
    private RecyclerView rvCart;
    private MaterialButton btnCheckout;

    private DbHelper dbHelper;
    private SessionManager sessionManager;
    private CartAdapter cartAdapter;

    private int userId = -1;
    private boolean isProgrammaticCheck = false;

    public CartFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DbHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        loadCartItems();
    }

    private void initViews(View view) {
        tvCartTitle = view.findViewById(R.id.tv_cart_title);
        tvTotal = view.findViewById(R.id.tv_total);
        tvEmptyCart = view.findViewById(R.id.tv_empty_cart);
        cbSelectAll = view.findViewById(R.id.cb_select_all);
        rvCart = view.findViewById(R.id.rv_cart);
        btnCheckout = view.findViewById(R.id.btn_checkout);
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(new CartAdapter.OnCartActionListener() {
            @Override
            public void onCheckedChanged(CartItem item, boolean isChecked) {
                dbHelper.updateCartChecked(item.getId(), isChecked);
                item.setChecked(isChecked);
                updateTotalAndSelectAllState();
            }

            @Override
            public void onIncreaseQuantity(CartItem item) {
                int newQuantity = item.getQuantity() + 1;

                if (newQuantity > item.getStock()) {
                    Toast.makeText(requireContext(), "Stok tidak cukup", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbHelper.updateCartQuantity(item.getId(), newQuantity);
                loadCartItems();
            }

            @Override
            public void onDecreaseQuantity(CartItem item) {
                int newQuantity = item.getQuantity() - 1;

                if (newQuantity < 1) {
                    Toast.makeText(requireContext(), "Minimal quantity 1", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbHelper.updateCartQuantity(item.getId(), newQuantity);
                loadCartItems();
            }

            @Override
            public void onDeleteItem(CartItem item) {
                dbHelper.deleteCartItem(item.getId());
                Toast.makeText(requireContext(), "Item dihapus", Toast.LENGTH_SHORT).show();
                loadCartItems();
            }
        });

        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCart.setAdapter(cartAdapter);
    }

    private void setupClickListeners() {
        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isProgrammaticCheck) {
                return;
            }

            dbHelper.updateAllCartChecked(userId, isChecked);
            loadCartItems();
        });

        btnCheckout.setOnClickListener(v -> {
            int selectedCount = dbHelper.getCheckedCartCount(userId);

            if (selectedCount == 0) {
                Toast.makeText(requireContext(), "Pilih item dulu sebelum checkout", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(requireContext(), CheckoutActivity.class);

            startActivity(intent);
        });
    }

    private void loadCartItems() {
        if (userId == -1) {
            Toast.makeText(requireContext(), "User belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        List<CartItem> items = new ArrayList<>();

        Cursor cursor = dbHelper.getCartItemsByUserId(userId);

        while (cursor.moveToNext()) {
            CartItem item = new CartItem();

            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            item.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
            item.setProductId(cursor.getString(cursor.getColumnIndexOrThrow("product_id")));
            item.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
            item.setProductImage(cursor.getString(cursor.getColumnIndexOrThrow("product_image")));
            item.setProductPrice(cursor.getInt(cursor.getColumnIndexOrThrow("product_price")));
            item.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
            item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
            item.setStock(cursor.getInt(cursor.getColumnIndexOrThrow("stock")));
            item.setIsChecked(cursor.getInt(cursor.getColumnIndexOrThrow("is_checked")));
            item.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));

            items.add(item);
        }

        cursor.close();

        cartAdapter.setCartItems(items);
        tvCartTitle.setText("Cart (" + items.size() + ")");

        if (items.isEmpty()) {
            rvCart.setVisibility(View.GONE);
            tvEmptyCart.setVisibility(View.VISIBLE);
        } else {
            rvCart.setVisibility(View.VISIBLE);
            tvEmptyCart.setVisibility(View.GONE);
        }

        updateTotalAndSelectAllState();
    }

    private void updateTotalAndSelectAllState() {
        int total = dbHelper.getCheckedCartTotal(userId);
        int checkedCount = dbHelper.getCheckedCartCount(userId);

        tvTotal.setText(formatRupiah(total));

        Cursor cursor = dbHelper.getCartItemsByUserId(userId);
        int cartCount = cursor.getCount();
        cursor.close();

        isProgrammaticCheck = true;
        cbSelectAll.setChecked(cartCount > 0 && checkedCount == cartCount);
        isProgrammaticCheck = false;
    }

    private String formatRupiah(int value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(value).replace(",00", "");
    }
}