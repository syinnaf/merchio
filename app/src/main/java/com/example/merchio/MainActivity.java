package com.example.merchio;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.merchio.db.DbHelper;
import com.example.merchio.fragments.CartFragment;
import com.example.merchio.fragments.HomeFragment;
import com.example.merchio.fragments.ProductSearchFragment;
import com.example.merchio.fragments.ProfileFragment;
import com.example.merchio.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    DbHelper dbHelper;
    SQLiteDatabase database;
    BottomNavigationView bottomNav;

    // Flag untuk mencegah bottom nav listener menimpa fragment yang sudah
    // di-set dari luar (misal: navigateToSearchWithFilters dari HomeFragment)
    private boolean suppressNavListener = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testDatabaseConnection();

        bottomNav = findViewById(R.id.bottomNav);
        setupBottomNav();

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private void setupBottomNav() {
        bottomNav.setOnItemSelectedListener(item -> {
            // Jika flag aktif, fragment sudah di-set dari luar — skip replace
            if (suppressNavListener) {
                suppressNavListener = false;
                return true;
            }

            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                openFragment(new HomeFragment());
                return true;

            } else if (itemId == R.id.nav_search) {
                openFragment(new ProductSearchFragment());
                return true;

            } else if (itemId == R.id.nav_cart) {
                openFragment(new CartFragment());
                return true;

            } else if (itemId == R.id.nav_profile) {
                openFragment(new ProfileFragment());
                return true;
            }

            return false;
        });
    }

    /**
     * Navigasi ke fragment tertentu dan update tab bottom nav
     * TANPA memicu listener (sehingga fragment berfilter tidak ditimpa).
     * Dipanggil dari HomeFragment saat navigate dengan filter.
     */
    public void navigateWithFragment(Fragment fragment, int navItemId) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();

        suppressNavListener = true;
        bottomNav.setSelectedItemId(navItemId);
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }

    private void testDatabaseConnection() {
        try {
            dbHelper = new DbHelper(this);
            database = dbHelper.getWritableDatabase();

            if (database != null && database.isOpen()) {
                Log.d("DB_TEST", "Database connected successfully");
                Log.d("DB_TEST", "Database path: " + database.getPath());

                checkTables();
            } else {
                Toast.makeText(this, "Database failed", Toast.LENGTH_SHORT).show();
                Log.e("DB_TEST", "Database failed to open");
            }

        } catch (Exception e) {
            Toast.makeText(this, "DB Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("DB_TEST", "Database error: ", e);
        }
    }

    private void checkTables() {
        Cursor cursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table'",
                null
        );

        Log.d("DB_TEST", "===== TABLE LIST =====");

        while (cursor.moveToNext()) {
            String tableName = cursor.getString(0);
            Log.d("DB_TEST", "Table: " + tableName);
        }

        cursor.close();
    }
}