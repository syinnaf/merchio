package com.example.merchio.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "merchio.db";
    public static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsers = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "username TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "phone TEXT, " +
                "avatar TEXT, " +
                "header TEXT, " +
                "created_at TEXT" +
                ")";

        String createCart = "CREATE TABLE cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "product_id TEXT, " +
                "product_name TEXT, " +
                "product_image TEXT, " +
                "product_price INTEGER, " +
                "type TEXT, " +
                "quantity INTEGER, " +
                "stock INTEGER, " +
                "is_checked INTEGER DEFAULT 0, " +
                "created_at TEXT" +
                ")";

        String createWishlist = "CREATE TABLE wishlist (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "product_id TEXT, " +
                "product_name TEXT, " +
                "product_image TEXT, " +
                "product_price INTEGER, " +
                "type TEXT, " +
                "created_at TEXT" +
                ")";

        String createOrders = "CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "order_code TEXT, " +
                "total_price INTEGER, " +
                "shipping_price INTEGER, " +
                "tax INTEGER, " +
                "payment_method TEXT, " +
                "shipping_method TEXT, " +
                "address TEXT, " +
                "status TEXT, " +
                "order_date TEXT, " +
                "estimated_arrival TEXT" +
                ")";

        String createOrderItems = "CREATE TABLE order_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER, " +
                "product_id TEXT, " +
                "product_name TEXT, " +
                "product_image TEXT, " +
                "price INTEGER, " +
                "quantity INTEGER, " +
                "type TEXT" +
                ")";

        String createAddresses = "CREATE TABLE addresses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "recipient_name TEXT, " +
                "phone TEXT, " +
                "address TEXT, " +
                "city TEXT, " +
                "postal_code TEXT, " +
                "is_default INTEGER DEFAULT 0" +
                ")";

        String createRecentSearches = "CREATE TABLE recent_searches (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "keyword TEXT, " +
                "created_at TEXT" +
                ")";

        String createSettings = "CREATE TABLE settings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "dark_mode INTEGER DEFAULT 0, " +
                "notification_enabled INTEGER DEFAULT 1, " +
                "selected_theme TEXT" +
                ")";

        db.execSQL(createUsers);
        db.execSQL(createCart);
        db.execSQL(createWishlist);
        db.execSQL(createOrders);
        db.execSQL(createOrderItems);
        db.execSQL(createAddresses);
        db.execSQL(createRecentSearches);
        db.execSQL(createSettings);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS wishlist");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS order_items");
        db.execSQL("DROP TABLE IF EXISTS addresses");
        db.execSQL("DROP TABLE IF EXISTS recent_searches");
        db.execSQL("DROP TABLE IF EXISTS settings");
        onCreate(db);
    }

    // =========================================================
    // USERS: REGISTER & LOGIN
    // =========================================================

    public boolean insertUser(String name, String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        values.put("phone", "");
        values.put("avatar", "");
        values.put("header", "");
        values.put("created_at", now());

        long result = db.insert("users", null, values);

        if (result != -1) {
            createDefaultSettings((int) result);
        }

        return result != -1;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM users WHERE email = ?",
                new String[]{email}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();

        return exists;
    }

    public int loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM users WHERE email = ? AND password = ?",
                new String[]{email, password}
        );

        int userId = -1;

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        }

        cursor.close();
        return userId;
    }

    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM users WHERE id = ?",
                new String[]{String.valueOf(userId)}
        );
    }

    public boolean updateUserProfile(int userId, String name, String username, String phone, String avatar, String header) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("username", username);
        values.put("phone", phone);
        values.put("avatar", avatar);
        values.put("header", header);

        int result = db.update(
                "users",
                values,
                "id = ?",
                new String[]{String.valueOf(userId)}
        );

        return result > 0;
    }

    // =========================================================
    // CART CRUD
    // =========================================================

    public boolean addToCart(
            int userId,
            String productId,
            String productName,
            String productImage,
            int productPrice,
            String type,
            int quantity,
            int stock
    ) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, quantity FROM cart WHERE user_id = ? AND product_id = ? AND type = ?",
                new String[]{String.valueOf(userId), productId, type}
        );

        if (cursor.moveToFirst()) {
            int cartId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int oldQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            cursor.close();

            int newQuantity = oldQuantity + quantity;

            if (newQuantity > stock) {
                newQuantity = stock;
            }

            return updateCartQuantity(cartId, newQuantity);
        }

        cursor.close();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("product_id", productId);
        values.put("product_name", productName);
        values.put("product_image", productImage);
        values.put("product_price", productPrice);
        values.put("type", type);
        values.put("quantity", quantity);
        values.put("stock", stock);
        values.put("is_checked", 0);
        values.put("created_at", now());

        long result = db.insert("cart", null, values);
        return result != -1;
    }

    public Cursor getCartByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM cart WHERE user_id = ? ORDER BY id DESC",
                new String[]{String.valueOf(userId)}
        );
    }

    public Cursor getCheckedCartByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM cart WHERE user_id = ? AND is_checked = 1 ORDER BY id DESC",
                new String[]{String.valueOf(userId)}
        );
    }

    public boolean updateCartQuantity(int cartId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (quantity <= 0) {
            return deleteCartItem(cartId);
        }

        ContentValues values = new ContentValues();
        values.put("quantity", quantity);

        int result = db.update(
                "cart",
                values,
                "id = ?",
                new String[]{String.valueOf(cartId)}
        );

        return result > 0;
    }

    public boolean updateCartChecked(int cartId, boolean isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("is_checked", isChecked ? 1 : 0);

        int result = db.update(
                "cart",
                values,
                "id = ?",
                new String[]{String.valueOf(cartId)}
        );

        return result > 0;
    }

    public boolean updateAllCartChecked(int userId, boolean isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("is_checked", isChecked ? 1 : 0);

        int result = db.update(
                "cart",
                values,
                "user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        return result > 0;
    }

    public int getCheckedCartTotal(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(product_price * quantity) AS total FROM cart WHERE user_id = ? AND is_checked = 1",
                new String[]{String.valueOf(userId)}
        );

        int total = 0;

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }

        cursor.close();
        return total;
    }

    public int getCheckedCartCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) AS total_item FROM cart WHERE user_id = ? AND is_checked = 1",
                new String[]{String.valueOf(userId)}
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndexOrThrow("total_item"));
        }

        cursor.close();
        return count;
    }

    public boolean deleteCartItem(int cartId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "cart",
                "id = ?",
                new String[]{String.valueOf(cartId)}
        );

        return result > 0;
    }

    public boolean clearCheckedCart(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "cart",
                "user_id = ? AND is_checked = 1",
                new String[]{String.valueOf(userId)}
        );

        return result > 0;
    }

    // =========================================================
    // WISHLIST CRUD
    // =========================================================

    public boolean addToWishlist(
            int userId,
            String productId,
            String productName,
            String productImage,
            int productPrice,
            String type
    ) {
        if (isProductInWishlist(userId, productId, type)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("product_id", productId);
        values.put("product_name", productName);
        values.put("product_image", productImage);
        values.put("product_price", productPrice);
        values.put("type", type);
        values.put("created_at", now());

        long result = db.insert("wishlist", null, values);
        return result != -1;
    }

    public boolean isProductInWishlist(int userId, String productId, String type) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM wishlist WHERE user_id = ? AND product_id = ? AND type = ?",
                new String[]{String.valueOf(userId), productId, type}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();

        return exists;
    }

    public Cursor getWishlistByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM wishlist WHERE user_id = ? ORDER BY id DESC",
                new String[]{String.valueOf(userId)}
        );
    }

    public boolean removeWishlist(int userId, String productId, String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "wishlist",
                "user_id = ? AND product_id = ? AND type = ?",
                new String[]{String.valueOf(userId), productId, type}
        );

        return result > 0;
    }

    public boolean deleteWishlistById(int wishlistId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "wishlist",
                "id = ?",
                new String[]{String.valueOf(wishlistId)}
        );

        return result > 0;
    }

    // =========================================================
    // ADDRESSES CRUD
    // =========================================================

    public boolean insertAddress(
            int userId,
            String recipientName,
            String phone,
            String address,
            String city,
            String postalCode,
            boolean isDefault
    ) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (isDefault) {
            resetDefaultAddress(userId);
        }

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("recipient_name", recipientName);
        values.put("phone", phone);
        values.put("address", address);
        values.put("city", city);
        values.put("postal_code", postalCode);
        values.put("is_default", isDefault ? 1 : 0);

        long result = db.insert("addresses", null, values);
        return result != -1;
    }

    public Cursor getAddressesByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM addresses WHERE user_id = ? ORDER BY is_default DESC, id DESC",
                new String[]{String.valueOf(userId)}
        );
    }

    public Cursor getDefaultAddress(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM addresses WHERE user_id = ? AND is_default = 1 LIMIT 1",
                new String[]{String.valueOf(userId)}
        );
    }

    public boolean updateAddress(
            int addressId,
            String recipientName,
            String phone,
            String address,
            String city,
            String postalCode,
            boolean isDefault,
            int userId
    ) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (isDefault) {
            resetDefaultAddress(userId);
        }

        ContentValues values = new ContentValues();
        values.put("recipient_name", recipientName);
        values.put("phone", phone);
        values.put("address", address);
        values.put("city", city);
        values.put("postal_code", postalCode);
        values.put("is_default", isDefault ? 1 : 0);

        int result = db.update(
                "addresses",
                values,
                "id = ?",
                new String[]{String.valueOf(addressId)}
        );

        return result > 0;
    }

    public boolean setDefaultAddress(int userId, int addressId) {
        SQLiteDatabase db = this.getWritableDatabase();

        resetDefaultAddress(userId);

        ContentValues values = new ContentValues();
        values.put("is_default", 1);

        int result = db.update(
                "addresses",
                values,
                "id = ? AND user_id = ?",
                new String[]{String.valueOf(addressId), String.valueOf(userId)}
        );

        return result > 0;
    }

    private void resetDefaultAddress(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("is_default", 0);

        db.update(
                "addresses",
                values,
                "user_id = ?",
                new String[]{String.valueOf(userId)}
        );
    }

    public boolean deleteAddress(int addressId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "addresses",
                "id = ?",
                new String[]{String.valueOf(addressId)}
        );

        return result > 0;
    }

    // =========================================================
    // CHECKOUT, ORDERS, ORDER ITEMS
    // =========================================================

    public long checkoutFromCart(
            int userId,
            String paymentMethod,
            String shippingMethod,
            String address,
            int shippingPrice,
            int tax,
            String estimatedArrival
    ) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cartCursor = getCheckedCartByUser(userId);

        if (cartCursor.getCount() == 0) {
            cartCursor.close();
            return -1;
        }

        int cartTotal = getCheckedCartTotal(userId);
        int grandTotal = cartTotal + shippingPrice + tax;
        String orderCode = "MRC" + System.currentTimeMillis();

        db.beginTransaction();

        long orderId = -1;

        try {
            ContentValues orderValues = new ContentValues();
            orderValues.put("user_id", userId);
            orderValues.put("order_code", orderCode);
            orderValues.put("total_price", grandTotal);
            orderValues.put("shipping_price", shippingPrice);
            orderValues.put("tax", tax);
            orderValues.put("payment_method", paymentMethod);
            orderValues.put("shipping_method", shippingMethod);
            orderValues.put("address", address);
            orderValues.put("status", "confirmed");
            orderValues.put("order_date", now());
            orderValues.put("estimated_arrival", estimatedArrival);

            orderId = db.insert("orders", null, orderValues);

            if (orderId == -1) {
                db.endTransaction();
                cartCursor.close();
                return -1;
            }

            while (cartCursor.moveToNext()) {
                ContentValues itemValues = new ContentValues();
                itemValues.put("order_id", orderId);
                itemValues.put("product_id", cartCursor.getString(cartCursor.getColumnIndexOrThrow("product_id")));
                itemValues.put("product_name", cartCursor.getString(cartCursor.getColumnIndexOrThrow("product_name")));
                itemValues.put("product_image", cartCursor.getString(cartCursor.getColumnIndexOrThrow("product_image")));
                itemValues.put("price", cartCursor.getInt(cartCursor.getColumnIndexOrThrow("product_price")));
                itemValues.put("quantity", cartCursor.getInt(cartCursor.getColumnIndexOrThrow("quantity")));
                itemValues.put("type", cartCursor.getString(cartCursor.getColumnIndexOrThrow("type")));

                db.insert("order_items", null, itemValues);
            }

            db.delete(
                    "cart",
                    "user_id = ? AND is_checked = 1",
                    new String[]{String.valueOf(userId)}
            );

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
            cartCursor.close();
        }

        return orderId;
    }

    public Cursor getOrdersByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM orders WHERE user_id = ? ORDER BY id DESC",
                new String[]{String.valueOf(userId)}
        );
    }

    public Cursor getOrderById(long orderId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM orders WHERE id = ?",
                new String[]{String.valueOf(orderId)}
        );
    }

    public Cursor getOrderItems(long orderId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM order_items WHERE order_id = ?",
                new String[]{String.valueOf(orderId)}
        );
    }

    public boolean updateOrderStatus(long orderId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", status);

        int result = db.update(
                "orders",
                values,
                "id = ?",
                new String[]{String.valueOf(orderId)}
        );

        return result > 0;
    }

    // =========================================================
    // RECENT SEARCHES
    // =========================================================

    public boolean addRecentSearch(int userId, String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                "recent_searches",
                "user_id = ? AND keyword = ?",
                new String[]{String.valueOf(userId), keyword}
        );

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("keyword", keyword);
        values.put("created_at", now());

        long result = db.insert("recent_searches", null, values);
        return result != -1;
    }

    public Cursor getRecentSearches(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM recent_searches WHERE user_id = ? ORDER BY id DESC LIMIT 10",
                new String[]{String.valueOf(userId)}
        );
    }

    public boolean deleteRecentSearch(int searchId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "recent_searches",
                "id = ?",
                new String[]{String.valueOf(searchId)}
        );

        return result > 0;
    }

    public boolean clearRecentSearches(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "recent_searches",
                "user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        return result > 0;
    }

    // =========================================================
    // SETTINGS
    // =========================================================

    public boolean createDefaultSettings(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("dark_mode", 0);
        values.put("notification_enabled", 1);
        values.put("selected_theme", "light");

        long result = db.insert("settings", null, values);
        return result != -1;
    }

    public Cursor getSettings(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM settings WHERE user_id = ? LIMIT 1",
                new String[]{String.valueOf(userId)}
        );
    }

    public boolean updateDarkMode(int userId, boolean darkMode) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("dark_mode", darkMode ? 1 : 0);
        values.put("selected_theme", darkMode ? "dark" : "light");

        int result = db.update(
                "settings",
                values,
                "user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        return result > 0;
    }

    public boolean updateNotification(int userId, boolean enabled) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("notification_enabled", enabled ? 1 : 0);

        int result = db.update(
                "settings",
                values,
                "user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        return result > 0;
    }
}