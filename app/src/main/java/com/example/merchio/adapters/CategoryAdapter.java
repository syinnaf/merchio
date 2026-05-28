package com.example.merchio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.merchio.R;
import com.example.merchio.models.Category;

import java.util.List;

public class CategoryAdapter
        extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    List<Category> categoryList;

    public CategoryAdapter(Context context,
                           List<Category> categoryList) {

        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_category,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Category category =
                categoryList.get(position);

        holder.txtCategory.setText(
                category.getName()
        );

        Glide.with(context)
                .load(category.getIconUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.imgCategory);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgCategory;
        TextView txtCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgCategory =
                    itemView.findViewById(R.id.imgCategory);

            txtCategory =
                    itemView.findViewById(R.id.txtCategory);
        }
    }
}