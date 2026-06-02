package com.example.merchio

import android.app.AlertDialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.adapters.HistoryAdapter
import com.example.merchio.data.model.OrderDisplayItem
import com.example.merchio.data.repository.OrderRepository
import com.example.merchio.utils.CurrencyFormatter
import com.example.merchio.utils.toast
import kotlinx.coroutines.launch

class PurchaseHistoryActivity : AppCompatActivity() {
    private val repository = OrderRepository()
    private lateinit var activeAdapter: HistoryAdapter
    private lateinit var pastAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_history)
        findViewById<android.view.View>(R.id.btn_back).setOnClickListener { finish() }
        activeAdapter = HistoryAdapter({ confirmCancel(it) }, { showDetail(it) })
        pastAdapter = HistoryAdapter({ confirmCancel(it) }, { showDetail(it) })
        findViewById<RecyclerView>(R.id.rvActiveOrders).layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.rvPastOrders).layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.rvActiveOrders).adapter = activeAdapter
        findViewById<RecyclerView>(R.id.rvPastOrders).adapter = pastAdapter
        load()
    }

    override fun onResume() { super.onResume(); if (::activeAdapter.isInitialized) load() }

    private fun load() {
        lifecycleScope.launch {
            runCatching { repository.myOrders() }
                .onSuccess { orders ->
                    activeAdapter.submitList(orders.filter { it.order.status == "packing" || it.order.status == "shipping" })
                    pastAdapter.submitList(orders.filter { it.order.status == "delivered" || it.order.status == "cancelled" })
                }.onFailure { toast(it.message ?: "Gagal memuat order") }
        }
    }

    private fun confirmCancel(order: OrderDisplayItem) {
        AlertDialog.Builder(this)
            .setTitle("Cancel order?")
            .setMessage("Order hanya bisa dibatalkan saat status packing.")
            .setPositiveButton("Cancel Order") { _, _ -> cancel(order) }
            .setNegativeButton("Tutup", null)
            .show()
    }

    private fun cancel(order: OrderDisplayItem) {
        lifecycleScope.launch {
            runCatching { repository.cancelOrder(order.order.id) }
                .onSuccess { toast(it.message ?: if (it.success) "Order dibatalkan" else "Gagal cancel"); load() }
                .onFailure { toast(it.message ?: "Gagal cancel") }
        }
    }

    private fun showDetail(order: OrderDisplayItem) {
        val items = order.items.joinToString("\n") { "• ${it.productName} x${it.quantity} - ${CurrencyFormatter.rupiah(it.price * it.quantity)}" }
        AlertDialog.Builder(this)
            .setTitle(order.order.orderCode)
            .setMessage("Status: ${order.order.status}\nTotal: ${CurrencyFormatter.rupiah(order.order.total)}\n\n$items")
            .setPositiveButton("OK", null)
            .show()
    }
}
