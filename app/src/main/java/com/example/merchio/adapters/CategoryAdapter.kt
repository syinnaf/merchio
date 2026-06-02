package com.example.merchio.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.R
import com.example.merchio.data.model.CategoryDto

class CategoryAdapter(private val onClick: (CategoryDto?) -> Unit) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private val items = mutableListOf<CategoryDto?>()
    private var selectedIndex = 0

    fun submitList(data: List<CategoryDto>) {
        items.clear()
        items.add(null)
        items.addAll(data)
        selectedIndex = 0
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) = holder.bind(items[position], position)
    override fun getItemCount(): Int = items.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.txt_category_name)
        fun bind(item: CategoryDto?, position: Int) {
            name.text = item?.name ?: "All"
            itemView.isSelected = position == selectedIndex
            itemView.setOnClickListener {
                selectedIndex = bindingAdapterPosition
                notifyDataSetChanged()
                onClick(item)
            }
        }
    }
}
