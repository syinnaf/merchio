package com.example.merchio.fragments;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.merchio.adapters.ProductAdapter;
import com.example.merchio.api.ApiClient;
import com.example.merchio.api.ApiService;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private RecyclerView rvCategory, rvPopular;
    private ViewPager2 bannerViewPager;

    private ApiService apiService;
    private DbHelper dbHelper;
    private SessionManager sessionManager;

    private int userId = -1;

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
        getProducts();

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
    }

    private void getProducts() {
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
                    List<Product> productList = response.body();

                    ProductAdapter adapter = new ProductAdapter(
                            requireContext(),
                            productList,
                            HomeFragment.this
                    );

                    rvPopular.setAdapter(adapter);

                } else {
                    Toast.makeText(
                            requireContext(),
                            "Produk gagal dimuat",
                            Toast.LENGTH_SHORT
                    ).show();
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

    private String safeText(String value, String fallback) {
        if (TextUtils.isEmpty(value)) {
            return fallback;
        }

        return value;
    }
}