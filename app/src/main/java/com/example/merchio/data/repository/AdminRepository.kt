package com.example.merchio.data.repository

import com.example.merchio.data.model.AdminUpdateOrderStatusParams
import com.example.merchio.data.model.DashboardStats
import com.example.merchio.data.model.OrderDisplayItem
import com.example.merchio.data.model.OrderDto
import com.example.merchio.data.model.OrderItemDto
import com.example.merchio.data.model.ProfileDto
import com.example.merchio.data.model.RpcResponse
import com.example.merchio.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest

class AdminRepository {
    private val client = SupabaseClientProvider.client

    suspend fun users(): List<ProfileDto> = client.from("profiles")
        .select()
        .decodeList<ProfileDto>()
        .sortedBy { it.email ?: it.username ?: it.id }

    suspend fun setUserActive(userId: String, active: Boolean) {
        client.from("profiles").update({ set("is_active", active) }) { filter { eq("id", userId) } }
    }

    suspend fun updateUser(user: ProfileDto, fullName: String, username: String, phone: String, role: String, active: Boolean) {
        client.from("profiles").update({
            set("full_name", fullName)
            set("username", username)
            set("phone", phone)
            set("role", role)
            set("is_active", active)
        }) { filter { eq("id", user.id) } }
    }

    suspend fun allOrders(): List<OrderDisplayItem> {
        val orders = client.from("orders")
            .select()
            .decodeList<OrderDto>()
            .sortedByDescending { it.createdAt ?: "" }
        return orders.map { OrderDisplayItem(it, orderItems(it.id)) }
    }

    suspend fun orderItems(orderId: String): List<OrderItemDto> = client.from("order_items")
        .select { filter { eq("order_id", orderId) } }
        .decodeList<OrderItemDto>()

    suspend fun updateOrderStatus(orderId: String, status: String): RpcResponse {
        return client.postgrest.rpc("admin_update_order_status", AdminUpdateOrderStatusParams(orderId, status)).decodeAs<RpcResponse>()
    }

    suspend fun dashboardStats(): DashboardStats {
        val users = users()
        val orders = client.from("orders").select().decodeList<OrderDto>()
        return DashboardStats(
            totalUsers = users.count { it.role == "user" },
            totalOrders = orders.size,
            packing = orders.count { it.status == "packing" },
            shipping = orders.count { it.status == "shipping" },
            delivered = orders.count { it.status == "delivered" },
            cancelled = orders.count { it.status == "cancelled" },
            revenue = orders.filter { it.status != "cancelled" }.sumOf { it.total }
        )
    }
}
