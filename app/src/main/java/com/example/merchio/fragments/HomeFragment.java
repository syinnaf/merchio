package com.example.merchio.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.merchio.DetailProductActivity;
import com.example.merchio.MainActivity;
import com.example.merchio.R;
import com.example.merchio.SessionManager;
import com.example.merchio.adapters.BannerAdapter;
import com.example.merchio.adapters.CategoryAdapter;
import com.example.merchio.adapters.ProductAdapter;
import com.example.merchio.api.ApiClient;
import com.example.merchio.api.ApiService;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.Banner;
import com.example.merchio.models.Category;
import com.example.merchio.models.Product;
import com.example.merchio.receivers.NetworkReceiver;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment
        implements ProductAdapter.OnProductClickListener,
        CategoryAdapter.OnCategoryClickListener,
        BannerAdapter.OnBannerClickListener,
        NetworkReceiver.NetworkListener {

    private RecyclerView rvCategory, rvPopular;
    private ViewPager2 bannerViewPager;

    private ApiService apiService;
    private DbHelper dbHelper;
    private SessionManager sessionManager;
    private NetworkReceiver networkReceiver;

    private int userId = -1;
    private final List<Product> productList = new ArrayList<>();
    private final List<Category> categoryList = new ArrayList<>();

    private EditText etSearch;
    private ImageButton btnFilter;
    private View viewFilterBadge;
    private HorizontalScrollView scrollActiveFilters;
    private LinearLayout llActiveFilters;
    private TextView tvProductCount;
    private TextView tvGreeting;

    private String activeCategory = null;
    private int    activeSort     = FilterBottomSheetFragment.SORT_DEFAULT;

    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private int bannerSize = 0;
    private static final long BANNER_DELAY_MS = 3000L;

    private final Runnable bannerRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerSize == 0 || bannerViewPager == null) return;
            int next = (bannerViewPager.getCurrentItem() + 1) % bannerSize;
            bannerViewPager.setCurrentItem(next, true);
            bannerHandler.postDelayed(this, BANNER_DELAY_MS);
        }
    };

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        initHelpers();
        setupRecyclerViews();
        setupSearch();
        setupFilterButton();
        setupNetworkReceiver();
        fetchCategories();
        fetchProducts();

        return view;
    }

    private void initViews(View view) {
        rvCategory = view.findViewById(R.id.rvCategory);
        rvPopular = view.findViewById(R.id.rvPopular);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        etSearch = view.findViewById(R.id.etSearch);
        btnFilter = view.findViewById(R.id.btnFilter);
        viewFilterBadge = view.findViewById(R.id.viewFilterBadge);
        scrollActiveFilters = view.findViewById(R.id.scroll_active_filters);
        llActiveFilters = view.findViewById(R.id.ll_active_filters);
        tvProductCount = view.findViewById(R.id.tvProductCount);
        tvGreeting = view.findViewById(R.id.tvGreeting);
    }

    private void initHelpers() {
        dbHelper = new DbHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();
        apiService = ApiClient.getClient().create(ApiService.class);
        setGreeting();
    }

    private void setGreeting() {
        if (tvGreeting == null) return;
        android.database.Cursor cursor = dbHelper.getUserById(userId);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIdx = cursor.getColumnIndex("name");
            String name = (nameIdx != -1) ? cursor.getString(nameIdx) : null;
            cursor.close();
            if (name != null && !name.isEmpty()) {
                tvGreeting.setText("Hello, " + name + "!");
                return;
            }
        }
        if (cursor != null) cursor.close();
        // Fallback ke email kalau name kosong
        String email = sessionManager.getEmail();
        if (email != null && !email.isEmpty()) {
            String fallback = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
            tvGreeting.setText("Hello, " + fallback + "!");
        }
    }

    private void setupRecyclerViews() {
        rvCategory.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPopular.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        rvPopular.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
        rvPopular.setNestedScrollingEnabled(false);
        rvCategory.setNestedScrollingEnabled(false);
    }

    private void setupSearch() {
        etSearch.setFocusable(false);
        etSearch.setClickable(true);
        etSearch.setOnClickListener(v -> navigateToSearch(null));
    }

    private void setupFilterButton() {
        btnFilter.setOnClickListener(v -> openFilterSheet());
    }

    private void openFilterSheet() {
        FilterBottomSheetFragment sheet = FilterBottomSheetFragment.newInstance(
                categoryList, activeCategory, activeSort);

        sheet.setFilterListener((category, sort) -> {
            activeCategory = category;
            activeSort     = sort;
            navigateToSearchWithFilters(activeCategory, activeSort);
        });

        sheet.show(getParentFragmentManager(), "filter_sheet");
    }

    private void applyFiltersToPopular() {
        if (productList.isEmpty()) return;

        List<Product> result = new ArrayList<>();

        for (Product p : productList) {
            if (activeCategory != null
                    && !activeCategory.equalsIgnoreCase(safeLower(p.getCategoryName()))) continue;
            result.add(p);
        }

        switch (activeSort) {
            case FilterBottomSheetFragment.SORT_PRICE_ASC:
                Collections.sort(result, (a, b) -> Integer.compare(a.getPrice(), b.getPrice()));
                break;
            case FilterBottomSheetFragment.SORT_PRICE_DESC:
                Collections.sort(result, (a, b) -> Integer.compare(b.getPrice(), a.getPrice()));
                break;
            case FilterBottomSheetFragment.SORT_POPULAR:
                Collections.sort(result, (a, b) -> Integer.compare(b.getSoldCount(), a.getSoldCount()));
                break;
            case FilterBottomSheetFragment.SORT_NAME_AZ:
                Collections.sort(result, (a, b) -> safeLower(a.getName()).compareTo(safeLower(b.getName())));
                break;
            default:
                Collections.sort(result, (a, b) -> Integer.compare(b.getSoldCount(), a.getSoldCount()));
                break;
        }

        boolean hasActiveFilter = activeCategory != null
                || activeSort != FilterBottomSheetFragment.SORT_DEFAULT;

        List<Product> display = hasActiveFilter
                ? result
                : result.subList(0, Math.min(10, result.size()));

        ProductAdapter adapter = new ProductAdapter(requireContext(), display, this);
        rvPopular.setAdapter(adapter);

        if (tvProductCount != null) {
            if (hasActiveFilter) {
                tvProductCount.setVisibility(View.VISIBLE);
                tvProductCount.setText(display.size() + " produk");
            } else {
                tvProductCount.setVisibility(View.GONE);
            }
        }
    }

    private void updateActiveFilterBar() {
        llActiveFilters.removeAllViews();
        boolean hasFilter = false;

        if (activeCategory != null) {
            addFilterChip("📂 " + activeCategory, () -> {
                activeCategory = null;
                applyFiltersToPopular();
                updateActiveFilterBar();
            });
            hasFilter = true;
        }

        if (activeSort != FilterBottomSheetFragment.SORT_DEFAULT) {
            addFilterChip("↕ " + sortLabel(activeSort), () -> {
                activeSort = FilterBottomSheetFragment.SORT_DEFAULT;
                applyFiltersToPopular();
                updateActiveFilterBar();
            });
            hasFilter = true;
        }

        scrollActiveFilters.setVisibility(hasFilter ? View.VISIBLE : View.GONE);
        viewFilterBadge.setVisibility(hasFilter ? View.VISIBLE : View.GONE);
    }

    private void addFilterChip(String label, Runnable onRemove) {
        LinearLayout chip = new LinearLayout(requireContext());
        chip.setOrientation(LinearLayout.HORIZONTAL);
        chip.setGravity(android.view.Gravity.CENTER_VERTICAL);
        chip.setPadding(24, 10, 18, 10);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 8, 0);
        chip.setLayoutParams(p);

        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(50f);
        bg.setColor(0xFFEDE9FF);
        chip.setBackground(bg);

        TextView tvLabel = new TextView(requireContext());
        tvLabel.setText(label);
        tvLabel.setTextSize(12f);
        tvLabel.setTextColor(0xFF5B3FCC);
        chip.addView(tvLabel);

        TextView tvClose = new TextView(requireContext());
        tvClose.setText("  ✕");
        tvClose.setTextSize(12f);
        tvClose.setTextColor(0xFF9999CC);
        chip.addView(tvClose);

        chip.setOnClickListener(v -> onRemove.run());
        llActiveFilters.addView(chip);
    }

    private void navigateToSearch(String categoryName) {
        ProductSearchFragment searchFragment = new ProductSearchFragment();
        if (categoryName != null) {
            Bundle args = new Bundle();
            args.putString(ProductSearchFragment.ARG_CATEGORY, categoryName);
            searchFragment.setArguments(args);
        }
        ((MainActivity) requireActivity()).navigateWithFragment(searchFragment, R.id.nav_search);
    }

    private void navigateToSearchWithFilters(String category, int sort) {
        ProductSearchFragment searchFragment = new ProductSearchFragment();
        Bundle args = new Bundle();
        if (category != null) {
            args.putString(ProductSearchFragment.ARG_CATEGORY, category);
        }
        args.putInt(ProductSearchFragment.ARG_SORT, sort);
        searchFragment.setArguments(args);
        ((MainActivity) requireActivity()).navigateWithFragment(searchFragment, R.id.nav_search);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerSize > 1) bannerHandler.postDelayed(bannerRunnable, BANNER_DELAY_MS);
    }

    @Override
    public void onPause() {
        super.onPause();
        bannerHandler.removeCallbacks(bannerRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bannerHandler.removeCallbacks(bannerRunnable);
        if (networkReceiver != null && getActivity() != null) {
            requireActivity().unregisterReceiver(networkReceiver);
            networkReceiver = null;
        }
    }

    private void fetchCategories() {
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    CategoryAdapter adapter = new CategoryAdapter(requireContext(), categoryList, HomeFragment.this);
                    rvCategory.setAdapter(adapter);
                } else {
                    showToast("Gagal memuat kategori");
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                if (!isAdded()) return;
                showToast("Error kategori: " + t.getMessage());
            }
        });
    }

    private void fetchProducts() {
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    applyFiltersToPopular();
                    fetchBanners();
                } else {
                    showToast("Gagal memuat produk");
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (!isAdded()) return;
                showToast("Error produk: " + t.getMessage());
            }
        });
    }

    private void fetchBanners() {
        apiService.getBanners().enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    List<Banner> bannerList = response.body();
                    for (Banner banner : bannerList) {
                        if ("best_seller".equals(banner.getType())) {
                            Product p = getBestSellerProduct();
                            if (p != null) { banner.setImageUrl(p.getImageUrl()); banner.setSubtitle(p.getName()); }
                        } else if ("limited".equals(banner.getType())) {
                            Product p = getLimitedProduct();
                            if (p != null) { banner.setImageUrl(p.getImageUrl()); banner.setSubtitle(p.getName()); }
                        }
                    }
                    BannerAdapter adapter = new BannerAdapter(requireContext(), bannerList, HomeFragment.this);
                    bannerViewPager.setAdapter(adapter);
                    bannerSize = bannerList.size();
                    if (bannerSize > 1) {
                        bannerHandler.removeCallbacks(bannerRunnable);
                        bannerHandler.postDelayed(bannerRunnable, BANNER_DELAY_MS);
                    }
                } else {
                    showToast("Gagal memuat banner");
                }
            }
            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {
                if (!isAdded()) return;
                showToast("Error banner: " + t.getMessage());
            }
        });
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(requireContext(), DetailProductActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(Product product) {
        if (userId == -1) { showToast("Silakan login dulu"); return; }
        if (product == null) { showToast("Produk tidak valid"); return; }
        if (product.getStock() <= 0) { showToast("Stok produk habis"); return; }
        ProductActionBottomSheetDialogFragment
                .newInstance(product, ProductActionBottomSheetDialogFragment.MODE_ADD_TO_CART)
                .show(getParentFragmentManager(), "product_action");
    }

    @Override
    public void onCategoryClick(Category category) {
        if (category != null) navigateToSearch(category.getName());
    }

    @Override
    public void onBannerClick(Banner banner) {
        if (banner == null || productList.isEmpty()) return;
        Product target = null;
        if ("best_seller".equals(banner.getType())) target = getBestSellerProduct();
        else if ("limited".equals(banner.getType())) target = getLimitedProduct();
        if (target != null) {
            Intent intent = new Intent(requireContext(), DetailProductActivity.class);
            intent.putExtra("product", target);
            startActivity(intent);
        }
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        if (getView() == null) return;
        if (!isConnected)
            Snackbar.make(getView(), "Tidak ada koneksi internet", Snackbar.LENGTH_LONG).show();
    }

    private void setupNetworkReceiver() {
        networkReceiver = new NetworkReceiver(this);
        requireActivity().registerReceiver(networkReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private String safeLower(String v) { return v == null ? "" : v.toLowerCase(); }

    private String sortLabel(int sort) {
        switch (sort) {
            case FilterBottomSheetFragment.SORT_POPULAR:    return "Terlaris";
            case FilterBottomSheetFragment.SORT_PRICE_ASC:  return "Termurah";
            case FilterBottomSheetFragment.SORT_PRICE_DESC: return "Termahal";
            case FilterBottomSheetFragment.SORT_NAME_AZ:    return "A-Z";
            default:                                         return "Default";
        }
    }

    private String formatPrice(int price) {
        if (price == 0) return "0";
        StringBuilder sb = new StringBuilder(String.valueOf(price));
        for (int i = sb.length() - 3; i > 0; i -= 3) sb.insert(i, '.');
        return sb.toString();
    }

    private void showToast(String msg) {
        if (isAdded()) Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private Product getBestSellerProduct() {
        if (productList.isEmpty()) return null;
        Product best = productList.get(0);
        for (Product p : productList) if (p.getSoldCount() > best.getSoldCount()) best = p;
        return best;
    }

    private Product getLimitedProduct() {
        if (productList.isEmpty()) return null;
        Product limited = productList.get(0);
        for (Product p : productList) if (p.getStock() < limited.getStock()) limited = p;
        return limited;
    }
}