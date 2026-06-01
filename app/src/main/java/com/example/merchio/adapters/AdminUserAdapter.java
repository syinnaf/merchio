package com.example.merchio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchio.R;
import com.example.merchio.models.AdminUser;

import java.util.ArrayList;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    public interface OnUserActionListener {
        void onEdit(AdminUser user);
    }

    private final List<AdminUser> users = new ArrayList<>();
    private final OnUserActionListener listener;

    public AdminUserAdapter(OnUserActionListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<AdminUser> newUsers) {
        users.clear();
        users.addAll(newUsers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        AdminUser user = users.get(position);

        holder.tvNumber.setText(String.valueOf(position + 1));
        holder.tvName.setText(empty(user.getName()) ? "Merchio User" : user.getName());
        holder.tvStatus.setText(user.isActive() ? "Status : Active" : "Status : Inactive");
        holder.tvStatus.setAlpha(user.isActive() ? 1f : 0.65f);

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private boolean empty(String value) {
        return value == null || value.trim().isEmpty();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvName, tvStatus, btnEdit;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tv_user_number);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvStatus = itemView.findViewById(R.id.tv_user_status);
            btnEdit = itemView.findViewById(R.id.btn_edit_user);
        }
    }
}
