package com.example.merchio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.merchio.R;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.OrderHistory;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter
        extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    public interface OnConfirmClick{
        void onConfirm(long orderId);
    }

    private final Context context;
    private final List<OrderHistory> list;
    private final OnConfirmClick listener;

    public HistoryAdapter(
            Context context,
            List<OrderHistory> list,
            OnConfirmClick listener
    ){
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_order_history,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        OrderHistory item = list.get(position);

        holder.txtTitle.setText(item.productName);

        holder.txtPrice.setText(
                rupiah(item.totalPrice)
        );

        holder.txtStatus.setText(
                item.status.toUpperCase()
        );

        Glide.with(context)
                .load(item.image)
                .placeholder(R.drawable.ic_cart)
                .into(holder.imgProduct);

        if(
                item.status.equalsIgnoreCase(
                        DbHelper.STATUS_DELIVERED
                )
                        &&
                        !item.isReceived
        ){

            holder.btnConfirm.setVisibility(
                    View.VISIBLE
            );

            holder.btnConfirm.setOnClickListener(v -> {

                if(listener != null){
                    listener.onConfirm(
                            item.orderId
                    );
                }

            });

        }else{

            holder.btnConfirm.setVisibility(
                    View.GONE
            );
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String rupiah(int amount){

        NumberFormat format =
                NumberFormat.getCurrencyInstance(
                        new Locale("id","ID")
                );

        return format.format(amount)
                .replace(",00","");
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder{

        ImageView imgProduct;

        TextView txtTitle;
        TextView txtPrice;
        TextView txtStatus;

        Button btnConfirm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct =
                    itemView.findViewById(
                            R.id.imgProduct
                    );

            txtTitle =
                    itemView.findViewById(
                            R.id.txtTitle
                    );

            txtPrice =
                    itemView.findViewById(
                            R.id.txtPrice
                    );

            txtStatus =
                    itemView.findViewById(
                            R.id.txtStatus
                    );

            btnConfirm =
                    itemView.findViewById(
                            R.id.btnConfirm
                    );
        }
    }
}