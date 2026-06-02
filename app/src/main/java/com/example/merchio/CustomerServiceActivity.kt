package com.example.merchio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class CustomerServiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_service)
        findViewById<View>(R.id.btn_back)?.setOnClickListener { finish() }
        findViewById<View>(R.id.btn_call)?.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:02112345678")))
        }
    }
}
