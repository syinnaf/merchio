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
import com.example.merchio.R;
import com.example.merchio.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private final Context context;
    private final List<Category> categories;
    private final OnCategoryClickListener listener;

    // Tracks which category is currently selected
    private int selectedPosition = 0;

    public CategoryAdapter(Context context, List<Category> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.txtCategoryName.setText(category.getName());

        if (!TextUtils.isEmpty(category.getIconUrl())) {
            Glide.with(context)
                    .load(category.getIconUrl())
                    .placeholder(R.drawable.ic_search)
                    .into(holder.imgCategoryIcon);
        } else {
            holder.imgCategoryIcon.setImageResource(R.drawable.ic_search);
        }

        // Highlight the selected category
        boolean isSelected = position == selectedPosition;
        holder.itemView.setSelected(isSelected);
        holder.txtCategoryName.setSelected(isSelected);

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCategoryIcon;
        TextView txtCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategoryIcon  = itemView.findViewById(R.id.img_category_icon);
            txtCategoryName  = itemView.findViewById(R.id.txt_category_name);
        }
    }
}