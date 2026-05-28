package com.example.merchio.api;

import com.example.merchio.models.Banner;
import com.example.merchio.models.Category;
import com.example.merchio.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("products.json")
    Call<List<Product>> getProducts();

    @GET("categories.json")
    Call<List<Category>> getCategories();

    @GET("banners.json")
    Call<List<Banner>> getBanners();
}