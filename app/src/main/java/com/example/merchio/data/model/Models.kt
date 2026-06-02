package com.example.merchio.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    @SerialName("full_name") val fullName: String? = null,
    val username: String? = null,
    val email: String? = null,
    val phone: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val role: String = "user",
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
) {
    val displayName: String get() = fullName?.takeIf { it.isNotBlank() } ?: username ?: email ?: "Merchio User"
}

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val description: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class ProductDto(
    val id: String,
    @SerialName("category_id") val categoryId: String? = null,
    val name: String,
    val brand: String? = null,
    val description: String? = null,
    val price: Double,
    val stock: Int,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("is_paid_promote") val isPaidPromote: Boolean = false,
    @SerialName("sold_count") val soldCount: Int = 0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class ProductWriteDto(
    @SerialName("category_id") val categoryId: String? = null,
    val name: String,
    val brand: String? = null,
    val description: String? = null,
    val price: Double,
    val stock: Int,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("is_paid_promote") val isPaidPromote: Boolean = false
)

@Serializable
data class CartItemDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("variant_label") val variantLabel: String = "Default",
    val quantity: Int,
    @SerialName("is_checked") val isChecked: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class CartItemInsertDto(
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("variant_label") val variantLabel: String = "Default",
    val quantity: Int,
    @SerialName("is_checked") val isChecked: Boolean = true
)

data class CartDisplayItem(
    val cart: CartItemDto,
    val product: ProductDto
) {
    val lineTotal: Double get() = cart.quantity * product.price
}

@Serializable
data class AddressDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("recipient_name") val recipientName: String,
    val phone: String,
    @SerialName("address_line") val addressLine: String,
    val city: String? = null,
    val province: String? = null,
    @SerialName("postal_code") val postalCode: String? = null,
    @SerialName("is_default") val isDefault: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
) {
    fun singleLine(): String = listOf(recipientName, phone, addressLine, city, province, postalCode)
        .filterNot { it.isNullOrBlank() }
        .joinToString(" • ")
}

@Serializable
data class AddressInsertDto(
    @SerialName("user_id") val userId: String,
    @SerialName("recipient_name") val recipientName: String,
    val phone: String,
    @SerialName("address_line") val addressLine: String,
    val city: String? = null,
    val province: String? = null,
    @SerialName("postal_code") val postalCode: String? = null,
    @SerialName("is_default") val isDefault: Boolean = true
)

@Serializable
data class OrderDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("order_code") val orderCode: String,
    @SerialName("address_id") val addressId: String? = null,
    @SerialName("payment_method") val paymentMethod: String,
    @SerialName("shipping_method") val shippingMethod: String = "Standard",
    val subtotal: Double,
    @SerialName("shipping_fee") val shippingFee: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double,
    val status: String = "packing",
    @SerialName("estimated_arrival") val estimatedArrival: String? = null,
    @SerialName("is_received") val isReceived: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class OrderItemDto(
    val id: String,
    @SerialName("order_id") val orderId: String,
    @SerialName("product_id") val productId: String? = null,
    @SerialName("product_name") val productName: String,
    @SerialName("product_image_url") val productImageUrl: String? = null,
    @SerialName("variant_label") val variantLabel: String = "Default",
    val price: Double,
    val quantity: Int,
    @SerialName("created_at") val createdAt: String? = null
)

data class OrderDisplayItem(
    val order: OrderDto,
    val items: List<OrderItemDto>
)

@Serializable
data class SettingsDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("default_payment_method") val defaultPaymentMethod: String? = "bank_transfer",
    @SerialName("dark_mode") val darkMode: Boolean = false,
    @SerialName("push_notification") val pushNotification: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class CheckoutCartParams(
    @SerialName("p_cart_item_ids") val cartItemIds: List<String>,
    @SerialName("p_address_id") val addressId: String,
    @SerialName("p_payment_method") val paymentMethod: String,
    @SerialName("p_shipping_method") val shippingMethod: String = "Standard",
    @SerialName("p_shipping_fee") val shippingFee: Double = 10000.0
)

@Serializable
data class CheckoutBuyNowParams(
    @SerialName("p_product_id") val productId: String,
    @SerialName("p_variant_label") val variantLabel: String = "Default",
    @SerialName("p_quantity") val quantity: Int,
    @SerialName("p_address_id") val addressId: String,
    @SerialName("p_payment_method") val paymentMethod: String,
    @SerialName("p_shipping_method") val shippingMethod: String = "Standard",
    @SerialName("p_shipping_fee") val shippingFee: Double = 10000.0
)

@Serializable
data class CancelOrderParams(
    @SerialName("p_order_id") val orderId: String
)

@Serializable
data class AdminUpdateOrderStatusParams(
    @SerialName("p_order_id") val orderId: String,
    @SerialName("p_status") val status: String
)

@Serializable
data class RpcResponse(
    val success: Boolean = false,
    val message: String? = null,
    @SerialName("order_id") val orderId: String? = null,
    @SerialName("order_code") val orderCode: String? = null
)

data class DashboardStats(
    val totalUsers: Int,
    val totalOrders: Int,
    val packing: Int,
    val shipping: Int,
    val delivered: Int,
    val cancelled: Int,
    val revenue: Double
)
