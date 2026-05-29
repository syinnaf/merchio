package com.example.merchio.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.merchio.DetailProductActivity;
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

    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private int bannerSize = 0;
    private static final long BANNER_DELAY_MS = 3000L;

    private final Runnable bannerRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerSize == 0 || bannerViewPager == null) {
                return;
            }

            int next = (bannerViewPager.getCurrentItem() + 1) % bannerSize;
            bannerViewPager.setCurrentItem(next, true);
            bannerHandler.postDelayed(this, BANNER_DELAY_MS);
        }
    };

    public HomeFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(
                R.layout.fragment_home,
                container,
                false
        );

        initViews(view);
        initHelpers();
        setupRecyclerViews();
        setupNetworkReceiver();
        fetchProducts();
        fetchCategories();
        fetchBanners();

        return view;
    }

    private void initViews(View view) {
        rvCategory = view.findViewById(R.id.rvCategory);
        rvPopular = view.findViewById(R.id.rvPopular);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
    }

    private void initHelpers() {
        dbHelper = new DbHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        apiService = ApiClient
                .getClient()
                .create(ApiService.class);
    }

    private void setupRecyclerViews() {
        rvCategory.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        rvPopular.setLayoutManager(
                new GridLayoutManager(
                        requireContext(),
                        2
                )
        );

        rvPopular.setNestedScrollingEnabled(false);
        rvCategory.setNestedScrollingEnabled(false);
    }

    private void setupNetworkReceiver() {
        networkReceiver = new NetworkReceiver(this);

        IntentFilter filter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION
        );

        requireActivity().registerReceiver(
                networkReceiver,
                filter
        );
    }

    @Override
    public void onResume() {
        super.onResume();

        if (bannerSize > 1) {
            bannerHandler.postDelayed(bannerRunnable, BANNER_DELAY_MS);
        }
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
                    ProductAdapter adapter = new ProductAdapter(
                            requireContext(),
                            response.body(),
                            HomeFragment.this
                    );

                    rvPopular.setAdapter(adapter);

                } else {
                    showToast("Gagal memuat produk");
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

                showToast("Error produk: " + t.getMessage());
            }
        });
    }

    private void fetchCategories() {
        apiService.getCategories().enqueue(new Callback<List<Category>>() {

            @Override
            public void onResponse(
                    Call<List<Category>> call,
                    Response<List<Category>> response
            ) {
                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    CategoryAdapter adapter = new CategoryAdapter(
                            requireContext(),
                            response.body(),
                            HomeFragment.this
                    );

                    rvCategory.setAdapter(adapter);

                } else {
                    showToast("Gagal memuat kategori");
                }
            }

            @Override
            public void onFailure(
                    Call<List<Category>> call,
                    Throwable t
            ) {
                if (!isAdded()) {
                    return;
                }

                showToast("Error kategori: " + t.getMessage());
            }
        });
    }

    private void fetchBanners() {
        apiService.getBanners().enqueue(new Callback<List<Banner>>() {

            @Override
            public void onResponse(
                    Call<List<Banner>> call,
                    Response<List<Banner>> response
            ) {
                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Banner> bannerList = response.body();

                    BannerAdapter adapter = new BannerAdapter(
                            requireContext(),
                            bannerList,
                            HomeFragment.this
                    );

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
            public void onFailure(
                    Call<List<Banner>> call,
                    Throwable t
            ) {
                if (!isAdded()) {
                    return;
                }

                showToast("Error banner: " + t.getMessage());
            }
        });
    }

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

        String productId = safeText(product.getId(), "0");
        String productName = safeText(product.getName(), "Produk");
        String productImage = safeText(product.getImageUrl(), "");
        String productType = safeText(product.getType(), "Default");

        boolean success = dbHelper.addToCart(
                userId,
                productId,
                productName,
                productImage,
                product.getPrice(),
                productType,
                1,
                product.getStock()
        );

        if (success) {
            Toast.makeText(
                    requireContext(),
                    productName + " masuk cart",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    requireContext(),
                    "Gagal menambahkan ke cart",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public void onCategoryClick(Category category) {
        if (category != null) {
            showToast("Kategori: " + category.getName());
        }
    }

    @Override
    public void onBannerClick(Banner banner) {
        if (banner != null) {
            showToast("Banner: " + banner.getTitle());
        }
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        if (getView() == null) {
            return;
        }

        if (!isConnected) {
            Snackbar.make(
                    getView(),
                    "Tidak ada koneksi internet",
                    Snackbar.LENGTH_LONG
            ).show();
        }
    }

    private String safeText(String value, String fallback) {
        if (TextUtils.isEmpty(value)) {
            return fallback;
        }

        return value;
    }

    private void showToast(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}