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
import com.example.merchio.R;
import com.example.merchio.models.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderSuccessAdapter
        extends RecyclerView.Adapter<OrderSuccessAdapter.ViewHolder> {

    private final Context context;
    private final List<CartItem> itemList;

    public OrderSuccessAdapter(Context context, List<CartItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_order_success, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = itemList.get(position);

        holder.txtName.setText(item.getProductName());
        holder.txtQty.setText("qty : " + item.getQuantity());
        holder.txtPrice.setText(rupiah(item.getProductPrice() * item.getQuantity()));

        Glide.with(context)
                .load(item.getProductImage())
                .placeholder(R.drawable.ic_cart)
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private String rupiah(int amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(amount).replace(",00", "");
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView  txtName, txtQty, txtPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtName    = itemView.findViewById(R.id.txtName);
            txtQty     = itemView.findViewById(R.id.txtQty);
            txtPrice   = itemView.findViewById(R.id.txtPrice);
        }
    }
}