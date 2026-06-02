package com.example.merchio.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.CheckoutActivity
import com.example.merchio.R
import com.example.merchio.adapters.CartAdapter
import com.example.merchio.data.model.CartDisplayItem
import com.example.merchio.data.repository.CartRepository
import com.example.merchio.utils.CurrencyFormatter
import com.example.merchio.utils.IntentKeys
import com.example.merchio.utils.toast
import com.example.merchio.utils.visible
import kotlinx.coroutines.launch

class CartFragment : Fragment() {
    private val cartRepository = CartRepository()
    private lateinit var adapter: CartAdapter
    private lateinit var totalText: TextView
    private lateinit var emptyText: TextView
    private lateinit var selectAll: CheckBox
    private lateinit var checkoutButton: View
    private var items: List<CartDisplayItem> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_cart, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rv_cart)
        totalText = view.findViewById(R.id.tv_total)
        emptyText = view.findViewById(R.id.tv_empty_cart)
        selectAll = view.findViewById(R.id.cb_select_all)
        checkoutButton = view.findViewById(R.id.btn_checkout)
        adapter = CartAdapter(
            onChecked = { item, checked -> updateChecked(item, checked) },
            onQuantity = { item, qty -> updateQuantity(item, qty) },
            onDelete = { deleteItem(it) }
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
        selectAll.setOnCheckedChangeListener { _, checked -> setAll(checked) }
        checkoutButton.setOnClickListener { checkout() }
        loadCart()
    }

    override fun onResume() { super.onResume(); if (::adapter.isInitialized) loadCart() }

    private fun loadCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching { cartRepository.cartItems() }
                .onSuccess { data ->
                    items = data
                    adapter.submitList(data)
                    emptyText.visible(data.isEmpty())
                    refreshTotal()
                    selectAll.setOnCheckedChangeListener(null)
                    selectAll.isChecked = data.isNotEmpty() && data.all { it.cart.isChecked }
                    selectAll.setOnCheckedChangeListener { _, checked -> setAll(checked) }
                }.onFailure { requireContext().toast(it.message ?: "Gagal memuat cart") }
        }
    }

    private fun refreshTotal() {
        val total = items.filter { it.cart.isChecked }.sumOf { it.lineTotal }
        totalText.text = CurrencyFormatter.rupiah(total)
    }

    private fun updateChecked(item: CartDisplayItem, checked: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch { runCatching { cartRepository.setChecked(item.cart.id, checked) }; loadCart() }
    }

    private fun updateQuantity(item: CartDisplayItem, qty: Int) {
        viewLifecycleOwner.lifecycleScope.launch { runCatching { cartRepository.updateQuantity(item.cart.id, qty) }; loadCart() }
    }

    private fun deleteItem(item: CartDisplayItem) {
        viewLifecycleOwner.lifecycleScope.launch { runCatching { cartRepository.delete(item.cart.id) }; loadCart() }
    }

    private fun setAll(checked: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch { runCatching { cartRepository.setAllChecked(checked) }; loadCart() }
    }

    private fun checkout() {
        val selected = items.filter { it.cart.isChecked }
        if (selected.isEmpty()) { requireContext().toast("Pilih item dulu"); return }
        val ids = ArrayList(selected.map { it.cart.id })
        startActivity(Intent(requireContext(), CheckoutActivity::class.java)
            .putExtra(IntentKeys.CHECKOUT_MODE, IntentKeys.MODE_CART)
            .putStringArrayListExtra(IntentKeys.CART_ITEM_IDS, ids))
    }
}
