package com.example.merchio.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.example.merchio.data.model.AddressDto
import com.example.merchio.data.model.AddressInsertDto
import com.example.merchio.data.model.ProfileDto
import com.example.merchio.data.model.SettingsDto
import com.example.merchio.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class ProfileRepository {
    private val client = SupabaseClientProvider.client
    private val storageRepository = StorageRepository()

    fun userId(): String = client.auth.currentUserOrNull()?.id ?: error("Belum login")

    suspend fun profile(): ProfileDto = client.from("profiles")
        .select { filter { eq("id", userId()) } }
        .decodeSingle()

    suspend fun updateProfile(fullName: String, username: String, phone: String, avatarUrl: String? = null) {
        client.from("profiles").update({
            set("full_name", fullName)
            set("username", username)
            set("phone", phone)
            if (!avatarUrl.isNullOrBlank()) set("avatar_url", avatarUrl)
        }) {
            filter { eq("id", userId()) }
        }
    }

    suspend fun uploadAvatar(uri: Uri, resolver: ContentResolver): String {
        val url = storageRepository.uploadAvatar(userId(), uri, resolver)
        client.from("profiles").update({ set("avatar_url", url) }) {
            filter { eq("id", userId()) }
        }
        return url
    }

    suspend fun addresses(): List<AddressDto> = client.from("addresses")
        .select { filter { eq("user_id", userId()) } }
        .decodeList<AddressDto>()
        .sortedByDescending { it.isDefault }

    suspend fun ensureDefaultAddress(): AddressDto {
        addresses().firstOrNull()?.let { return it }
        val inserted = client.from("addresses").insert(
            AddressInsertDto(
                userId = userId(),
                recipientName = "Merchio Customer",
                phone = "081234567890",
                addressLine = "Isi alamat kamu di Personal Information",
                city = "Jakarta",
                province = "DKI Jakarta",
                postalCode = "00000",
                isDefault = true
            )
        ) { select() }.decodeSingle<AddressDto>()
        return inserted
    }

    suspend fun saveDefaultAddress(recipient: String, phone: String, address: String, city: String, province: String, postal: String) {
        val existing = addresses().firstOrNull()
        if (existing == null) {
            client.from("addresses").insert(AddressInsertDto(userId(), recipient, phone, address, city, province, postal, true))
        } else {
            client.from("addresses").update({
                set("recipient_name", recipient)
                set("phone", phone)
                set("address_line", address)
                set("city", city)
                set("province", province)
                set("postal_code", postal)
                set("is_default", true)
            }) { filter { eq("id", existing.id) } }
        }
    }

    suspend fun settings(): SettingsDto? = runCatching {
        client.from("settings").select { filter { eq("user_id", userId()) } }.decodeSingle<SettingsDto>()
    }.getOrNull()

    suspend fun updateSettings(defaultPayment: String, darkMode: Boolean, push: Boolean) {
        client.from("settings").update({
            set("default_payment_method", defaultPayment)
            set("dark_mode", darkMode)
            set("push_notification", push)
        }) { filter { eq("user_id", userId()) } }
    }
}
