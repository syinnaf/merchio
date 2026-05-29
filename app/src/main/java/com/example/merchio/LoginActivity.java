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

public class LoginActivity extends AppCompatActivity {

    EditText edtEmailLogin, edtPasswordLogin;
    Button btnLogin;
    TextView txtGoRegister, txtForgotPassword;

    DbHelper dbHelper;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DbHelper(this);
        dbHelper.getWritableDatabase();
        sessionManager = new SessionManager(this);

        dbHelper.ensureRoleColumnExists();
        dbHelper.createDefaultAdminIfNeeded();

        if (sessionManager.isLoggedIn()) {
            int savedUserId = sessionManager.getUserId();
            String role = dbHelper.getUserRole(savedUserId);

            Intent intent;

            if ("admin".equalsIgnoreCase(role)) {
                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, MainActivity.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        edtEmailLogin = findViewById(R.id.edtEmailLogin);
        edtPasswordLogin = findViewById(R.id.edtPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        txtGoRegister = findViewById(R.id.txtGoRegister);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);

        btnLogin.setOnClickListener(v -> loginUser());

        txtGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        txtForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Fitur forgot password belum dibuat", Toast.LENGTH_SHORT).show();
        });
    }

    private void loginUser() {
        String email = edtEmailLogin.getText().toString().trim();
        String password = edtPasswordLogin.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edtEmailLogin.setError("Email wajib diisi");
            edtEmailLogin.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmailLogin.setError("Format email tidak valid");
            edtEmailLogin.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPasswordLogin.setError("Password wajib diisi");
            edtPasswordLogin.requestFocus();
            return;
        }

        if (password.length() < 8) {
            edtPasswordLogin.setError("Password minimal 8 karakter");
            edtPasswordLogin.requestFocus();
            return;
        }

        int userId = dbHelper.loginUser(email, password);

        if (userId != -1) {
            sessionManager.saveLogin(userId, email);

            Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();

            String role = dbHelper.getUserRole(userId);

            Intent intent;

            if ("admin".equalsIgnoreCase(role)) {
                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, MainActivity.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Email atau password salah", Toast.LENGTH_SHORT).show();
        }
    }
}