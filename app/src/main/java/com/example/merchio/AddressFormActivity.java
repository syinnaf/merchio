package com.example.merchio;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.merchio.db.DbHelper;

public class AddressFormActivity extends AppCompatActivity {

    private TextView btnBack;
    private EditText etRecipient, etPhone, etAddress, etCity, etPostalCode;
    private CheckBox cbDefault;
    private Button btnSave;

    private DbHelper dbHelper;
    private SessionManager sessionManager;
    private int userId = -1;

    private int addressId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_form);

        dbHelper = new DbHelper(this);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        btnBack = findViewById(R.id.btn_back);
        etRecipient = findViewById(R.id.et_recipient);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etCity = findViewById(R.id.et_city);
        etPostalCode = findViewById(R.id.et_postal_code);
        cbDefault = findViewById(R.id.cb_default);
        btnSave = findViewById(R.id.btn_save);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveAddress());

        addressId = getIntent().getIntExtra(DeliveryAddressActivity.EXTRA_ADDRESS_ID, -1);
        isEditMode = addressId != -1;

        if (isEditMode) {
            loadAddressData();
            btnSave.setText("Update Address");
        }
    }

    private void loadAddressData() {
        Cursor cursor = dbHelper.getAddressById(addressId);
        if (cursor.moveToFirst()) {
            etRecipient.setText(cursor.getString(cursor.getColumnIndexOrThrow("recipient_name")));
            etPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            etAddress.setText(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            etCity.setText(cursor.getString(cursor.getColumnIndexOrThrow("city")));
            etPostalCode.setText(cursor.getString(cursor.getColumnIndexOrThrow("postal_code")));
            cbDefault.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow("is_default")) == 1);
        }
        cursor.close();
    }

    private void saveAddress() {
        String recipient = etRecipient.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String postalCode = etPostalCode.getText().toString().trim();
        boolean isDefault = cbDefault.isChecked();

        if (TextUtils.isEmpty(recipient) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)
                || TextUtils.isEmpty(city) || TextUtils.isEmpty(postalCode)) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success;
        if (isEditMode) {
            success = dbHelper.updateAddress(
                    addressId,
                    recipient,
                    phone,
                    address,
                    city,
                    postalCode,
                    isDefault,
                    userId
            );
        } else {
            success = dbHelper.insertAddress(
                    userId,
                    recipient,
                    phone,
                    address,
                    city,
                    postalCode,
                    isDefault
            );
        }

        Toast.makeText(this, success ? "Alamat tersimpan" : "Gagal menyimpan alamat", Toast.LENGTH_SHORT).show();

        if (success) finish();
    }
}