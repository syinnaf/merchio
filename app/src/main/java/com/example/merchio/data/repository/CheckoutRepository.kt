package com.example.merchio.data.repository

import com.example.merchio.data.model.CheckoutBuyNowParams
import com.example.merchio.data.model.CheckoutCartParams
import com.example.merchio.data.model.RpcResponse
import com.example.merchio.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest

class CheckoutRepository {
    private val client = SupabaseClientProvider.client

    suspend fun checkoutCart(
        cartItemIds: List<String>,
        addressId: String,
        paymentMethod: String,
        shippingMethod: String,
        shippingFee: Double
    ): RpcResponse {
        return client.postgrest.rpc(
            function = "checkout_cart",
            parameters = CheckoutCartParams(cartItemIds, addressId, paymentMethod, shippingMethod, shippingFee)
        ).decodeAs<RpcResponse>()
    }

    suspend fun checkoutBuyNow(
        productId: String,
        variantLabel: String,
        quantity: Int,
        addressId: String,
        paymentMethod: String,
        shippingMethod: String,
        shippingFee: Double
    ): RpcResponse {
        return client.postgrest.rpc(
            function = "checkout_buy_now",
            parameters = CheckoutBuyNowParams(productId, variantLabel, quantity, addressId, paymentMethod, shippingMethod, shippingFee)
        ).decodeAs<RpcResponse>()
    }
}
