package com.example.merchio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.R
import com.example.merchio.data.model.ProductDto
import com.example.merchio.utils.CurrencyFormatter
import com.example.merchio.utils.loadMerchioImage

class AdminProductAdapter(
    private val onEdit: (ProductDto) -> Unit,
    private val onToggleActive: (ProductDto) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder>() {
    private val items = mutableListOf<ProductDto>()
    fun submitList(data: List<ProductDto>) { items.clear(); items.addAll(data); notifyDataSetChanged() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminProductViewHolder =
        AdminProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_admin_product, parent, false))
    override fun onBindViewHolder(holder: AdminProductViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    inner class AdminProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.imgProduct)
        private val name: TextView = itemView.findViewById(R.id.tvProductName)
        private val info: TextView = itemView.findViewById(R.id.tvProductInfo)
        private val status: TextView = itemView.findViewById(R.id.tvProductStatus)
        private val edit: View = itemView.findViewById(R.id.btnEditProduct)
        private val toggle: View = itemView.findViewById(R.id.btnToggleProduct)
        fun bind(item: ProductDto) {
            image.loadMerchioImage(item.imageUrl)
            name.text = item.name
            info.text = "${CurrencyFormatter.rupiah(item.price)} • Stock ${item.stock}"
            status.text = if (item.isActive) "ACTIVE" else "INACTIVE"
            edit.setOnClickListener { onEdit(item) }
            toggle.setOnClickListener { onToggleActive(item) }
        }
    }
}
