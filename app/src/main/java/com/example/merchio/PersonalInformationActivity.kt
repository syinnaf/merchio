package com.example.merchio

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.merchio.data.repository.ProfileRepository
import com.example.merchio.utils.textString
import com.example.merchio.utils.toast
import android.widget.EditText
import kotlinx.coroutines.launch

class PersonalInformationActivity : AppCompatActivity() {
    private val repository = ProfileRepository()
    private lateinit var nameInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var emailText: TextView
    private var avatarUrl: String? = null

    private val pickAvatar = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri ?: return@registerForActivityResult
        lifecycleScope.launch {
            runCatching { repository.uploadAvatar(uri, contentResolver) }
                .onSuccess { avatarUrl = it; toast("Avatar berhasil diupload") }
                .onFailure { toast(it.message ?: "Upload avatar gagal") }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_information)
        findViewById<android.view.View>(R.id.btn_back).setOnClickListener { finish() }
        nameInput = findViewById(R.id.edt_name)
        usernameInput = findViewById(R.id.edt_username)
        phoneInput = findViewById(R.id.edt_phone)
        emailText = findViewById(R.id.tv_email)
        findViewById<android.view.View>(R.id.btn_save).setOnClickListener { save() }
        emailText.setOnClickListener { pickAvatar.launch("image/*") }
        load()
    }

    private fun load() {
        lifecycleScope.launch {
            runCatching { repository.profile() }
                .onSuccess {
                    nameInput.setText(it.fullName.orEmpty())
                    usernameInput.setText(it.username.orEmpty())
                    phoneInput.setText(it.phone.orEmpty())
                    emailText.text = it.email.orEmpty()
                    avatarUrl = it.avatarUrl
                }
                .onFailure { toast(it.message ?: "Gagal load profile") }
        }
    }

    private fun save() {
        lifecycleScope.launch {
            runCatching { repository.updateProfile(nameInput.textString(), usernameInput.textString(), phoneInput.textString(), avatarUrl) }
                .onSuccess { toast("Profile tersimpan"); finish() }
                .onFailure { toast(it.message ?: "Gagal simpan profile") }
        }
    }
}
