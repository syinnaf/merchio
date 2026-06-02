package com.example.merchio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.R
import com.example.merchio.data.model.OrderDisplayItem
import com.example.merchio.utils.CurrencyFormatter

class AdminOrderAdapter(
    private val onStatusClick: (OrderDisplayItem) -> Unit,
    private val onDetailClick: (OrderDisplayItem) -> Unit
) : RecyclerView.Adapter<AdminOrderAdapter.AdminOrderViewHolder>() {
    private val items = mutableListOf<OrderDisplayItem>()
    fun submitList(data: List<OrderDisplayItem>) { items.clear(); items.addAll(data); notifyDataSetChanged() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminOrderViewHolder =
        AdminOrderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_admin_order, parent, false))
    override fun onBindViewHolder(holder: AdminOrderViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    inner class AdminOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tv_order_title)
        private val status: TextView = itemView.findViewById(R.id.tv_order_status)
        private val customer: TextView = itemView.findViewById(R.id.tv_order_customer)
        private val date: TextView = itemView.findViewById(R.id.tv_order_date)
        private val total: TextView = itemView.findViewById(R.id.tv_order_total)
        private val btnStatus: View = itemView.findViewById(R.id.btn_order_status)
        private val btnDetail: View = itemView.findViewById(R.id.btn_order_detail)
        fun bind(item: OrderDisplayItem) {
            title.text = item.order.orderCode
            status.text = item.order.status.uppercase()
            customer.text = "User: ${item.order.userId.take(8)}"
            date.text = item.order.createdAt ?: "-"
            total.text = CurrencyFormatter.rupiah(item.order.total)
            btnStatus.setOnClickListener { onStatusClick(item) }
            btnDetail.setOnClickListener { onDetailClick(item) }
        }
    }
}
