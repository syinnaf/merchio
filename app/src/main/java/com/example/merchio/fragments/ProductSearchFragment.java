package com.example.merchio.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchio.DetailProductActivity;
import com.example.merchio.R;
import com.example.merchio.SessionManager;
import com.example.merchio.adapters.ProductAdapter;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.Product;
import com.google.android.flexbox.FlexboxLayout;
import com.example.merchio.api.ApiClient;
import com.example.merchio.api.ApiService;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductSearchFragment extends Fragment {

    private EditText etSearch;
    private LinearLayout layoutRecentSearches, layoutEmpty;
    private FlexboxLayout flexboxRecent;
    private TextView btnClearAll;
    private RecyclerView rvProducts;

    private ProductAdapter productAdapter;

    private final List<Product> allProducts = new ArrayList<>();
    private final List<Product> filteredProducts = new ArrayList<>();

    private DbHelper dbHelper;
    private SessionManager sessionManager;
    private ApiService apiService;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // INIT VIEW
        etSearch = view.findViewById(R.id.et_search);
        layoutRecentSearches = view.findViewById(R.id.layout_recent_searches);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        flexboxRecent = view.findViewById(R.id.flexbox_recent);
        btnClearAll = view.findViewById(R.id.btn_clear_all);
        rvProducts = view.findViewById(R.id.rv_products);

        // INIT DB & SESSION
        dbHelper = new DbHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        apiService = ApiClient
                .getClient()
                .create(ApiService.class);

        // SETUP RECYCLERVIEW
        rvProducts.setLayoutManager(
                new GridLayoutManager(
                        requireContext(),
                        2
                )
        );

        int spacing = getResources()
                .getDimensionPixelSize(R.dimen.grid_spacing);

        rvProducts.addItemDecoration(
                new GridSpacingItemDecoration(
                        2,
                        spacing,
                        true
                )
        );

        // ADAPTER
        productAdapter = new ProductAdapter(
                requireContext(),
                filteredProducts,
                new ProductAdapter.OnProductClickListener() {

                    @Override
                    public void onProductClick(Product product) {

                        Intent intent = new Intent(
                                requireContext(),
                                DetailProductActivity.class
                        );

                        intent.putExtra("product", product);

                        startActivity(intent);
                    }

                    @Override
                    public void onAddToCartClick(Product product) {

                        if (userId == -1) {
                            Toast.makeText(
                                    requireContext(),
                                    "Silakan login dulu",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        if (product == null) {
                            Toast.makeText(
                                    requireContext(),
                                    "Produk tidak valid",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        if (product.getStock() <= 0) {
                            Toast.makeText(
                                    requireContext(),
                                    "Stok produk habis",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        ProductActionBottomSheetDialogFragment
                                .newInstance(
                                        product,
                                        ProductActionBottomSheetDialogFragment.MODE_ADD_TO_CART
                                )
                                .show(
                                        getParentFragmentManager(),
                                        "product_action"
                                );
                    }
                }
        );

        rvProducts.setAdapter(productAdapter);

        fetchProducts();

        // TAMPILKAN RECENT SEARCH
        showRecentSearches();

        // SEARCH ACTION
        etSearch.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                String keyword = etSearch.getText().toString().trim();

                if (!keyword.isEmpty()) {
                    dbHelper.addRecentSearch(userId, keyword);
                }

                filterProducts(keyword);

                return true;
            }

            return false;
        });

        // REALTIME SEARCH
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {}

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {

                String keyword = s.toString().trim();

                if (keyword.isEmpty()) {

                    showRecentSearches();

                    filterProducts("");

                } else {

                    layoutRecentSearches.setVisibility(View.GONE);

                    filterProducts(keyword);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // CLEAR RECENT SEARCH
        btnClearAll.setOnClickListener(v -> {

            dbHelper.clearRecentSearches(userId);

            flexboxRecent.removeAllViews();

            layoutRecentSearches.setVisibility(View.GONE);
        });
    }

    // =========================================================
    // FETCH PRODUCTS
    // =========================================================
    private void fetchProducts() {
        apiService.getProducts().enqueue(new Callback<List<Product>>() {

            @Override
            public void onResponse(
                    Call<List<Product>> call,
                    Response<List<Product>> response
            ) {
                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Product> apiProducts = response.body();

                    allProducts.clear();
                    allProducts.addAll(apiProducts);

                    filteredProducts.clear();
                    filteredProducts.addAll(apiProducts);

                    productAdapter.updateList(filteredProducts);

                    checkEmpty();

                } else {
                    Toast.makeText(
                            requireContext(),
                            "Produk gagal dimuat",
                            Toast.LENGTH_SHORT
                    ).show();

                    checkEmpty();
                }
            }

            @Override
            public void onFailure(
                    Call<List<Product>> call,
                    Throwable t
            ) {
                if (!isAdded()) {
                    return;
                }

                Toast.makeText(
                        requireContext(),
                        "Error API: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

                checkEmpty();
            }
        });
    }

    // =========================================================
    // FILTER PRODUCTS
    // =========================================================

    private void filterProducts(String keyword) {

        filteredProducts.clear();

        if (keyword.isEmpty()) {

            filteredProducts.addAll(allProducts);

        } else {

            String lower = keyword.toLowerCase();

            for (Product p : allProducts) {

                if (safeLower(p.getName()).contains(lower)
                        || safeLower(p.getCategoryName()).contains(lower)
                        || safeLower(p.getBrand()).contains(lower)) {

                    filteredProducts.add(p);

                }
            }
        }

        productAdapter.updateList(filteredProducts);

        checkEmpty();
    }

    private String safeLower(String value) {
        if (value == null) {
            return "";
        }

        return value.toLowerCase();
    }

    // =========================================================
    // EMPTY STATE
    // =========================================================

    private void checkEmpty() {

        if (filteredProducts.isEmpty()) {

            rvProducts.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);

        } else {

            rvProducts.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    // =========================================================
    // RECENT SEARCH
    // =========================================================

    private void showRecentSearches() {

        Cursor cursor = dbHelper.getRecentSearches(userId);

        flexboxRecent.removeAllViews();

        if (cursor != null && cursor.getCount() > 0) {

            layoutRecentSearches.setVisibility(View.VISIBLE);

            while (cursor.moveToNext()) {

                int searchId = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String keyword = cursor.getString(
                        cursor.getColumnIndexOrThrow("keyword")
                );

                addRecentChip(searchId, keyword);
            }

            cursor.close();

        } else {

            layoutRecentSearches.setVisibility(View.GONE);
        }
    }

    // =========================================================
    // RECENT CHIP
    // =========================================================

    private void addRecentChip(int searchId, String keyword) {

        TextView chip = new TextView(requireContext());

        FlexboxLayout.LayoutParams params =
                new FlexboxLayout.LayoutParams(
                        FlexboxLayout.LayoutParams.WRAP_CONTENT,
                        FlexboxLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 12, 12);

        chip.setLayoutParams(params);

        chip.setText("🕐 " + keyword);

        chip.setTextSize(13f);

        chip.setTextColor(0xFF555555);

        chip.setPadding(36, 16, 36, 16);

        GradientDrawable bg = new GradientDrawable();

        bg.setShape(GradientDrawable.RECTANGLE);

        bg.setCornerRadius(50f);

        bg.setColor(0xFFF4F1FF);

        chip.setBackground(bg);

        // CLICK CHIP
        chip.setOnClickListener(v -> {

            etSearch.setText(keyword);

            etSearch.setSelection(keyword.length());

            layoutRecentSearches.setVisibility(View.GONE);

            filterProducts(keyword);
        });

        flexboxRecent.addView(chip);
    }

    // =========================================================
    // REFRESH RECENT SEARCH
    // =========================================================

    @Override
    public void onResume() {

        super.onResume();

        if (etSearch != null
                && etSearch.getText().toString().isEmpty()) {

            showRecentSearches();
        }
    }
}