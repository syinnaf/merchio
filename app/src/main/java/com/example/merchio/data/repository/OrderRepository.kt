package com.example.merchio.data.repository

import com.example.merchio.data.model.CancelOrderParams
import com.example.merchio.data.model.OrderDisplayItem
import com.example.merchio.data.model.OrderDto
import com.example.merchio.data.model.OrderItemDto
import com.example.merchio.data.model.RpcResponse
import com.example.merchio.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest

class OrderRepository {
    private val client = SupabaseClientProvider.client
    private fun userId(): String = client.auth.currentUserOrNull()?.id ?: error("Belum login")

    suspend fun myOrders(): List<OrderDisplayItem> {
        val orders = client.from("orders")
            .select { filter { eq("user_id", userId()) } }
            .decodeList<OrderDto>()
            .sortedByDescending { it.createdAt ?: "" }
        return orders.map { order -> OrderDisplayItem(order, orderItems(order.id)) }
    }

    suspend fun orderById(id: String): OrderDisplayItem {
        val order = client.from("orders").select { filter { eq("id", id) } }.decodeSingle<OrderDto>()
        return OrderDisplayItem(order, orderItems(order.id))
    }

    suspend fun orderItems(orderId: String): List<OrderItemDto> = client.from("order_items")
        .select { filter { eq("order_id", orderId) } }
        .decodeList<OrderItemDto>()

    suspend fun cancelOrder(orderId: String): RpcResponse {
        return client.postgrest.rpc("cancel_order", CancelOrderParams(orderId)).decodeAs<RpcResponse>()
    }

    suspend fun summaryCounts(): Triple<Int, Int, Int> {
        val orders = myOrders().map { it.order }
        return Triple(
            orders.count { it.status == "packing" },
            orders.count { it.status == "shipping" },
            orders.count { it.status == "delivered" }
        )
    }
}
