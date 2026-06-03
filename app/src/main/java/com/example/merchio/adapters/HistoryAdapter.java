package com.example.merchio.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.merchio.PurchaseHistoryDetailActivity;
import com.example.merchio.R;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.OrderHistory;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    public interface OnConfirmClick {
        void onConfirm(long orderId);
    }

    private final Context context;
    private final List<OrderHistory> list;
    private final OnConfirmClick listener;
    private final DbHelper dbHelper;

    public HistoryAdapter(
            Context context,
            List<OrderHistory> list,
            OnConfirmClick listener
    ) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.dbHelper = new DbHelper(context);
    }

    private String labelStatus(String status) {
        if (status == null) return "Packing";
        if (status.equalsIgnoreCase(DbHelper.STATUS_SHIPPING)) return "Shipping";
        if (status.equalsIgnoreCase(DbHelper.STATUS_DELIVERED)) return "Delivered";
        if (status.equalsIgnoreCase(DbHelper.STATUS_CANCELLED)) return "Cancelled";
        return "Packing";
    }

    private int statusBackground(String status) {
        if (status == null) return R.drawable.bg_status_packing;
        if (status.equalsIgnoreCase(DbHelper.STATUS_SHIPPING)) return R.drawable.bg_status_shipping;
        if (status.equalsIgnoreCase(DbHelper.STATUS_DELIVERED)) return R.drawable.bg_status_delivered;
        if (status.equalsIgnoreCase(DbHelper.STATUS_CANCELLED)) return R.drawable.bg_status_cancelled;
        return R.drawable.bg_status_packing;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        OrderHistory item = list.get(position);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PurchaseHistoryDetailActivity.class);
            intent.putExtra("order_id", item.orderId);
            context.startActivity(intent);
        });

        String firstProductName = item.productName;
        String firstProductImage = item.image;
        int extraItems = 0;

        Cursor cursor = null;
        try {
            cursor = dbHelper.getOrderItems(item.orderId);

            if (cursor != null && cursor.moveToFirst()) {
                firstProductName = cursor.getString(
                        cursor.getColumnIndexOrThrow("product_name")
                );

                firstProductImage = cursor.getString(
                        cursor.getColumnIndexOrThrow("product_image")
                );

                extraItems = cursor.getCount() - 1;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        holder.txtTitle.setText(firstProductName);
        holder.txtPrice.setText(rupiah(item.totalPrice));
        holder.txtStatus.setText(labelStatus(item.status));
        holder.txtStatus.setBackgroundResource(statusBackground(item.status));

        if (extraItems > 0) {
            holder.txtMoreItems.setVisibility(View.VISIBLE);
            holder.txtMoreItems.setText("+" + extraItems);
        } else {
            holder.txtMoreItems.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(firstProductImage)
                .placeholder(R.drawable.ic_cart)
                .into(holder.imgProduct);

        if (item.status != null
                && item.status.equalsIgnoreCase(DbHelper.STATUS_DELIVERED)
                && !item.isReceived) {

            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.btnConfirm.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConfirm(item.orderId);
                }
            });

        } else {
            holder.btnConfirm.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private String rupiah(int amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(amount).replace(",00", "");
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView txtTitle;
        TextView txtPrice;
        TextView txtStatus;
        TextView txtMoreItems;
        Button btnConfirm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtMoreItems = itemView.findViewById(R.id.txtMoreItems);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
        }
    }
}