package com.example.merchio

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.adapters.CheckoutAdapter
import com.example.merchio.data.model.CartDisplayItem
import com.example.merchio.data.model.CartItemDto
import com.example.merchio.data.repository.CartRepository
import com.example.merchio.data.repository.CheckoutRepository
import com.example.merchio.data.repository.ProductRepository
import com.example.merchio.data.repository.ProfileRepository
import com.example.merchio.utils.CurrencyFormatter
import com.example.merchio.utils.IntentKeys
import com.example.merchio.utils.toast
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity() {
    private val cartRepository = CartRepository()
    private val productRepository = ProductRepository()
    private val profileRepository = ProfileRepository()
    private val checkoutRepository = CheckoutRepository()
    private val adapter = CheckoutAdapter()
    private var items: List<CartDisplayItem> = emptyList()
    private var addressId: String? = null
    private var mode: String = IntentKeys.MODE_CART

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        findViewById<TextView>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<RecyclerView>(R.id.rvCheckout).layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.rvCheckout).adapter = adapter
        findViewById<RadioButton>(R.id.rbStandard).isChecked = true
        findViewById<RadioButton>(R.id.rbBank).isChecked = true
        findViewById<RadioButton>(R.id.rbStandard).setOnClickListener { refreshTotals() }
        findViewById<RadioButton>(R.id.rbExpress).setOnClickListener { refreshTotals() }
        findViewById<android.view.View>(R.id.btnBuy).setOnClickListener { checkout() }
        mode = intent.getStringExtra(IntentKeys.CHECKOUT_MODE) ?: IntentKeys.MODE_CART
        loadCheckout()
    }

    private fun loadCheckout() {
        lifecycleScope.launch {
            runCatching {
                val address = profileRepository.ensureDefaultAddress()
                addressId = address.id
                findViewById<TextView>(R.id.txtAddress).text = address.singleLine()

                items = if (mode == IntentKeys.MODE_BUY_NOW) {
                    val productId = intent.getStringExtra(IntentKeys.PRODUCT_ID) ?: error("Product kosong")
                    val qty = intent.getIntExtra(IntentKeys.QUANTITY, 1)
                    val product = productRepository.productById(productId)
                    listOf(CartDisplayItem(CartItemDto("buy_now", profileRepository.userId(), product.id, "Default", qty, true), product))
                } else {
                    val ids = intent.getStringArrayListExtra(IntentKeys.CART_ITEM_IDS).orEmpty().toSet()
                    cartRepository.cartItems().filter { ids.contains(it.cart.id) }
                }
            }.onSuccess {
                adapter.submitList(items)
                refreshTotals()
            }.onFailure { toast(it.message ?: "Gagal checkout") }
        }
    }

    private fun shippingFee(): Double = if (findViewById<RadioButton>(R.id.rbExpress).isChecked) 15000.0 else 10000.0
    private fun shippingMethod(): String = if (findViewById<RadioButton>(R.id.rbExpress).isChecked) "Express" else "Standard"

    private fun paymentMethod(): String = when {
        findViewById<RadioButton>(R.id.rbDana).isChecked -> "dana"
        findViewById<RadioButton>(R.id.rbGopay).isChecked -> "gopay"
        findViewById<RadioButton>(R.id.rbShopeePay).isChecked -> "shopeepay"
        findViewById<RadioButton>(R.id.rbCod).isChecked -> "cod"
        else -> "bank_transfer"
    }

    private fun refreshTotals() {
        val subtotal = items.sumOf { it.lineTotal }
        val shipping = shippingFee()
        val tax = kotlin.math.round(subtotal * 0.10)
        val total = subtotal + shipping + tax
        findViewById<TextView>(R.id.txtSubtotal).text = CurrencyFormatter.rupiah(subtotal)
        findViewById<TextView>(R.id.txtShipping).text = CurrencyFormatter.rupiah(shipping)
        findViewById<TextView>(R.id.txtTax).text = CurrencyFormatter.rupiah(tax)
        findViewById<TextView>(R.id.txtTotal).text = CurrencyFormatter.rupiah(total)
    }

    private fun checkout() {
        val addr = addressId ?: return toast("Alamat belum tersedia")
        if (items.isEmpty()) return toast("Item kosong")
        lifecycleScope.launch {
            findViewById<android.view.View>(R.id.btnBuy).isEnabled = false
            val result = runCatching {
                if (mode == IntentKeys.MODE_BUY_NOW) {
                    val first = items.first()
                    checkoutRepository.checkoutBuyNow(first.product.id, first.cart.variantLabel, first.cart.quantity, addr, paymentMethod(), shippingMethod(), shippingFee())
                } else {
                    checkoutRepository.checkoutCart(items.map { it.cart.id }, addr, paymentMethod(), shippingMethod(), shippingFee())
                }
            }
            result.onSuccess { res ->
                if (res.success) {
                    startActivity(Intent(this@CheckoutActivity, OrderSuccessActivity::class.java)
                        .putExtra(IntentKeys.ORDER_ID, res.orderId)
                        .putExtra(IntentKeys.ORDER_CODE, res.orderCode))
                    finish()
                } else toast(res.message ?: "Checkout gagal")
            }.onFailure { toast(it.message ?: "Checkout gagal") }
            findViewById<android.view.View>(R.id.btnBuy).isEnabled = true
        }
    }
}
