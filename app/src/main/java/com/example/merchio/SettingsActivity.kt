package com.example.merchio

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.merchio.data.repository.AuthRepository
import com.example.merchio.data.repository.ProfileRepository
import com.example.merchio.utils.loadMerchioImage
import com.example.merchio.utils.toast
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private val authRepository = AuthRepository()
    private val profileRepository = ProfileRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        findViewById<android.view.View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<android.view.View>(R.id.menu_personal_info).setOnClickListener { startActivity(Intent(this, PersonalInformationActivity::class.java)) }
        findViewById<android.view.View>(R.id.menu_payment).setOnClickListener { startActivity(Intent(this, PaymentMethodActivity::class.java)) }
        findViewById<android.view.View>(R.id.menu_logout).setOnClickListener { logout() }
        loadProfile()
    }

    override fun onResume() { super.onResume(); loadProfile() }

    private fun loadProfile() {
        lifecycleScope.launch {
            runCatching { authRepository.currentProfileOrNull(true) }
                .onSuccess { profile ->
                    profile ?: return@onSuccess
                    findViewById<TextView>(R.id.tv_name).text = profile.displayName
                    findViewById<TextView>(R.id.tv_username).text = profile.email ?: ""
                    findViewById<ImageView>(R.id.img_avatar).loadMerchioImage(profile.avatarUrl)
                }
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            runCatching { authRepository.signOut() }
                .onSuccess { startActivity(Intent(this@SettingsActivity, LoginActivity::class.java)); finishAffinity() }
                .onFailure { toast(it.message ?: "Logout gagal") }
        }
    }
}
