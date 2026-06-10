package com.example.merchio;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.merchio.db.DbHelper;

public class SettingsActivity extends AppCompatActivity {

    private TextView btnBack, tvName, tvUsername;
    private TextView menuPersonalInfo, menuPayment, menuLogout;
    private ImageView imgAvatar;

    private DbHelper dbHelper;
    private SessionManager sessionManager;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        initHelpers();
        loadUserData();
        setupClicks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvName = findViewById(R.id.tv_name);
        tvUsername = findViewById(R.id.tv_username);
        imgAvatar = findViewById(R.id.img_avatar);

        menuPersonalInfo = findViewById(R.id.menu_personal_info);
        menuPayment = findViewById(R.id.menu_payment);
        menuLogout = findViewById(R.id.menu_logout);
    }

    private void initHelpers() {
        dbHelper = new DbHelper(this);
        dbHelper.ensureUserImageColumnsExist();
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
    }

    private void loadUserData() {
        if (userId == -1) {
            tvName.setText("Guest");
            tvUsername.setText("@guest");
            imgAvatar.setImageResource(R.drawable.logo_merchio);
            return;
        }

        Cursor cursor = dbHelper.getUserById(userId);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String avatar = cursor.getString(cursor.getColumnIndexOrThrow("avatar"));

            tvName.setText(!TextUtils.isEmpty(name) ? name : "Merchio User");

            if (!TextUtils.isEmpty(username)) {
                tvUsername.setText("@" + username);
            } else {
                tvUsername.setText(email);
            }

            if (!TextUtils.isEmpty(avatar)) {
                Glide.with(this)
                        .load(avatar)
                        .placeholder(R.drawable.logo_merchio)
                        .error(R.drawable.logo_merchio)
                        .into(imgAvatar);
            } else {
                imgAvatar.setImageResource(R.drawable.logo_merchio);
            }
        }

        cursor.close();
    }

    private void setupClicks() {
        btnBack.setOnClickListener(v -> finish());

        menuPersonalInfo.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, PersonalInformationActivity.class);
            startActivity(intent);
        });

        menuPayment.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, PaymentMethodActivity.class);
            startActivity(intent);
        });

        menuLogout.setOnClickListener(v -> {
            sessionManager.logout();

            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
