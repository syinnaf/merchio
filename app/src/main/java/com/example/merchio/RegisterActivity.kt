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

class RegisterActivity : AppCompatActivity() {
    private val authRepository = AuthRepository()
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var confirmInput: EditText
    private lateinit var registerButton: View
    private lateinit var loginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        emailInput = findViewById(R.id.edtEmailRegister)
        passwordInput = findViewById(R.id.edtPasswordRegister)
        usernameInput = findViewById(R.id.edtUsername)
        confirmInput = findViewById(R.id.edtConfirmPassword)
        registerButton = findViewById(R.id.btnRegister)
        loginText = findViewById(R.id.txtGoLogin)
        loginText.setOnClickListener { finish() }
        registerButton.setOnClickListener { register() }
    }

    private fun register() {
        val email = emailInput.textString()
        val username = usernameInput.textString()
        val password = passwordInput.textString()
        val confirm = confirmInput.textString()
        if (email.isBlank()) { emailInput.error = "Email wajib diisi"; return }
        if (username.isBlank()) { usernameInput.error = "Username wajib diisi"; return }
        if (password.length < 6) { passwordInput.error = "Minimal 6 karakter"; return }
        if (password != confirm) { confirmInput.error = "Password tidak sama"; return }

        registerButton.isEnabled = false
        lifecycleScope.launch {
            runCatching { authRepository.signUp(email, password, username) }
                .onSuccess {
                    toast("Register berhasil")
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finishAffinity()
                }
                .onFailure { toast(it.message ?: "Register gagal") }
            registerButton.isEnabled = true
        }
    }
}
