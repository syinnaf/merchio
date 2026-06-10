package com.example.merchio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchio.R;
import com.example.merchio.models.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressVH> {

    public interface Listener {
        void onEdit(Address address);
        void onSetDefault(Address address);

        void onSelect(Address address);
    }

    private final List<Address> data;
    private final Listener listener;
    private final boolean isCheckoutMode;

    public AddressAdapter(List<Address> data, Listener listener, boolean isCheckoutMode) {
        this.data = data;
        this.listener = listener;
        this.isCheckoutMode = isCheckoutMode;
    }

    @NonNull
    @Override
    public AddressVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressVH holder, int position) {
        Address address = data.get(position);

        if (isCheckoutMode) {
            holder.btnSelect.setVisibility(View.VISIBLE);
        } else {
            holder.btnSelect.setVisibility(View.GONE);
        }

        holder.tvRecipient.setText(address.getRecipientName());
        holder.tvPhone.setText(address.getPhone());
        holder.tvFullAddress.setText(address.getFullAddress());

        holder.tvDefaultBadge.setVisibility(address.isDefaultAddress() ? View.VISIBLE : View.GONE);

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(address));
        holder.btnDefault.setOnClickListener(v -> listener.onSetDefault(address));
        holder.btnSelect.setOnClickListener(v -> listener.onSelect(address));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class AddressVH extends RecyclerView.ViewHolder {
        TextView tvRecipient, tvPhone, tvFullAddress, tvDefaultBadge;
        Button btnEdit, btnDefault, btnSelect;

        public AddressVH(@NonNull View itemView) {
            super(itemView);
            tvRecipient = itemView.findViewById(R.id.tvRecipient);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvFullAddress = itemView.findViewById(R.id.tvFullAddress);
            tvDefaultBadge = itemView.findViewById(R.id.tvDefaultBadge);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDefault = itemView.findViewById(R.id.btn_default);
            btnSelect = itemView.findViewById(R.id.btn_select);
        }
    }
}