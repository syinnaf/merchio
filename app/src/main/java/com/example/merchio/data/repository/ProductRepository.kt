package com.example.merchio.data.repository

import com.example.merchio.data.model.CategoryDto
import com.example.merchio.data.model.ProductDto
import com.example.merchio.data.model.ProductWriteDto
import com.example.merchio.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from

class ProductRepository {
    private val client = SupabaseClientProvider.client

    suspend fun categories(includeInactive: Boolean = false): List<CategoryDto> {
        val data = client.from("categories").select().decodeList<CategoryDto>()
        return data.filter { includeInactive || it.isActive }.sortedBy { it.name }
    }

    suspend fun activeProducts(): List<ProductDto> = client.from("products")
        .select { filter { eq("is_active", true) } }
        .decodeList<ProductDto>()
        .sortedWith(compareByDescending<ProductDto> { it.isPaidPromote }.thenBy { it.name })

    suspend fun allProductsForAdmin(): List<ProductDto> = client.from("products")
        .select()
        .decodeList<ProductDto>()
        .sortedBy { it.name }

    suspend fun productById(id: String): ProductDto = client.from("products")
        .select { filter { eq("id", id) } }
        .decodeSingle()

    suspend fun search(query: String): List<ProductDto> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return activeProducts()
        return activeProducts().filter {
            it.name.lowercase().contains(q) ||
                (it.brand ?: "").lowercase().contains(q) ||
                (it.description ?: "").lowercase().contains(q)
        }
    }

    suspend fun addProduct(product: ProductWriteDto) {
        client.from("products").insert(product)
    }

    suspend fun updateProduct(id: String, product: ProductWriteDto) {
        client.from("products").update({
            set("category_id", product.categoryId)
            set("name", product.name)
            set("brand", product.brand)
            set("description", product.description)
            set("price", product.price)
            set("stock", product.stock)
            set("image_url", product.imageUrl)
            set("is_active", product.isActive)
            set("is_paid_promote", product.isPaidPromote)
        }) { filter { eq("id", id) } }
    }

    suspend fun setProductActive(id: String, active: Boolean) {
        client.from("products").update({ set("is_active", active) }) { filter { eq("id", id) } }
    }
}
