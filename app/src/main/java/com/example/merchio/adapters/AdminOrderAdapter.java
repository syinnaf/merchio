package com.example.merchio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchio.R;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.AdminOrder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {

    public interface OnOrderActionListener {
        void onChangeStatus(AdminOrder order);
        void onDetail(AdminOrder order);
    }

    private final Context context;
    private final List<AdminOrder> orders = new ArrayList<>();
    private final OnOrderActionListener listener;

    public AdminOrderAdapter(Context context, OnOrderActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setOrders(List<AdminOrder> newOrders) {
        orders.clear();
        orders.addAll(newOrders);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        AdminOrder order = orders.get(position);

        holder.tvTitle.setText(empty(order.getOrderCode()) ? "Order ID" : order.getOrderCode());
        holder.tvCustomer.setText(empty(order.getCustomerName()) ? "Unknown Customer" : order.getCustomerName());
        holder.tvDate.setText(empty(order.getOrderDate()) ? "-" : order.getOrderDate());
        holder.tvTotal.setText(rupiah(order.getTotalPrice()));
        holder.tvStatus.setText(labelStatus(order.getStatus()));
        holder.tvStatus.setBackgroundResource(statusBackground(order.getStatus()));

        holder.btnStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChangeStatus(order);
            }
        });

        holder.btnDetail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetail(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
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

    private String rupiah(int amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(amount).replace(",00", "");
    }

    private boolean empty(String value) {
        return value == null || value.trim().isEmpty();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCustomer, tvDate, tvTotal, tvStatus, btnStatus, btnDetail;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_order_title);
            tvCustomer = itemView.findViewById(R.id.tv_order_customer);
            tvDate = itemView.findViewById(R.id.tv_order_date);
            tvTotal = itemView.findViewById(R.id.tv_order_total);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            btnStatus = itemView.findViewById(R.id.btn_order_status);
            btnDetail = itemView.findViewById(R.id.btn_order_detail);
        }
    }
}
