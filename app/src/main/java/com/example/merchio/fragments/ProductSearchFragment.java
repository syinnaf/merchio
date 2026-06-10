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
import android.widget.HorizontalScrollView;
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
import com.example.merchio.models.Category;
import com.example.merchio.models.Product;
import com.google.android.flexbox.FlexboxLayout;
import com.example.merchio.api.ApiClient;
import com.example.merchio.api.ApiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductSearchFragment extends Fragment {

    public static final String ARG_CATEGORY = "arg_category";

    // Konstanta sort didefinisikan langsung di sini
    public static final int SORT_DEFAULT    = 0;
    public static final int SORT_POPULAR    = 1;
    public static final int SORT_PRICE_ASC  = 2;
    public static final int SORT_PRICE_DESC = 3;
    public static final int SORT_NAME_AZ    = 4;

    private EditText etSearch;
    private LinearLayout layoutRecentSearches, layoutEmpty;
    private LinearLayout layoutFilterSort;
    private HorizontalScrollView scrollCategories;

    private LinearLayout llCategories;
    private LinearLayout layoutSortContainer;
    private FlexboxLayout flexboxRecent;
    private TextView btnClearAll;
    private RecyclerView rvProducts;

    private ProductAdapter productAdapter;

    private final List<Product> allProducts = new ArrayList<>();
    private final List<Product> filteredProducts = new ArrayList<>();
    private final List<Category> categories = new ArrayList<>();

    private String selectedCategory = null;
    private int selectedSort = SORT_DEFAULT;

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

        Bundle args = getArguments();
        if (args != null) {
            selectedCategory = args.getString(ARG_CATEGORY, null);
        }

        // INIT VIEW
        etSearch = view.findViewById(R.id.et_search);
        layoutRecentSearches = view.findViewById(R.id.layout_recent_searches);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        flexboxRecent = view.findViewById(R.id.flexbox_recent);
        btnClearAll = view.findViewById(R.id.btn_clear_all);
        rvProducts = view.findViewById(R.id.rv_products);
        layoutFilterSort = view.findViewById(R.id.layout_filter_sort);
        scrollCategories = view.findViewById(R.id.scroll_categories);
        llCategories = view.findViewById(R.id.ll_categories);
        layoutSortContainer = view.findViewById(R.id.layout_sort_container);

        // INIT DB & SESSION
        dbHelper = new DbHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        apiService = ApiClient.getClient().create(ApiService.class);

        // SETUP RECYCLERVIEW
        rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        rvProducts.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));

        // ADAPTER
        productAdapter = new ProductAdapter(
                requireContext(),
                filteredProducts,
                new ProductAdapter.OnProductClickListener() {
                    @Override
                    public void onProductClick(Product product) {
                        Intent intent = new Intent(requireContext(), DetailProductActivity.class);
                        intent.putExtra("product", product);
                        startActivity(intent);
                    }

                    @Override
                    public void onAddToCartClick(Product product) {
                        if (userId == -1) {
                            Toast.makeText(requireContext(), "Silakan login dulu", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (product == null) {
                            Toast.makeText(requireContext(), "Produk tidak valid", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (product.getStock() <= 0) {
                            Toast.makeText(requireContext(), "Stok produk habis", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ProductActionBottomSheetDialogFragment
                                .newInstance(product, ProductActionBottomSheetDialogFragment.MODE_ADD_TO_CART)
                                .show(getParentFragmentManager(), "product_action");
                    }
                }
        );

        rvProducts.setAdapter(productAdapter);

        buildSortChips();

        // Jika ada kategori dari home, langsung tampilkan bar filter
        if (selectedCategory != null) {
            layoutFilterSort.setVisibility(View.VISIBLE);
        }

        fetchCategories();
        showRecentSearches();

        // SEARCH ACTION
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String keyword = etSearch.getText().toString().trim();
                if (!keyword.isEmpty()) {
                    dbHelper.addRecentSearch(userId, keyword);
                }
                applyFilters();
                return true;
            }
            return false;
        });

        // REALTIME SEARCH
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                if (keyword.isEmpty()) {
                    showRecentSearches();
                } else {
                    layoutRecentSearches.setVisibility(View.GONE);
                }
                applyFilters();
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

    private void fetchCategories() {
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body());
                }
                buildCategoryChips();
                fetchProducts();
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                if (!isAdded()) return;
                buildCategoryChips();
                fetchProducts();
            }
        });
    }

    private void fetchProducts() {
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    allProducts.clear();
                    allProducts.addAll(response.body());
                    applyFilters();
                } else {
                    Toast.makeText(requireContext(), "Produk gagal dimuat", Toast.LENGTH_SHORT).show();
                    checkEmpty();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Error API: " + t.getMessage(), Toast.LENGTH_LONG).show();
                checkEmpty();
            }
        });
    }

    private void buildCategoryChips() {
        llCategories.removeAllViews();
        addCategoryChip("Semua", null);
        for (Category cat : categories) {
            addCategoryChip(cat.getName(), cat.getName());
        }
    }

    private void addCategoryChip(String label, String categoryValue) {
        TextView chip = new TextView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 12, 0);
        chip.setLayoutParams(params);
        chip.setText(label);
        chip.setTextSize(13f);
        chip.setPadding(36, 16, 36, 16);
        chip.setTag(categoryValue);

        updateCategoryChipStyle(chip,
                (categoryValue == null && selectedCategory == null)
                        || (categoryValue != null && categoryValue.equals(selectedCategory))
        );

        chip.setOnClickListener(v -> {
            selectedCategory = (String) chip.getTag();
            for (int i = 0; i < llCategories.getChildCount(); i++) {
                View child = llCategories.getChildAt(i);
                if (child instanceof TextView) {
                    String tag = (String) child.getTag();
                    boolean isSelected = (tag == null && selectedCategory == null)
                            || (tag != null && tag.equals(selectedCategory));
                    updateCategoryChipStyle((TextView) child, isSelected);
                }
            }
            applyFilters();
        });

        llCategories.addView(chip);
    }

    private void updateCategoryChipStyle(TextView chip, boolean selected) {
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(60f);
        if (selected) {
            bg.setColor(0xFF8B6DFF);
            chip.setTextColor(0xFFFFFFFF);
        } else {
            bg.setColor(0xFFF4F1FF);
            chip.setTextColor(0xFF555555);
        }
        chip.setBackground(bg);
    }

    private void buildSortChips() {
        layoutSortContainer.removeAllViews();
        String[] sortLabels = {"Default", "Terlaris", "Termurah", "Termahal", "A-Z"};
        int[]    sortValues = {SORT_DEFAULT, SORT_POPULAR, SORT_PRICE_ASC, SORT_PRICE_DESC, SORT_NAME_AZ};

        for (int i = 0; i < sortLabels.length; i++) {
            final int sortVal = sortValues[i];
            TextView chip = new TextView(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 10, 0);
            chip.setLayoutParams(params);
            chip.setText(sortLabels[i]);
            chip.setTextSize(12f);
            chip.setPadding(28, 12, 28, 12);
            chip.setTag(sortVal);
            updateSortChipStyle(chip, sortVal == selectedSort);

            chip.setOnClickListener(v -> {
                selectedSort = sortVal;
                for (int j = 0; j < layoutSortContainer.getChildCount(); j++) {
                    View child = layoutSortContainer.getChildAt(j);
                    if (child instanceof TextView) {
                        int tag = (int) child.getTag();
                        updateSortChipStyle((TextView) child, tag == selectedSort);
                    }
                }
                applyFilters();
            });

            layoutSortContainer.addView(chip);
        }
    }

    private void updateSortChipStyle(TextView chip, boolean selected) {
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(50f);
        if (selected) {
            bg.setColor(0xFF2457FF);
            chip.setTextColor(0xFFFFFFFF);
        } else {
            bg.setColor(0xFFEEF0FF);
            chip.setTextColor(0xFF555577);
        }
        chip.setBackground(bg);
    }

    private void applyFilters() {
        String keyword = etSearch != null
                ? etSearch.getText().toString().trim().toLowerCase()
                : "";

        filteredProducts.clear();

        for (Product p : allProducts) {
            if (selectedCategory != null
                    && !selectedCategory.equalsIgnoreCase(safeLower(p.getCategoryName()))) {
                continue;
            }
            if (!keyword.isEmpty()) {
                boolean match = safeLower(p.getName()).contains(keyword)
                        || safeLower(p.getCategoryName()).contains(keyword)
                        || safeLower(p.getBrand()).contains(keyword);
                if (!match) continue;
            }
            filteredProducts.add(p);
        }

        switch (selectedSort) {
            case SORT_PRICE_ASC:
                Collections.sort(filteredProducts, (a, b) -> Integer.compare(a.getPrice(), b.getPrice()));
                break;
            case SORT_PRICE_DESC:
                Collections.sort(filteredProducts, (a, b) -> Integer.compare(b.getPrice(), a.getPrice()));
                break;
            case SORT_POPULAR:
                Collections.sort(filteredProducts, (a, b) -> Integer.compare(b.getSoldCount(), a.getSoldCount()));
                break;
            case SORT_NAME_AZ:
                Collections.sort(filteredProducts, (a, b) -> safeLower(a.getName()).compareTo(safeLower(b.getName())));
                break;
            default:
                break;
        }

        productAdapter.updateList(filteredProducts);
        checkEmpty();
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private void checkEmpty() {
        if (filteredProducts.isEmpty()) {
            rvProducts.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvProducts.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void showRecentSearches() {
        Cursor cursor = dbHelper.getRecentSearches(userId);
        flexboxRecent.removeAllViews();
        if (cursor != null && cursor.getCount() > 0) {
            layoutRecentSearches.setVisibility(View.VISIBLE);
            while (cursor.moveToNext()) {
                int searchId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String keyword = cursor.getString(cursor.getColumnIndexOrThrow("keyword"));
                addRecentChip(searchId, keyword);
            }
            cursor.close();
        } else {
            layoutRecentSearches.setVisibility(View.GONE);
        }
    }

    private void addRecentChip(int searchId, String keyword) {
        TextView chip = new TextView(requireContext());
        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
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

        chip.setOnClickListener(v -> {
            etSearch.setText(keyword);
            etSearch.setSelection(keyword.length());
            layoutRecentSearches.setVisibility(View.GONE);
            applyFilters();
        });

        flexboxRecent.addView(chip);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (etSearch != null && etSearch.getText().toString().isEmpty()) {
            showRecentSearches();
        }
    }
}