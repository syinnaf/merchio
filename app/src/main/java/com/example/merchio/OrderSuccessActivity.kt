package com.example.merchio

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.adapters.CheckoutAdapter
import com.example.merchio.utils.IntentKeys

class OrderSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)
        findViewById<TextView>(R.id.txtOrderCode).text = intent.getStringExtra(IntentKeys.ORDER_CODE) ?: "Order Success"
        findViewById<TextView>(R.id.txtEstimatedArrival).text = "Estimated arrival: 2-4 days"
        findViewById<RecyclerView>(R.id.rvOrderItems).layoutManager = LinearLayoutManager(this)
        findViewById<android.view.View>(R.id.btnContinueShopping).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }
}
