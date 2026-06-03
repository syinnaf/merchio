package com.example.merchio;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchio.adapters.AdminUserAdapter;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.AdminUser;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private TextView btnBack, tvEmpty;
    private RecyclerView rvUsers;

    private DbHelper dbHelper;
    private AdminUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        dbHelper = new DbHelper(this);
        dbHelper.ensureUserActiveColumnExists();

        btnBack = findViewById(R.id.btn_back);
        tvEmpty = findViewById(R.id.tv_empty_users);
        rvUsers = findViewById(R.id.rv_users);

        btnBack.setOnClickListener(v -> finish());

        adapter = new AdminUserAdapter(this::showEditUserDialog);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);

        loadUsers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    private void loadUsers() {
        List<AdminUser> users = new ArrayList<>();

        Cursor cursor = dbHelper.getAllUsersForAdmin();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            boolean active = cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1;

            users.add(new AdminUser(id, name, username, email, phone, active));
        }

        cursor.close();

        adapter.setUsers(users);
        tvEmpty.setVisibility(users.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void showEditUserDialog(AdminUser user) {
        android.view.View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_edit_user, null, false);

        EditText edtName = view.findViewById(R.id.edt_user_name);
        EditText edtUsername = view.findViewById(R.id.edt_username);
        EditText edtEmail = view.findViewById(R.id.edt_email);
        EditText edtPhone = view.findViewById(R.id.edt_phone);
        RadioGroup rgStatus = view.findViewById(R.id.rg_user_status);
        RadioButton rbActive = view.findViewById(R.id.rb_active);
        RadioButton rbInactive = view.findViewById(R.id.rb_inactive);
        TextView btnCancel = view.findViewById(R.id.btn_cancel_user);
        TextView btnSave = view.findViewById(R.id.btn_save_user);

        edtName.setText(value(user.getName()));
        edtUsername.setText(value(user.getUsername()));
        edtEmail.setText(value(user.getEmail()));
        edtPhone.setText(value(user.getPhone()));
        rbActive.setChecked(user.isActive());
        rbInactive.setChecked(!user.isActive());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String username = edtUsername.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            boolean active = rgStatus.getCheckedRadioButtonId() == R.id.rb_active;

            if (TextUtils.isEmpty(name)) {
                edtName.setError("Nama wajib diisi");
                return;
            }

            if (TextUtils.isEmpty(username)) {
                edtUsername.setError("Username wajib diisi");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                edtEmail.setError("Email wajib diisi");
                return;
            }

            boolean updated = dbHelper.updateUserByAdmin(
                    user.getId(),
                    name,
                    username,
                    email,
                    phone,
                    active
            );

            if (updated) {
                Toast.makeText(this, "User berhasil diupdate", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadUsers();
            } else {
                Toast.makeText(this, "Gagal update. Email mungkin sudah dipakai.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setOnShowListener(d -> {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(android.R.color.transparent);
                window.setLayout(
                        (int) (getResources().getDisplayMetrics().widthPixels * 0.92f),
                        android.view.WindowManager.LayoutParams.WRAP_CONTENT
                );
            }
        });

        dialog.show();
    }

    private String value(String value) {
        return value == null ? "" : value;
    }
}
