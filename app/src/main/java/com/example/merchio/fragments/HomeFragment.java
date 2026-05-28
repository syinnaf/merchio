package com.example.merchio.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.merchio.R;
import com.example.merchio.adapters.BannerAdapter;
import com.example.merchio.adapters.CategoryAdapter;
import com.example.merchio.adapters.ProductAdapter;
import com.example.merchio.api.ApiClient;
import com.example.merchio.api.ApiService;
import com.example.merchio.models.Banner;
import com.example.merchio.models.Category;
import com.example.merchio.models.Product;
import com.example.merchio.receivers.NetworkReceiver;

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
    private NetworkReceiver networkReceiver;

    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private int bannerSize = 0;
    private static final long BANNER_DELAY_MS = 3000L;

    private final Runnable bannerRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerSize == 0) return;
            int next = (bannerViewPager.getCurrentItem() + 1) % bannerSize;
            bannerViewPager.setCurrentItem(next, true);
            bannerHandler.postDelayed(this, BANNER_DELAY_MS);
        }
    };

    public HomeFragment() {}

    // ── Lifecycle ──────────────────────────────────────────────────────────────
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvCategory      = view.findViewById(R.id.rvCategory);
        rvPopular       = view.findViewById(R.id.rvPopular);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);

        // Category: horizontal
        rvCategory.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Popular products: 2-column grid
        rvPopular.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Disable nested scrolling so ScrollView controls the page
        rvPopular.setNestedScrollingEnabled(false);
        rvCategory.setNestedScrollingEnabled(false);

        apiService = ApiClient.getClient().create(ApiService.class);

        fetchProducts();
        fetchCategories();
        fetchBanners();

        networkReceiver =
                new NetworkReceiver(this);

        IntentFilter filter =
                new IntentFilter(
                        ConnectivityManager.CONNECTIVITY_ACTION
                );

        requireActivity().registerReceiver(
                networkReceiver,
                filter
        );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Restart auto-scroll when fragment becomes visible
        if (bannerSize > 1) {
            bannerHandler.postDelayed(bannerRunnable, BANNER_DELAY_MS);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop auto-scroll to avoid leaking the Handler
        bannerHandler.removeCallbacks(bannerRunnable);
    }

    // ── API Calls ──────────────────────────────────────────────────────────────

    private void fetchProducts() {
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductAdapter adapter = new ProductAdapter(
                            getContext(),
                            response.body(),
                            HomeFragment.this
                    );
                    rvPopular.setAdapter(adapter);
                } else {
                    showToast("Gagal memuat produk");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                showToast("Error produk: " + t.getMessage());
            }
        });
    }

    private void fetchCategories() {
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoryAdapter adapter = new CategoryAdapter(
                            getContext(),
                            response.body(),
                            HomeFragment.this
                    );
                    rvCategory.setAdapter(adapter);
                } else {
                    showToast("Gagal memuat kategori");
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                showToast("Error kategori: " + t.getMessage());
            }
        });
    }

    private void fetchBanners() {
        apiService.getBanners().enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Banner> bannerList = response.body();
                    BannerAdapter adapter = new BannerAdapter(
                            getContext(),
                            bannerList,
                            HomeFragment.this
                    );
                    bannerViewPager.setAdapter(adapter);

                    bannerSize = bannerList.size();

                    // Start auto-scroll only when there are multiple banners
                    if (bannerSize > 1) {
                        bannerHandler.postDelayed(bannerRunnable, BANNER_DELAY_MS);
                    }
                } else {
                    showToast("Gagal memuat banner");
                }
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {
                showToast("Error banner: " + t.getMessage());
            }
        });
    }

    // ── Callbacks ──────────────────────────────────────────────────────────────

    @Override
    public void onProductClick(Product product) {
        // TODO: pindah ke DetailProductActivity
        showToast(product.getName());
    }

    @Override
    public void onAddToCartClick(Product product) {
        // TODO: simpan ke SQLite cart
        showToast(product.getName() + " ditambahkan ke keranjang");
    }

    @Override
    public void onCategoryClick(Category category) {
        // TODO: filter produk berdasarkan kategori
        showToast("Kategori: " + category.getName());
    }

    @Override
    public void onBannerClick(Banner banner) {
        // TODO: buka target_url atau promo detail
        showToast("Banner: " + banner.getTitle());
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNetworkChange(boolean isConnected) {

        if(getView() == null) return;

        if(!isConnected) {

            Snackbar.make(
                    getView(),
                    "Tidak ada koneksi internet",
                    Snackbar.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(networkReceiver != null) {

            requireActivity()
                    .unregisterReceiver(networkReceiver);
        }
    }
}