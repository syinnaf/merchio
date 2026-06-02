package com.example.merchio

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.merchio.data.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(900)
            val profile = authRepository.currentProfileOrNull(retry = true)
            when {
                profile == null -> open(LoginActivity::class.java)
                !profile.isActive -> {
                    authRepository.signOut()
                    open(LoginActivity::class.java)
                }
                profile.role == "admin" -> open(AdminDashboardActivity::class.java)
                else -> open(MainActivity::class.java)
            }
        }
    }

    private fun open(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
        finish()
    }
}
