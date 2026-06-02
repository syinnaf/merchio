package com.example.merchio.data.repository

import com.example.merchio.data.model.CartDisplayItem
import com.example.merchio.data.model.CartItemDto
import com.example.merchio.data.model.CartItemInsertDto
import com.example.merchio.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class CartRepository {
    private val client = SupabaseClientProvider.client
    private val productRepository = ProductRepository()

    private fun userId(): String = client.auth.currentUserOrNull()?.id ?: error("Belum login")

    suspend fun addToCart(productId: String, quantity: Int = 1, variant: String = "Default") {
        val uid = userId()
        val existing = client.from("cart_items")
            .select {
                filter {
                    eq("user_id", uid)
                    eq("product_id", productId)
                    eq("variant_label", variant)
                }
            }
            .decodeList<CartItemDto>()
            .firstOrNull()

        if (existing == null) {
            client.from("cart_items").insert(CartItemInsertDto(uid, productId, variant, quantity, true))
        } else {
            client.from("cart_items").update({
                set("quantity", existing.quantity + quantity)
                set("is_checked", true)
            }) { filter { eq("id", existing.id) } }
        }
    }

    suspend fun cartItems(): List<CartDisplayItem> {
        val rows = client.from("cart_items")
            .select { filter { eq("user_id", userId()) } }
            .decodeList<CartItemDto>()
        return rows.mapNotNull { cart ->
            runCatching { CartDisplayItem(cart, productRepository.productById(cart.productId)) }.getOrNull()
        }.sortedBy { it.product.name }
    }

    suspend fun selectedCartItems(): List<CartDisplayItem> = cartItems().filter { it.cart.isChecked }

    suspend fun setChecked(id: String, checked: Boolean) {
        client.from("cart_items").update({ set("is_checked", checked) }) { filter { eq("id", id) } }
    }

    suspend fun setAllChecked(checked: Boolean) {
        cartItems().forEach { setChecked(it.cart.id, checked) }
    }

    suspend fun updateQuantity(id: String, quantity: Int) {
        if (quantity <= 0) delete(id) else client.from("cart_items").update({ set("quantity", quantity) }) { filter { eq("id", id) } }
    }

    suspend fun delete(id: String) {
        client.from("cart_items").delete { filter { eq("id", id) } }
    }
}
