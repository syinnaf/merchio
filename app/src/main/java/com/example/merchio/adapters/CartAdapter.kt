package com.example.merchio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.R
import com.example.merchio.data.model.CartDisplayItem
import com.example.merchio.utils.CurrencyFormatter
import com.example.merchio.utils.loadMerchioImage

class CartAdapter(
    private val onChecked: (CartDisplayItem, Boolean) -> Unit,
    private val onQuantity: (CartDisplayItem, Int) -> Unit,
    private val onDelete: (CartDisplayItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    private val items = mutableListOf<CartDisplayItem>()

    fun submitList(data: List<CartDisplayItem>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false))
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val check: CheckBox = itemView.findViewById(R.id.cb_item)
        private val image: ImageView = itemView.findViewById(R.id.img_product)
        private val name: TextView = itemView.findViewById(R.id.tv_product_name)
        private val type: TextView = itemView.findViewById(R.id.tv_product_type)
        private val price: TextView = itemView.findViewById(R.id.tv_product_price)
        private val minus: View = itemView.findViewById(R.id.btn_minus)
        private val quantity: TextView = itemView.findViewById(R.id.tv_quantity)
        private val plus: View = itemView.findViewById(R.id.btn_plus)
        private val delete: View = itemView.findViewById(R.id.btn_delete)

        fun bind(item: CartDisplayItem) {
            check.setOnCheckedChangeListener(null)
            check.isChecked = item.cart.isChecked
            image.loadMerchioImage(item.product.imageUrl)
            name.text = item.product.name
            type.text = item.cart.variantLabel
            price.text = CurrencyFormatter.rupiah(item.product.price)
            quantity.text = item.cart.quantity.toString()
            check.setOnCheckedChangeListener { _, isChecked -> onChecked(item, isChecked) }
            minus.setOnClickListener { onQuantity(item, item.cart.quantity - 1) }
            plus.setOnClickListener { onQuantity(item, item.cart.quantity + 1) }
            delete.setOnClickListener { onDelete(item) }
        }
    }
}
