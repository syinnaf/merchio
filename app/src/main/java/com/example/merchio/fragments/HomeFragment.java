package com.example.merchio.fragments;

import android.os.Bundle;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    RecyclerView rvCategory, rvPopular;
    ViewPager2 bannerViewPager;

    ApiService apiService;

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

        return view;
    }

    private void getProducts() {

        apiService.getProducts()
                .enqueue(new Callback<List<Product>>() {

                    @Override
                    public void onResponse(
                            Call<List<Product>> call,
                            Response<List<Product>> response) {

                        if(response.isSuccessful()
                                && response.body() != null) {

                            List<Product> productList =
                                    response.body();

                            Toast.makeText(
                                    getContext(),
                                    "Produk berhasil diambil : "
                                            + productList.size(),
                                    Toast.LENGTH_LONG
                            ).show();

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

                        Toast.makeText(
                                getContext(),
                                "Error : " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}