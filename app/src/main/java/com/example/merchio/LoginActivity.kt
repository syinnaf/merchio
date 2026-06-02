package com.example.merchio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.merchio.data.repository.AuthRepository
import com.example.merchio.utils.textString
import com.example.merchio.utils.toast
import android.widget.EditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val authRepository = AuthRepository()
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: View
    private lateinit var registerText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.edtEmailLogin)
        passwordInput = findViewById(R.id.edtPasswordLogin)
        loginButton = findViewById(R.id.btnLogin)
        registerText = findViewById(R.id.txtGoRegister)

        registerText.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        loginButton.setOnClickListener { login() }
    }

    private fun login() {
        val email = emailInput.textString()
        val password = passwordInput.textString()
        if (email.isBlank()) { emailInput.error = "Email wajib diisi"; return }
        if (password.isBlank()) { passwordInput.error = "Password wajib diisi"; return }

        loginButton.isEnabled = false
        lifecycleScope.launch {
            runCatching { authRepository.signIn(email, password) }
                .onSuccess { profile ->
                    toast("Login berhasil")
                    val target = if (profile.role == "admin") AdminDashboardActivity::class.java else MainActivity::class.java
                    startActivity(Intent(this@LoginActivity, target))
                    finishAffinity()
                }
                .onFailure { toast(it.message ?: "Login gagal") }
            loginButton.isEnabled = true
        }
    }
}
