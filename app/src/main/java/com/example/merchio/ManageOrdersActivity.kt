package com.example.merchio

import android.app.AlertDialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.adapters.AdminOrderAdapter
import com.example.merchio.data.model.OrderDisplayItem
import com.example.merchio.data.repository.AdminRepository
import com.example.merchio.utils.CurrencyFormatter
import com.example.merchio.utils.toast
import com.example.merchio.utils.visible
import kotlinx.coroutines.launch

class ManageOrdersActivity : AppCompatActivity() {
    private val repository = AdminRepository()
    private lateinit var adapter: AdminOrderAdapter
    private lateinit var empty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_orders)
        findViewById<android.view.View>(R.id.btn_back).setOnClickListener { finish() }
        empty = findViewById(R.id.tv_empty_orders)
        adapter = AdminOrderAdapter({ showStatusDialog(it) }, { showDetail(it) })
        findViewById<RecyclerView>(R.id.rv_orders).layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.rv_orders).adapter = adapter
        load()
    }

    override fun onResume() { super.onResume(); if (::adapter.isInitialized) load() }

    private fun load() {
        lifecycleScope.launch {
            runCatching { repository.allOrders() }
                .onSuccess { data -> adapter.submitList(data); empty.visible(data.isEmpty()) }
                .onFailure { toast(it.message ?: "Gagal load order") }
        }
    }

    private fun showStatusDialog(order: OrderDisplayItem) {
        val statuses = arrayOf("packing", "shipping", "delivered", "cancelled")
        AlertDialog.Builder(this)
            .setTitle("Update ${order.order.orderCode}")
            .setItems(statuses) { _, which -> updateStatus(order, statuses[which]) }
            .show()
    }

    private fun updateStatus(order: OrderDisplayItem, status: String) {
        lifecycleScope.launch {
            runCatching { repository.updateOrderStatus(order.order.id, status) }
                .onSuccess { toast(it.message ?: if (it.success) "Status updated" else "Gagal update"); load() }
                .onFailure { toast(it.message ?: "Gagal update") }
        }
    }

    private fun showDetail(order: OrderDisplayItem) {
        val items = order.items.joinToString("\n") { "• ${it.productName} x${it.quantity} - ${CurrencyFormatter.rupiah(it.price * it.quantity)}" }
        AlertDialog.Builder(this)
            .setTitle(order.order.orderCode)
            .setMessage("User: ${order.order.userId}\nStatus: ${order.order.status}\nTotal: ${CurrencyFormatter.rupiah(order.order.total)}\n\n$items")
            .setPositiveButton("OK", null)
            .show()
    }
}
