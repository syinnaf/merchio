package com.example.merchio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.merchio.R;
import com.example.merchio.models.CartItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems = new ArrayList<>();
    private final OnCartActionListener listener;

    public interface OnCartActionListener {
        void onCheckedChanged(CartItem item, boolean isChecked);
        void onIncreaseQuantity(CartItem item);
        void onDecreaseQuantity(CartItem item);
        void onDeleteItem(CartItem item);
    }

    public CartAdapter(OnCartActionListener listener) {
        this.listener = listener;
    }

    public void setCartItems(List<CartItem> items) {
        cartItems.clear();
        cartItems.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.cbItem.setOnCheckedChangeListener(null);
        holder.cbItem.setChecked(item.isChecked());

        holder.tvName.setText(item.getProductName());
        holder.tvType.setText(item.getType());
        holder.tvPrice.setText(formatRupiah(item.getProductPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        if (item.getProductImage() != null && !item.getProductImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getProductImage())
                    .placeholder(R.drawable.logo_merchio) //ganti produk nanti
                    .error(R.drawable.logo_merchio)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(android.R.color.transparent);
            holder.imgProduct.setBackgroundResource(R.drawable.logo_merchio); //ganti produk nanti
        }

        holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onCheckedChanged(item, isChecked);
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIncreaseQuantity(item);
            }
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDecreaseQuantity(item);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteItem(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private String formatRupiah(int value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(value).replace(",00", "");
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {

        CheckBox cbItem;
        ImageView imgProduct;
        TextView tvName, tvType, tvPrice, tvQuantity;
        ImageButton btnMinus, btnPlus, btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cbItem = itemView.findViewById(R.id.cb_item);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvType = itemView.findViewById(R.id.tv_product_type);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}