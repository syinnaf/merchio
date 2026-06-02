package com.example.merchio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.R
import com.example.merchio.data.model.ProfileDto

class AdminUserAdapter(private val onEdit: (ProfileDto) -> Unit) : RecyclerView.Adapter<AdminUserAdapter.AdminUserViewHolder>() {
    private val items = mutableListOf<ProfileDto>()
    fun submitList(data: List<ProfileDto>) { items.clear(); items.addAll(data); notifyDataSetChanged() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminUserViewHolder =
        AdminUserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_admin_user, parent, false))
    override fun onBindViewHolder(holder: AdminUserViewHolder, position: Int) = holder.bind(items[position], position)
    override fun getItemCount(): Int = items.size

    inner class AdminUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val number: TextView = itemView.findViewById(R.id.tv_user_number)
        private val name: TextView = itemView.findViewById(R.id.tv_user_name)
        private val status: TextView = itemView.findViewById(R.id.tv_user_status)
        private val edit: View = itemView.findViewById(R.id.btn_edit_user)
        fun bind(item: ProfileDto, pos: Int) {
            number.text = "${pos + 1}."
            name.text = "${item.displayName}\n${item.email ?: "-"}"
            status.text = if (item.isActive) item.role.uppercase() else "INACTIVE"
            edit.setOnClickListener { onEdit(item) }
        }
    }
}
