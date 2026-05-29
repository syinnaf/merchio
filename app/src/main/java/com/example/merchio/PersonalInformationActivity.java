package com.example.merchio;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.merchio.db.DbHelper;

public class PersonalInformationActivity extends AppCompatActivity {

    private TextView btnBack, tvEmail;
    private EditText edtName, edtUsername, edtPhone;
    private Button btnSave;

    private DbHelper dbHelper;
    private SessionManager sessionManager;

    private int userId = -1;
    private String currentAvatar = "";
    private String currentHeader = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        initViews();
        initHelpers();
        loadUserData();
        setupClicks();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvEmail = findViewById(R.id.tv_email);

        edtName = findViewById(R.id.edt_name);
        edtUsername = findViewById(R.id.edt_username);
        edtPhone = findViewById(R.id.edt_phone);

        btnSave = findViewById(R.id.btn_save);
    }

    private void initHelpers() {
        dbHelper = new DbHelper(this);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
    }

    private void loadUserData() {
        if (userId == -1) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Cursor cursor = dbHelper.getUserById(userId);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

            currentAvatar = cursor.getString(cursor.getColumnIndexOrThrow("avatar"));
            currentHeader = cursor.getString(cursor.getColumnIndexOrThrow("header"));

            edtName.setText(name);
            edtUsername.setText(username);
            edtPhone.setText(phone);
            tvEmail.setText("Email: " + email);
        }

        cursor.close();
    }

    private void setupClicks() {
        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String name = edtName.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            edtName.setError("Nama tidak boleh kosong");
            edtName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            edtUsername.setError("Username tidak boleh kosong");
            edtUsername.requestFocus();
            return;
        }

        if (username.length() < 3) {
            edtUsername.setError("Username minimal 3 karakter");
            edtUsername.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(phone) && phone.length() < 10) {
            edtPhone.setError("Nomor telepon minimal 10 digit");
            edtPhone.requestFocus();
            return;
        }

        boolean success = dbHelper.updateUserProfile(
                userId,
                name,
                username,
                phone,
                currentAvatar,
                currentHeader
        );

        if (success) {
            Toast.makeText(this, "Profile berhasil diperbarui", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Profile gagal diperbarui", Toast.LENGTH_SHORT).show();
        }
    }
}