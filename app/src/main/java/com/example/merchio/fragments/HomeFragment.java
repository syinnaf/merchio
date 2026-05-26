package com.example.merchio.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.merchio.R;

public class HomeFragment extends Fragment {

    RecyclerView rvCategory, rvPopular;
    ViewPager2 bannerViewPager;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvCategory = view.findViewById(R.id.rvCategory);
        rvPopular = view.findViewById(R.id.rvPopular);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);

        rvCategory.setLayoutManager(
                new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        rvPopular.setLayoutManager(
                new GridLayoutManager(getContext(), 2)
        );

        return view;
    }
}