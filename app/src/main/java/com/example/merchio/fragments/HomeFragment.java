package com.example.merchio.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.merchio.R;
import com.example.merchio.api.ApiClient;
import com.example.merchio.api.ApiService;
import com.example.merchio.models.Product;
import com.example.merchio.models.Banner;
import com.example.merchio.models.Category;
import com.example.merchio.adapters.ProductAdapter;
import com.example.merchio.adapters.BannerAdapter;
import com.example.merchio.adapters.CategoryAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    RecyclerView rvCategory, rvPopular;
    ViewPager2 bannerViewPager;

    ApiService apiService;

    Handler sliderHandler = new Handler(android.os.Looper.getMainLooper());

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_home,
                container,
                false
        );

        rvCategory = view.findViewById(R.id.rvCategory);

        rvPopular = view.findViewById(R.id.rvPopular);

        bannerViewPager =
                view.findViewById(R.id.bannerViewPager);

        rvCategory.setLayoutManager(
                new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        rvPopular.setLayoutManager(
                new GridLayoutManager(
                        getContext(),
                        2
                )
        );

        apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        getProducts();
        getCategories();
        getBanners();

        return view;
    }

    private void getProducts() {

        apiService.getProducts()
                .enqueue(new Callback<List<Product>>() {

                    @Override
                    public void onResponse(
                            Call<List<Product>> call,
                            Response<List<Product>> response) {

                        if (!isAdded() || getContext() == null) return;

                        if(response.isSuccessful()
                                && response.body() != null) {

                            List<Product> productList =
                                    response.body();

                            ProductAdapter adapter =
                                    new ProductAdapter(
                                            getContext(),
                                            productList,
                                            HomeFragment.this
                                    );

                            rvPopular.setAdapter(adapter);

                        } else {

                            Toast.makeText(
                                    getContext(),
                                    "Response gagal",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Product>> call,
                            Throwable t) {

                        if (!isAdded() || getContext() == null) return;

                        Toast.makeText(
                                getContext(),
                                "Error : " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void getCategories() {

        apiService.getCategories()
                .enqueue(new Callback<List<Category>>() {

                    @Override
                    public void onResponse(
                            Call<List<Category>> call,
                            Response<List<Category>> response) {

                        if (!isAdded() || getContext() == null) return;

                        if(response.isSuccessful()
                                && response.body() != null) {

                            CategoryAdapter adapter =
                                    new CategoryAdapter(
                                            getContext(),
                                            response.body()
                                    );

                            rvCategory.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Category>> call,
                            Throwable t) {

                        if (!isAdded() || getContext() == null) return;

                        Toast.makeText(
                                getContext(),
                                t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void getBanners() {

        apiService.getBanners()
                .enqueue(new Callback<List<Banner>>() {

                    @Override
                    public void onResponse(
                            Call<List<Banner>> call,
                            Response<List<Banner>> response) {

                        if (!isAdded() || getContext() == null) return;

                        if(response.isSuccessful()
                                && response.body() != null) {

                            BannerAdapter adapter =
                                    new BannerAdapter(
                                            getContext(),
                                            response.body()
                                    );

                            bannerViewPager.setAdapter(adapter);

                            autoSlideBanner();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Banner>> call,
                            Throwable t) {

                        if (!isAdded() || getContext() == null) return;

                        Toast.makeText(
                                getContext(),
                                t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void autoSlideBanner() {

        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                if(bannerViewPager == null ||
                        bannerViewPager.getAdapter() == null)
                    return;

                int current =
                        bannerViewPager.getCurrentItem();

                int total =
                        bannerViewPager.getAdapter()
                                .getItemCount();

                if(current < total - 1) {

                    bannerViewPager.setCurrentItem(
                            current + 1
                    );

                } else {

                    bannerViewPager.setCurrentItem(0);
                }

                sliderHandler.postDelayed(
                        this,
                        3000
                );
            }
        };

        sliderHandler.postDelayed(
                runnable,
                3000
        );
    }

    @Override
    public void onProductClick(Product product) {

        Toast.makeText(
                getContext(),
                product.getName(),
                Toast.LENGTH_SHORT
        ).show();

        // nanti pindah ke DetailProductActivity
    }

    @Override
    public void onAddToCartClick(Product product) {

        Toast.makeText(
                getContext(),
                product.getName() + " ditambahkan",
                Toast.LENGTH_SHORT
        ).show();

        // nanti simpan ke SQLite
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sliderHandler.removeCallbacksAndMessages(null);
    }
}