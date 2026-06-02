package com.example.merchio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.R
import com.example.merchio.data.model.CartDisplayItem
import com.example.merchio.utils.CurrencyFormatter
import com.example.merchio.utils.loadMerchioImage

class CheckoutAdapter : RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder>() {
    private val items = mutableListOf<CartDisplayItem>()

    fun submitList(data: List<CartDisplayItem>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        return CheckoutViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_checkout, parent, false))
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    inner class CheckoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.imgProduct)
        private val name: TextView = itemView.findViewById(R.id.txtName)
        private val type: TextView = itemView.findViewById(R.id.txtType)
        private val qty: TextView = itemView.findViewById(R.id.txtQty)
        private val price: TextView = itemView.findViewById(R.id.txtPrice)
        fun bind(item: CartDisplayItem) {
            image.loadMerchioImage(item.product.imageUrl)
            name.text = item.product.name
            type.text = item.cart.variantLabel
            qty.text = "Qty ${item.cart.quantity}"
            price.text = CurrencyFormatter.rupiah(item.lineTotal)
        }
    }
}
