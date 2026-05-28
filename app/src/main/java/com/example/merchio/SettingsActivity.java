package com.example.merchio;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.example.merchio.db.DbHelper;

public class SettingsActivity extends AppCompatActivity {

    private TextView btnBack, tvName, tvUsername;
    private TextView menuPersonalInfo, menuPayment, menuPush, menuLogout;
    private ImageView imgAvatar;
    private Switch switchDarkMode;

    private DbHelper dbHelper;
    private SessionManager sessionManager;
    private int userId = -1;

    private boolean isInitializingSwitch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        initHelpers();
        loadUserData();
        loadDarkMode();
        setupClicks();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvName = findViewById(R.id.tv_name);
        tvUsername = findViewById(R.id.tv_username);
        imgAvatar = findViewById(R.id.img_avatar);

        menuPersonalInfo = findViewById(R.id.menu_personal_info);
        menuPayment = findViewById(R.id.menu_payment);
        menuPush = findViewById(R.id.menu_push);
        menuLogout = findViewById(R.id.menu_logout);

        switchDarkMode = findViewById(R.id.switch_dark_mode);
    }

    private void initHelpers() {
        dbHelper = new DbHelper(this);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
    }

    private void loadUserData() {
        if (userId == -1) {
            tvName.setText("Guest");
            tvUsername.setText("@guest");
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
                        .into(imgAvatar);
            }
        }

        cursor.close();
    }

    private void loadDarkMode() {
        if (userId == -1) {
            return;
        }

        isInitializingSwitch = true;
        boolean darkMode = dbHelper.getDarkModeSetting(userId);
        switchDarkMode.setChecked(darkMode);
        isInitializingSwitch = false;
    }

    private void setupClicks() {
        btnBack.setOnClickListener(v -> finish());

        menuPersonalInfo.setOnClickListener(v ->
                Toast.makeText(this, "Personal Information nanti dibuat", Toast.LENGTH_SHORT).show()
        );

        menuPayment.setOnClickListener(v ->
                Toast.makeText(this, "Payment Method nanti dibuat", Toast.LENGTH_SHORT).show()
        );

        menuPush.setOnClickListener(v ->
                Toast.makeText(this, "Push Notification nanti dibuat", Toast.LENGTH_SHORT).show()
        );

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isInitializingSwitch || userId == -1) {
                return;
            }

            dbHelper.saveDarkModeSetting(userId, isChecked);

            AppCompatDelegate.setDefaultNightMode(
                    isChecked
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
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