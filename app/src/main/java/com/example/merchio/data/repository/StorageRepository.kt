package com.example.merchio.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.example.merchio.data.remote.SupabaseClientProvider
import io.github.jan.supabase.storage.storage
import java.util.UUID

class StorageRepository {
    private val client = SupabaseClientProvider.client

    suspend fun uploadAvatar(userId: String, uri: Uri, resolver: ContentResolver): String {
        val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: error("File avatar tidak bisa dibaca")
        val path = "$userId/avatar-${System.currentTimeMillis()}.jpg"
        val bucket = client.storage.from("avatars")
        bucket.upload(path, bytes) { upsert = true }
        return bucket.publicUrl(path)
    }

    suspend fun uploadProductImage(productName: String, uri: Uri, resolver: ContentResolver): String {
        val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: error("File produk tidak bisa dibaca")
        val safeName = productName.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-').ifBlank { "product" }
        val path = "$safeName-${UUID.randomUUID()}.jpg"
        val bucket = client.storage.from("products")
        bucket.upload(path, bytes) { upsert = true }
        return bucket.publicUrl(path)
    }
}
