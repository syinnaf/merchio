package com.example.merchio

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.merchio.data.repository.AdminRepository
import com.example.merchio.data.repository.AuthRepository
import com.example.merchio.utils.toast
import kotlinx.coroutines.launch

class AdminDashboardActivity : AppCompatActivity() {
    private val adminRepository = AdminRepository()
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)
        findViewById<android.view.View>(R.id.menu_manage_users).setOnClickListener { startActivity(Intent(this, ManageUsersActivity::class.java)) }
        findViewById<android.view.View>(R.id.menu_manage_orders).setOnClickListener { startActivity(Intent(this, ManageOrdersActivity::class.java)) }
        findViewById<android.view.View>(R.id.menu_manage_products)?.setOnClickListener { startActivity(Intent(this, ManageProductsActivity::class.java)) }
        findViewById<android.view.View>(R.id.menu_logout).setOnClickListener { logout() }
        loadStats()
    }

    override fun onResume() { super.onResume(); loadStats() }

    private fun loadStats() {
        lifecycleScope.launch {
            runCatching { adminRepository.dashboardStats() }
                .onSuccess { stats ->
                    findViewById<TextView>(R.id.tv_total_users).text = stats.totalUsers.toString()
                    findViewById<TextView>(R.id.tv_total_orders).text = stats.totalOrders.toString()
                    findViewById<TextView>(R.id.tv_cancelled).text = stats.cancelled.toString()
                    findViewById<TextView>(R.id.tv_packing).text = "${stats.packing}\nPacking"
                    findViewById<TextView>(R.id.tv_shipping).text = "${stats.shipping}\nShipping"
                    findViewById<TextView>(R.id.tv_delivered).text = "${stats.delivered}\nDelivered"
                    findViewById<TextView>(R.id.tv_cancelled_summary).text = "${stats.cancelled}\nCancelled"
                }
                .onFailure { toast(it.message ?: "Gagal load dashboard") }
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            authRepository.signOut()
            startActivity(Intent(this@AdminDashboardActivity, LoginActivity::class.java))
            finishAffinity()
        }
    }
}
