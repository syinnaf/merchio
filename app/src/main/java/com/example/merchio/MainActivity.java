package com.example.merchio;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.merchio.db.DbHelper;
import com.example.merchio.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {

    DbHelper dbHelper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testDatabaseConnection();

        loadHomeFragment();
    }

    private void loadHomeFragment() {

        HomeFragment homeFragment = new HomeFragment();

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.frameLayout, homeFragment);

        transaction.commit();
    }

    private void testDatabaseConnection() {
        try {
            dbHelper = new DbHelper(this);
            database = dbHelper.getWritableDatabase();

            if (database != null && database.isOpen()) {
                Toast.makeText(this, "Database connected", Toast.LENGTH_SHORT).show();
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