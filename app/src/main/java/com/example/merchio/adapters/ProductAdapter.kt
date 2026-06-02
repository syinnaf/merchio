package com.example.merchio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.R
import com.example.merchio.data.model.ProductDto
import com.example.merchio.utils.CurrencyFormatter
import com.example.merchio.utils.loadMerchioImage

class ProductAdapter(
    private val onClick: (ProductDto) -> Unit,
    private val onCartClick: (ProductDto) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val items = mutableListOf<ProductDto>()

    fun submitList(data: List<ProductDto>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_search, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.img_product)
        private val category: TextView = itemView.findViewById(R.id.txt_category)
        private val name: TextView = itemView.findViewById(R.id.txt_name)
        private val price: TextView = itemView.findViewById(R.id.txt_price)
        private val cart: View = itemView.findViewById(R.id.btn_cart)

        fun bind(item: ProductDto) {
            image.loadMerchioImage(item.imageUrl)
            category.text = if (item.stock > 0) "Stock ${item.stock}" else "Out of stock"
            name.text = item.name
            price.text = CurrencyFormatter.rupiah(item.price)
            itemView.setOnClickListener { onClick(item) }
            cart.setOnClickListener { onCartClick(item) }
        }
    }
}
