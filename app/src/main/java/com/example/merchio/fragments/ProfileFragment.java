package com.example.merchio.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.merchio.CustomerServiceActivity;
import com.example.merchio.PaymentMethodActivity;
import com.example.merchio.PurchaseHistoryActivity;
import com.example.merchio.R;
import com.example.merchio.SessionManager;
import com.example.merchio.SettingsActivity;
import com.example.merchio.db.DbHelper;

public class ProfileFragment extends Fragment {

    private ImageView imgHeader, imgAvatar;
    private TextView tvName, tvUsername;
    private TextView tvPackingCount, tvShippingCount, tvDeliveredCount;
    private TextView menuPurchaseHistory, menuPaymentMethod, menuSetting, menuCustomerService;

    private DbHelper dbHelper;
    private SessionManager sessionManager;
    private int userId = -1;

    private String currentAvatar = "";
    private String currentHeader = "";

    private ActivityResultLauncher<String[]> avatarPickerLauncher;
    private ActivityResultLauncher<String[]> headerPickerLauncher;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerImagePickers();
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
        setupImageClicks();

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

    private void registerImagePickers() {
        avatarPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri == null || dbHelper == null || userId == -1) {
                        return;
                    }

                    persistImagePermission(uri);
                    currentAvatar = uri.toString();

                    boolean success = dbHelper.updateUserAvatar(userId, currentAvatar);

                    if (success) {
                        showImage(currentAvatar, imgAvatar);
                        Toast.makeText(requireContext(), "Foto profil berhasil diupload", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Foto profil gagal diupload", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        headerPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri == null || dbHelper == null || userId == -1) {
                        return;
                    }

                    persistImagePermission(uri);
                    currentHeader = uri.toString();

                    boolean success = dbHelper.updateUserHeader(userId, currentHeader);

                    if (success) {
                        showImage(currentHeader, imgHeader);
                        Toast.makeText(requireContext(), "Banner profil berhasil diupload", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Banner profil gagal diupload", Toast.LENGTH_SHORT).show();
                    }
                }
        );
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
        dbHelper.ensureUserImageColumnsExist();
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();
    }

    private void loadUserProfile() {
        if (userId == -1) {
            tvName.setText("Guest");
            tvUsername.setText("@guest");
            imgAvatar.setImageResource(R.drawable.logo_merchio);
            imgHeader.setImageResource(R.drawable.logo_merchio);
            return;
        }

        Cursor cursor = dbHelper.getUserById(userId);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            currentAvatar = cursor.getString(cursor.getColumnIndexOrThrow("avatar"));
            currentHeader = cursor.getString(cursor.getColumnIndexOrThrow("header"));

            tvName.setText(!TextUtils.isEmpty(name) ? name : "Merchio User");

            if (!TextUtils.isEmpty(username)) {
                tvUsername.setText("@" + username);
            } else {
                tvUsername.setText(email);
            }

            showImage(currentAvatar, imgAvatar);
            showImage(currentHeader, imgHeader);
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

    private void setupImageClicks() {
        imgAvatar.setOnClickListener(v -> {
            if (userId == -1) {
                Toast.makeText(requireContext(), "Login dulu untuk upload foto profil", Toast.LENGTH_SHORT).show();
                return;
            }
            avatarPickerLauncher.launch(new String[]{"image/*"});
        });

        imgHeader.setOnClickListener(v -> {
            if (userId == -1) {
                Toast.makeText(requireContext(), "Login dulu untuk upload banner", Toast.LENGTH_SHORT).show();
                return;
            }
            headerPickerLauncher.launch(new String[]{"image/*"});
        });
    }

    private void persistImagePermission(Uri uri) {
        try {
            requireContext().getContentResolver().takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );
        } catch (SecurityException ignored) {
        }
    }

    private void showImage(String imageUri, ImageView target) {
        if (target == null) {
            return;
        }

        if (TextUtils.isEmpty(imageUri)) {
            target.setImageResource(R.drawable.logo_merchio);
            return;
        }

        Glide.with(requireContext())
                .load(imageUri)
                .placeholder(R.drawable.logo_merchio)
                .error(R.drawable.logo_merchio)
                .into(target);
    }
}
