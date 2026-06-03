package com.example.merchio.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.merchio.R;
import com.example.merchio.SessionManager;
import com.example.merchio.db.DbHelper;
import com.example.merchio.SettingsActivity;
import com.example.merchio.PurchaseHistoryActivity;
import com.example.merchio.PaymentMethodActivity;
import com.example.merchio.CustomerServiceActivity;

public class ProfileFragment extends Fragment {

    private ImageView imgHeader, imgAvatar;
    private TextView tvName, tvUsername;
    private TextView tvPackingCount, tvShippingCount, tvDeliveredCount;
    private TextView menuPurchaseHistory, menuPaymentMethod, menuSetting, menuCustomerService;

    private DbHelper dbHelper;
    private SessionManager sessionManager;
    private int userId = -1;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        initHelpers();
        loadUserProfile();
        loadOrderSummary();
        setupMenuClicks();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (dbHelper != null && sessionManager != null) {
            userId = sessionManager.getUserId();
            loadUserProfile();
            loadOrderSummary();
        }
    }

    private void initViews(View view) {
        imgHeader = view.findViewById(R.id.img_header);
        imgAvatar = view.findViewById(R.id.img_avatar);

        tvName = view.findViewById(R.id.tv_name);
        tvUsername = view.findViewById(R.id.tv_username);

        tvPackingCount = view.findViewById(R.id.tv_packing_count);
        tvShippingCount = view.findViewById(R.id.tv_shipping_count);
        tvDeliveredCount = view.findViewById(R.id.tv_delivered_count);

        menuPurchaseHistory = view.findViewById(R.id.menu_purchase_history);
        menuPaymentMethod = view.findViewById(R.id.menu_payment_method);
        menuSetting = view.findViewById(R.id.menu_setting);
        menuCustomerService = view.findViewById(R.id.menu_customer_service);
    }

    private void initHelpers() {
        dbHelper = new DbHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();
    }

    private void loadUserProfile() {
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
            String header = cursor.getString(cursor.getColumnIndexOrThrow("header"));

            tvName.setText(!TextUtils.isEmpty(name) ? name : "Merchio User");

            if (!TextUtils.isEmpty(username)) {
                tvUsername.setText("@" + username);
            } else {
                tvUsername.setText(email);
            }

            if (!TextUtils.isEmpty(avatar)) {
                Glide.with(requireContext())
                        .load(avatar)
                        .placeholder(R.drawable.logo_merchio)
                        .into(imgAvatar);
            }

            if (!TextUtils.isEmpty(header)) {
                Glide.with(requireContext())
                        .load(header)
                        .placeholder(R.drawable.logo_merchio)
                        .into(imgHeader);
            }
        }

        cursor.close();
    }

    private void loadOrderSummary() {
        if (userId == -1) {
            setOrderSummary(0, 0, 0);
            return;
        }

        dbHelper.normalizeLegacyOrderStatuses(userId);

        int packing = dbHelper.getOrderCountByStatus(userId, DbHelper.STATUS_PACKING);
        int shipping = dbHelper.getOrderCountByStatus(userId, DbHelper.STATUS_SHIPPING);
        int delivered = dbHelper.getOrderCountByStatus(userId, DbHelper.STATUS_DELIVERED);

        setOrderSummary(packing, shipping, delivered);
    }

//    private void loadOrderSummary() {
//        if (userId == -1) {
//            setOrderSummary(0, 0, 0);
//            return;
//        }
//
//        int packing = dbHelper.getOrderCountByStatus(userId, "packing");
//        int shipping = dbHelper.getOrderCountByStatus(userId, "shipping")
//                + dbHelper.getOrderCountByStatus(userId, "shipped")
//                + dbHelper.getOrderCountByStatus(userId, "in_transit");
//        int delivered = dbHelper.getOrderCountByStatus(userId, "delivered");
//
//        setOrderSummary(packing, shipping, delivered);
//    }

    private void setOrderSummary(int packing, int shipping, int delivered) {
        tvPackingCount.setText(packing + "\nPacking");
        tvShippingCount.setText(shipping + "\nShipping");
        tvDeliveredCount.setText(delivered + "\nDelivered");
    }

    private void setupMenuClicks() {
        menuPurchaseHistory.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PurchaseHistoryActivity.class);
            startActivity(intent);
        });

        menuPaymentMethod.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PaymentMethodActivity.class);
            startActivity(intent);
        });

        menuSetting.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SettingsActivity.class);
            startActivity(intent);
        });

        menuCustomerService.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CustomerServiceActivity.class);
            startActivity(intent);
        });
    }
}