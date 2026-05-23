package com.example.merchio;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.merchio.db.DbHelper;

public class RegisterActivity extends AppCompatActivity {

    EditText edtEmailRegister, edtPasswordRegister, edtUsername, edtConfirmPassword;
    Button btnRegister;
    TextView txtGoLogin;

    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DbHelper(this);

        edtEmailRegister = findViewById(R.id.edtEmailRegister);
        edtPasswordRegister = findViewById(R.id.edtPasswordRegister);
        edtUsername = findViewById(R.id.edtUsername);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtGoLogin = findViewById(R.id.txtGoLogin);

        btnRegister.setOnClickListener(v -> registerUser());

        txtGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String email = edtEmailRegister.getText().toString().trim();
        String password = edtPasswordRegister.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edtEmailRegister.setError("Email wajib diisi");
            edtEmailRegister.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmailRegister.setError("Format email tidak valid");
            edtEmailRegister.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPasswordRegister.setError("Password wajib diisi");
            edtPasswordRegister.requestFocus();
            return;
        }

        if (password.length() < 6) {
            edtPasswordRegister.setError("Password minimal 6 karakter");
            edtPasswordRegister.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            edtUsername.setError("Username wajib diisi");
            edtUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            edtConfirmPassword.setError("Konfirmasi password wajib diisi");
            edtConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Password tidak sama");
            edtConfirmPassword.requestFocus();
            return;
        }

        if (dbHelper.isEmailExists(email)) {
            edtEmailRegister.setError("Email sudah terdaftar");
            edtEmailRegister.requestFocus();
            return;
        }

        // Karena form kamu belum punya field name, sementara name diisi sama dengan username.
        boolean success = dbHelper.insertUser(username, username, email, password);

        if (success) {
            Toast.makeText(this, "Register berhasil, silakan login", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Register gagal", Toast.LENGTH_SHORT).show();
        }
    }
}