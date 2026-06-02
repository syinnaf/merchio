package com.example.merchio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PaymentMethodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)
        findViewById<android.view.View>(R.id.btn_back)?.setOnClickListener { finish() }
    }
}
