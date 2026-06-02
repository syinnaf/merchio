package com.example.merchio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.R
import com.example.merchio.data.model.OrderDisplayItem
import com.example.merchio.utils.CurrencyFormatter
import com.example.merchio.utils.loadMerchioImage

class HistoryAdapter(
    private val onCancel: (OrderDisplayItem) -> Unit,
    private val onClick: (OrderDisplayItem) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    private val items = mutableListOf<OrderDisplayItem>()
    fun submitList(data: List<OrderDisplayItem>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
        HistoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false))
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.imgProduct)
        private val status: TextView = itemView.findViewById(R.id.txtStatus)
        private val title: TextView = itemView.findViewById(R.id.txtTitle)
        private val price: TextView = itemView.findViewById(R.id.txtPrice)
        private val confirm: View = itemView.findViewById(R.id.btnConfirm)
        fun bind(item: OrderDisplayItem) {
            val first = item.items.firstOrNull()
            image.loadMerchioImage(first?.productImageUrl)
            status.text = item.order.status.uppercase()
            title.text = "${item.order.orderCode} • ${first?.productName ?: "Order"}"
            price.text = CurrencyFormatter.rupiah(item.order.total)
            confirm.visibility = if (item.order.status == "packing") View.VISIBLE else View.GONE
            confirm.setOnClickListener { onCancel(item) }
            itemView.setOnClickListener { onClick(item) }
        }
    }
}
