package com.example.merchio.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.CheckoutActivity
import com.example.merchio.DetailProductActivity
import com.example.merchio.R
import com.example.merchio.adapters.CategoryAdapter
import com.example.merchio.adapters.ProductAdapter
import com.example.merchio.data.model.CategoryDto
import com.example.merchio.data.model.ProductDto
import com.example.merchio.data.repository.AuthRepository
import com.example.merchio.data.repository.CartRepository
import com.example.merchio.data.repository.ProductRepository
import com.example.merchio.utils.IntentKeys
import com.example.merchio.utils.toast
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private val productRepository = ProductRepository()
    private val cartRepository = CartRepository()
    private val authRepository = AuthRepository()
    private lateinit var productAdapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private var allProducts: List<ProductDto> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rvCategory = view.findViewById<RecyclerView>(R.id.rvCategory)
        val rvPopular = view.findViewById<RecyclerView>(R.id.rvPopular)
        val search = view.findViewById<EditText>(R.id.etSearch)
        val greeting = view.findViewById<TextView>(R.id.tvGreeting)

        productAdapter = ProductAdapter(
            onClick = { openDetail(it) },
            onCartClick = { addToCart(it) }
        )
        categoryAdapter = CategoryAdapter { selected -> filterByCategory(selected) }

        rvCategory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvCategory.adapter = categoryAdapter
        rvPopular.layoutManager = LinearLayoutManager(requireContext())
        rvPopular.adapter = productAdapter

        search.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, ProductSearchFragment())
                .commit()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            authRepository.currentProfileOrNull()?.let { greeting.text = "Hi, ${it.displayName}" }
        }
        loadData()
    }

    override fun onResume() {
        super.onResume()
        if (::productAdapter.isInitialized) loadData()
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                val categories = productRepository.categories()
                allProducts = productRepository.activeProducts()
                categories
            }.onSuccess { categories ->
                categoryAdapter.submitList(categories)
                productAdapter.submitList(allProducts)
            }.onFailure {
                requireContext().toast(it.message ?: "Gagal memuat produk")
            }
        }
    }

    private fun filterByCategory(category: CategoryDto?) {
        productAdapter.submitList(if (category == null) allProducts else allProducts.filter { it.categoryId == category.id })
    }

    private fun openDetail(product: ProductDto) {
        startActivity(Intent(requireContext(), DetailProductActivity::class.java).putExtra(IntentKeys.PRODUCT_ID, product.id))
    }

    private fun addToCart(product: ProductDto) {
        if (product.stock <= 0) { requireContext().toast("Stock habis"); return }
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching { cartRepository.addToCart(product.id) }
                .onSuccess { requireContext().toast("Ditambahkan ke cart") }
                .onFailure { requireContext().toast(it.message ?: "Gagal tambah cart") }
        }
    }
}
