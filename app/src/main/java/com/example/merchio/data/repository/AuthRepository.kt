package com.example.merchio.data.repository

import com.example.merchio.data.model.ProfileDto
import com.example.merchio.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository {
    private val client = SupabaseClientProvider.client

    suspend fun signIn(email: String, password: String): ProfileDto {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        val profile = currentProfileOrNull(retry = true) ?: error("Profile tidak ditemukan")
        if (!profile.isActive) {
            signOut()
            error("Akun kamu sedang dinonaktifkan admin")
        }
        return profile
    }

    suspend fun signUp(email: String, password: String, username: String): ProfileDto? {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("username", username)
                put("full_name", username)
            }
        }
        return currentProfileOrNull(retry = true)
    }

    suspend fun signOut() {
        client.auth.signOut()
    }

    fun currentUserId(): String? = client.auth.currentUserOrNull()?.id

    suspend fun currentProfileOrNull(retry: Boolean = false): ProfileDto? {
        val userId = currentUserId() ?: return null
        repeat(if (retry) 5 else 1) { attempt ->
            runCatching {
                return client.from("profiles")
                    .select { filter { eq("id", userId) } }
                    .decodeSingle<ProfileDto>()
            }
            if (attempt < 4) delay(500)
        }
        return null
    }
}
