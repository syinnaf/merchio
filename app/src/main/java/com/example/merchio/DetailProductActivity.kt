package com.example.merchio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.merchio.data.model.ProductDto
import com.example.merchio.data.repository.CartRepository
import com.example.merchio.data.repository.ProductRepository
import com.example.merchio.utils.CurrencyFormatter
import com.example.merchio.utils.IntentKeys
import com.example.merchio.utils.loadMerchioImage
import com.example.merchio.utils.toast
import kotlinx.coroutines.launch

class DetailProductActivity : AppCompatActivity() {
    private val productRepository = ProductRepository()
    private val cartRepository = CartRepository()
    private var product: ProductDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<View>(R.id.btnCart).setOnClickListener { addToCart() }
        findViewById<View>(R.id.btnBuyNow).setOnClickListener { buyNow() }
        loadProduct()
    }

    override fun onResume() { super.onResume(); if (product != null) loadProduct() }

    private fun loadProduct() {
        val id = intent.getStringExtra(IntentKeys.PRODUCT_ID) ?: return finish()
        lifecycleScope.launch {
            runCatching { productRepository.productById(id) }
                .onSuccess { data ->
                    product = data
                    findViewById<ImageView>(R.id.imgProduct).loadMerchioImage(data.imageUrl)
                    findViewById<TextView>(R.id.tvName).text = data.name
                    findViewById<TextView>(R.id.tvPrice).text = CurrencyFormatter.rupiah(data.price)
                    findViewById<TextView>(R.id.tvType).text = "Stock ${data.stock} • Sold ${data.soldCount}"
                    findViewById<TextView>(R.id.tvDescription).text = data.description ?: "No description"
                }
                .onFailure { toast(it.message ?: "Produk tidak ditemukan"); finish() }
        }
    }

    private fun addToCart() {
        val item = product ?: return
        if (item.stock <= 0) { toast("Stock habis"); return }
        lifecycleScope.launch {
            runCatching { cartRepository.addToCart(item.id) }
                .onSuccess { toast("Ditambahkan ke cart") }
                .onFailure { toast(it.message ?: "Gagal tambah cart") }
        }
    }

    private fun buyNow() {
        val item = product ?: return
        if (item.stock <= 0) { toast("Stock habis"); return }
        startActivity(Intent(this, CheckoutActivity::class.java)
            .putExtra(IntentKeys.CHECKOUT_MODE, IntentKeys.MODE_BUY_NOW)
            .putExtra(IntentKeys.PRODUCT_ID, item.id)
            .putExtra(IntentKeys.QUANTITY, 1))
    }
}
