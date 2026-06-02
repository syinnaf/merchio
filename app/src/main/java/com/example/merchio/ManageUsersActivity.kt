package com.example.merchio

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.merchio.adapters.AdminUserAdapter
import com.example.merchio.data.model.ProfileDto
import com.example.merchio.data.repository.AdminRepository
import com.example.merchio.utils.textString
import com.example.merchio.utils.toast
import com.example.merchio.utils.visible
import kotlinx.coroutines.launch

class ManageUsersActivity : AppCompatActivity() {
    private val repository = AdminRepository()
    private lateinit var adapter: AdminUserAdapter
    private lateinit var empty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_users)
        findViewById<android.view.View>(R.id.btn_back).setOnClickListener { finish() }
        empty = findViewById(R.id.tv_empty_users)
        adapter = AdminUserAdapter { showEditDialog(it) }
        findViewById<RecyclerView>(R.id.rv_users).layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.rv_users).adapter = adapter
        load()
    }

    override fun onResume() { super.onResume(); if (::adapter.isInitialized) load() }

    private fun load() {
        lifecycleScope.launch {
            runCatching { repository.users() }
                .onSuccess { data -> adapter.submitList(data); empty.visible(data.isEmpty()) }
                .onFailure { toast(it.message ?: "Gagal load users") }
        }
    }

    private fun showEditDialog(user: ProfileDto) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null)
        val name = view.findViewById<EditText>(R.id.edt_user_name)
        val username = view.findViewById<EditText>(R.id.edt_username)
        val phone = view.findViewById<EditText>(R.id.edt_phone)
        val email = view.findViewById<EditText>(R.id.edt_email)
        val rbActive = view.findViewById<RadioButton>(R.id.rb_active)
        val rbInactive = view.findViewById<RadioButton>(R.id.rb_inactive)
        name?.setText(user.fullName.orEmpty())
        username?.setText(user.username.orEmpty())
        phone?.setText(user.phone.orEmpty())
        email.setText(user.email.orEmpty())
        email.isEnabled = false
        rbActive.isChecked = user.isActive
        rbInactive.isChecked = !user.isActive
        AlertDialog.Builder(this)
            .setTitle(user.email ?: "Edit User")
            .setView(view)
            .setPositiveButton("Simpan") { _, _ ->
                lifecycleScope.launch {
                    runCatching {
                        repository.updateUser(
                            user,
                            name.textString(),
                            username.textString(),
                            phone.textString(),
                            user.role,
                            rbActive.isChecked
                        )
                    }.onSuccess { toast("User tersimpan"); load() }
                        .onFailure { toast(it.message ?: "Gagal simpan") }
                }
            }
            .setNegativeButton("Tutup", null)
            .show()
    }
}
