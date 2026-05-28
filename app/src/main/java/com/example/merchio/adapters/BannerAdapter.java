package com.example.merchio.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.merchio.DetailProductActivity;
import com.example.merchio.R;
import com.example.merchio.models.Banner;

import java.util.List;

public class BannerAdapter
        extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    Context context;
    List<Banner> bannerList;

    public BannerAdapter(Context context,
                         List<Banner> bannerList) {

        this.context = context;
        this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_banner,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Banner banner = bannerList.get(position);

        Glide.with(context)
                .load(banner.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.imgBanner);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailProductActivity.class);
            intent.putExtra("product_id", banner.getProductId()); // sudah benar
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgBanner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBanner =
                    itemView.findViewById(R.id.imgBanner);
        }
    }
}