package com.example.merchio.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.merchio.R;
import com.example.merchio.models.Banner;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    public interface OnBannerClickListener {
        void onBannerClick(Banner banner);
    }

    private final Context context;
    private final List<Banner> banners;
    private OnBannerClickListener listener;

    public BannerAdapter(Context context, List<Banner> banners) {
        this.context = context;
        this.banners = banners;
    }

    public BannerAdapter(Context context, List<Banner> banners, OnBannerClickListener listener) {
        this.context = context;
        this.banners = banners;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Banner banner = banners.get(position);

        holder.txtTitle.setText(banner.getTitle());
        holder.txtSubtitle.setText(banner.getSubtitle());

        if (!TextUtils.isEmpty(banner.getImageUrl())) {
            Glide.with(context)
                    .load(banner.getImageUrl())
                    .apply(new RequestOptions().transform(new RoundedCorners(24)))
                    .placeholder(R.drawable.ic_cart)
                    .into(holder.imgBanner);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBannerClick(banner);
            }
        });
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {

        ImageView imgBanner;
        TextView txtTitle, txtSubtitle;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner   = itemView.findViewById(R.id.img_banner);
            txtTitle    = itemView.findViewById(R.id.txt_banner_title);
            txtSubtitle = itemView.findViewById(R.id.txt_banner_subtitle);
        }
    }
}