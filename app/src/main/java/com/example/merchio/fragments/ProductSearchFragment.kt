package com.example.merchio.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.DetailProductActivity
import com.example.merchio.R
import com.example.merchio.adapters.ProductAdapter
import com.example.merchio.data.model.ProductDto
import com.example.merchio.data.repository.CartRepository
import com.example.merchio.data.repository.ProductRepository
import com.example.merchio.utils.IntentKeys
import com.example.merchio.utils.toast
import com.example.merchio.utils.visible
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProductSearchFragment : Fragment() {
    private val productRepository = ProductRepository()
    private val cartRepository = CartRepository()
    private lateinit var adapter: ProductAdapter
    private lateinit var emptyLayout: View
    private var searchJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val search = view.findViewById<EditText>(R.id.et_search)
        val rv = view.findViewById<RecyclerView>(R.id.rv_products)
        emptyLayout = view.findViewById(R.id.layout_empty)
        adapter = ProductAdapter({ openDetail(it) }, { addToCart(it) })
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { debounceSearch(s?.toString().orEmpty()) }
            override fun afterTextChanged(s: Editable?) {}
        })
        load("")
    }

    private fun debounceSearch(q: String) {
        searchJob?.cancel()
        searchJob = viewLifecycleOwner.lifecycleScope.launch { delay(300); load(q) }
    }

    private fun load(q: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching { productRepository.search(q) }
                .onSuccess { data -> adapter.submitList(data); emptyLayout.visible(data.isEmpty()) }
                .onFailure { requireContext().toast(it.message ?: "Gagal search") }
        }
    }

    private fun openDetail(product: ProductDto) {
        startActivity(Intent(requireContext(), DetailProductActivity::class.java).putExtra(IntentKeys.PRODUCT_ID, product.id))
    }

    private fun addToCart(product: ProductDto) {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching { cartRepository.addToCart(product.id) }
                .onSuccess { requireContext().toast("Ditambahkan ke cart") }
                .onFailure { requireContext().toast(it.message ?: "Gagal tambah cart") }
        }
    }
}
