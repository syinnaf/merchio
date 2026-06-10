package com.example.merchio;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merchio.adapters.AddressAdapter;
import com.example.merchio.db.DbHelper;
import com.example.merchio.models.Address;

import java.util.ArrayList;
import java.util.List;

public class DeliveryAddressActivity extends AppCompatActivity implements AddressAdapter.Listener {

    public static final String EXTRA_ADDRESS_ID = "extra_address_id";
    public static final String EXTRA_MODE = "mode";
    public static final String MODE_PROFILE = "profile";
    public static final String MODE_CHECKOUT = "checkout";

    private TextView btnBack;
    private Button btnAddAddress;
    private RecyclerView rvAddresses;

    private final List<Address> addressList = new ArrayList<>();
    private AddressAdapter adapter;
    private boolean isCheckoutMode = false;

    private DbHelper dbHelper;
    private SessionManager sessionManager;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address);

        dbHelper = new DbHelper(this);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        btnBack = findViewById(R.id.btn_back);
        btnAddAddress = findViewById(R.id.btn_add_address);
        rvAddresses = findViewById(R.id.rv_addresses);
        String mode = getIntent().getStringExtra(EXTRA_MODE);
        isCheckoutMode = MODE_CHECKOUT.equals(mode);

        adapter = new AddressAdapter(addressList, this, isCheckoutMode);
        rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        rvAddresses.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnAddAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressFormActivity.class);
            startActivity(intent);
        });

        loadAddresses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
    }

    private void loadAddresses() {
        addressList.clear();

        Cursor cursor = dbHelper.getAddressesByUser(userId);
        while (cursor.moveToNext()) {
            Address address = new Address();
            address.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            address.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
            address.setRecipientName(cursor.getString(cursor.getColumnIndexOrThrow("recipient_name")));
            address.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            address.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            address.setCity(cursor.getString(cursor.getColumnIndexOrThrow("city")));
            address.setPostalCode(cursor.getString(cursor.getColumnIndexOrThrow("postal_code")));
            address.setIsDefault(cursor.getInt(cursor.getColumnIndexOrThrow("is_default")));
            addressList.add(address);
        }
        cursor.close();

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEdit(Address address) {
        Intent intent = new Intent(this, AddressFormActivity.class);
        intent.putExtra(EXTRA_ADDRESS_ID, address.getId());
        startActivity(intent);
    }

    @Override
    public void onSelect(Address address) {

        Intent result = new Intent();

        result.putExtra(
                "selected_address_id",
                address.getId()
        );

        setResult(RESULT_OK, result);

        finish();
    }

    @Override
    public void onSetDefault(Address address) {

        boolean success =
                dbHelper.setDefaultAddress(
                        userId,
                        address.getId()
                );

        if(success){

            loadAddresses();

        }
    }
}