package com.example.merchio.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteConstraintException;
import android.text.TextUtils;
import com.example.merchio.models.CartItem;
import com.example.merchio.models.OrderItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "merchio.db";
    public static final int DATABASE_VERSION = 5;

    public static final String STATUS_PACKING = "packing";
    public static final String STATUS_SHIPPING = "shipping";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_CANCELLED = "cancelled";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    private String normalizeOrderStatus(String status) {
        if (TextUtils.isEmpty(status)) {
            return STATUS_PACKING;
        }

        status = status.toLowerCase().trim();

        if (status.equals("confirmed")) {
            return STATUS_PACKING;
        }

        if (status.equals("shipped") || status.equals("in_transit")) {
            return STATUS_SHIPPING;
        }

        if (status.equals(STATUS_DELIVERED)) {
            return STATUS_DELIVERED;
        }

        if (status.equals(STATUS_SHIPPING)) {
            return STATUS_SHIPPING;
        }

        if (status.equals(STATUS_CANCELLED) || status.equals("canceled") || status.equals("cancel")) {
            return STATUS_CANCELLED;
        }

        return STATUS_PACKING;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUsersTable(db);
        createCartTable(db);
        createWishlistTable(db);
        createAddressesTable(db);
        createOrdersTable(db);
        createOrderItemsTable(db);
        createRecentSearchesTable(db);
        createSettingsTable(db);
    }

    private void createUsersTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT, " +
                        "username TEXT, " +
                        "email TEXT UNIQUE, " +
                        "password TEXT, " +
                        "phone TEXT, " +
                        "avatar TEXT, " +
                        "header TEXT, " +
                        "created_at TEXT, " +
                        "role TEXT DEFAULT 'customer', " +
                        "is_active INTEGER DEFAULT 1" +
                        ")"
        );
    }

    private void createCartTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE cart (" +
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
                        ")"
        );
    }

    private void createWishlistTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE wishlist (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_id INTEGER, " +
                        "product_id TEXT, " +
                        "product_name TEXT, " +
                        "product_image TEXT, " +
                        "product_price INTEGER, " +
                        "type TEXT, " +
                        "created_at TEXT" +
                        ")"
        );
    }

    private void createAddressesTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE addresses (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_id INTEGER, " +
                        "recipient_name TEXT, " +
                        "phone TEXT, " +
                        "address TEXT, " +
                        "city TEXT, " +
                        "postal_code TEXT, " +
                        "is_default INTEGER DEFAULT 0" +
                        ")"
        );
    }

    private void createOrdersTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE orders (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_id INTEGER, " +
                        "order_code TEXT, " +
                        "total_price INTEGER, " +
                        "shipping_price INTEGER, " +
                        "tax INTEGER, " +
                        "payment_method TEXT, " +
                        "shipping_method TEXT, " +
                        "address_id INTEGER, " +
                        "address TEXT, " +
                        "status TEXT, " +
                        "is_received INTEGER DEFAULT 0, " +
                        "order_date TEXT, " +
                        "estimated_arrival TEXT" +
                        ")"
        );
    }

    private void createOrderItemsTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE order_items (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "order_id INTEGER, " +
                        "product_id TEXT, " +
                        "product_name TEXT, " +
                        "product_image TEXT, " +
                        "price INTEGER, " +
                        "quantity INTEGER, " +
                        "type TEXT" +
                        ")"
        );
    }

    private void createRecentSearchesTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE recent_searches (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_id INTEGER, " +
                        "keyword TEXT, " +
                        "created_at TEXT" +
                        ")"
        );
    }

    private void createSettingsTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE settings (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_id INTEGER, " +
                        "dark_mode INTEGER DEFAULT 0, " +
                        "notification_enabled INTEGER DEFAULT 1, " +
                        "selected_theme TEXT" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 4) {
            try {
                db.execSQL(
                        "ALTER TABLE orders ADD COLUMN is_received INTEGER DEFAULT 0"
                );
            } catch (Exception ignored) {
            }
        }

        if (oldVersion < 5) {
            try {
                db.execSQL(
                        "ALTER TABLE users ADD COLUMN is_active INTEGER DEFAULT 1"
                );
            } catch (Exception ignored) {
            }
        }
    }

    private void dropAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS order_items");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS wishlist");
        db.execSQL("DROP TABLE IF EXISTS addresses");
        db.execSQL("DROP TABLE IF EXISTS recent_searches");
        db.execSQL("DROP TABLE IF EXISTS settings");
        db.execSQL("DROP TABLE IF EXISTS users");
    }

    // =========================================================
    // USERS
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
        values.put("role", "customer");
        values.put("is_active", 1);

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

    public boolean checkUser(String email, String password) {
        return loginUser(email, password) != -1;
    }

    public int loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        ensureUserActiveColumnExists();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM users WHERE email = ? AND password = ? AND is_active = 1",
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

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM users WHERE email = ?",
                new String[]{email}
        );
    }

    public String getUserRole(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT role FROM users WHERE id = ?",
                new String[]{String.valueOf(userId)}
        );

        String role = "customer";

        if (cursor.moveToFirst()) {
            role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
        }

        cursor.close();
        return role;
    }

    public void ensureRoleColumnExists() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("PRAGMA table_info(users)", null);

        boolean roleExists = false;

        while (cursor.moveToNext()) {
            String columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"));

            if ("role".equals(columnName)) {
                roleExists = true;
                break;
            }
        }

        cursor.close();

        if (!roleExists) {
            db.execSQL("ALTER TABLE users ADD COLUMN role TEXT DEFAULT 'customer'");
        }
    }

    public void ensureUserActiveColumnExists() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("PRAGMA table_info(users)", null);

        boolean activeExists = false;

        while (cursor.moveToNext()) {
            String columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"));

            if ("is_active".equals(columnName)) {
                activeExists = true;
                break;
            }
        }

        cursor.close();

        if (!activeExists) {
            db.execSQL("ALTER TABLE users ADD COLUMN is_active INTEGER DEFAULT 1");
        }
    }

    public void createDefaultAdminIfNeeded() {
        ensureRoleColumnExists();
        ensureUserActiveColumnExists();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM users WHERE email = ?",
                new String[]{"admin@merchio.id"}
        );

        boolean adminExists = cursor.moveToFirst();
        cursor.close();

        if (adminExists) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put("name", "Merchio Admin");
        values.put("username", "admin");
        values.put("email", "admin@merchio.id");
        values.put("password", "admin123");
        values.put("phone", "");
        values.put("avatar", "");
        values.put("header", "");
        values.put("created_at", now());
        values.put("role", "admin");
        values.put("is_active", 1);

        long result = db.insert("users", null, values);

        if (result != -1) {
            createDefaultSettings((int) result);
        }
    }

    public Cursor getAllUsersForAdmin() {
        ensureUserActiveColumnExists();
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM users WHERE LOWER(IFNULL(role, 'customer')) != 'admin' ORDER BY id DESC",
                null
        );
    }

    public boolean updateUserByAdmin(
            int userId,
            String name,
            String username,
            String email,
            String phone,
            boolean isActive
    ) {
        ensureUserActiveColumnExists();
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("username", username);
        values.put("email", email);
        values.put("phone", phone);
        values.put("is_active", isActive ? 1 : 0);

        try {
            int result = db.update(
                    "users",
                    values,
                    "id = ? AND LOWER(IFNULL(role, 'customer')) != 'admin'",
                    new String[]{String.valueOf(userId)}
            );

            return result > 0;
        } catch (SQLiteConstraintException e) {
            return false;
        }
    }

    public boolean updateUserProfile(
            int userId,
            String name,
            String username,
            String phone,
            String avatar,
            String header
    ) {
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
    // CART
    // =========================================================

    public boolean insertCartItem(
            int userId,
            String productId,
            String productName,
            String productImage,
            int productPrice,
            String type,
            int quantity,
            int stock
    ) {
        return addToCart(userId, productId, productName, productImage, productPrice, type, quantity, stock);
    }

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

        if (quantity < 1) {
            quantity = 1;
        }

        if (quantity > stock) {
            quantity = stock;
        }

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

    public Cursor getCartItemsByUserId(int userId) {
        return getCartByUser(userId);
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

        Cursor cursor = db.rawQuery(
                "SELECT stock FROM cart WHERE id = ?",
                new String[]{String.valueOf(cartId)}
        );

        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }

        int stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock"));
        cursor.close();

        if (quantity > stock) {
            quantity = stock;
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

    public boolean updateCartQty(int cartId, int quantity) {
        return updateCartQuantity(cartId, quantity);
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

        if (cursor.moveToFirst() && !cursor.isNull(cursor.getColumnIndexOrThrow("total"))) {
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

    public boolean deleteCheckedCartItems(int userId) {
        return clearCheckedCart(userId);
    }

    public boolean clearCartByUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "cart",
                "user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        return result > 0;
    }

    // =========================================================
    // WISHLIST
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
    // ADDRESSES
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

    public Cursor getAddressesByUserId(int userId) {
        return getAddressesByUser(userId);
    }

    public Cursor getAddressById(int addressId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM addresses WHERE id = ?",
                new String[]{String.valueOf(addressId)}
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
                "id = ? AND user_id = ?",
                new String[]{String.valueOf(addressId), String.valueOf(userId)}
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

    public boolean deleteAddressByUser(int userId, int addressId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "addresses",
                "id = ? AND user_id = ?",
                new String[]{String.valueOf(addressId), String.valueOf(userId)}
        );

        return result > 0;
    }

    // =========================================================
    // ORDERS
    // =========================================================

    public long insertOrder(
            int userId,
            String orderCode,
            int totalPrice,
            int shippingPrice,
            int tax,
            String paymentMethod,
            String shippingMethod,
            int addressId,
            String address,
            String status,
            String estimatedArrival
    ) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (TextUtils.isEmpty(orderCode)) {
            orderCode = "MRC" + System.currentTimeMillis();
        }

        status = normalizeOrderStatus(status);

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("order_code", orderCode);
        values.put("total_price", totalPrice);
        values.put("shipping_price", shippingPrice);
        values.put("tax", tax);
        values.put("payment_method", paymentMethod);
        values.put("shipping_method", shippingMethod);
        values.put("address_id", addressId);
        values.put("address", address);
        values.put("status", status);
        values.put("order_date", now());
        values.put("estimated_arrival", estimatedArrival);

        return db.insert("orders", null, values);
    }

    public boolean insertOrderItem(
            long orderId,
            String productId,
            String productName,
            String productImage,
            int price,
            int quantity,
            String type
    ) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("order_id", orderId);
        values.put("product_id", productId);
        values.put("product_name", productName);
        values.put("product_image", productImage);
        values.put("price", price);
        values.put("quantity", quantity);
        values.put("type", type);

        long result = db.insert("order_items", null, values);
        return result != -1;
    }

    public long checkoutFromCart(
            int userId,
            String paymentMethod,
            String shippingMethod,
            String address,
            int shippingPrice,
            int tax,
            String estimatedArrival
    ) {
        return checkoutFromCart(
                userId,
                paymentMethod,
                shippingMethod,
                0,
                address,
                shippingPrice,
                tax,
                estimatedArrival
        );
    }

    public long checkoutFromCart(
            int userId,
            String paymentMethod,
            String shippingMethod,
            int addressId,
            String address,
            int shippingPrice,
            int tax,
            String estimatedArrival
    ) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cartCursor = db.rawQuery(
                "SELECT * FROM cart WHERE user_id = ? AND is_checked = 1 ORDER BY id DESC",
                new String[]{String.valueOf(userId)}
        );

        if (cartCursor.getCount() == 0) {
            cartCursor.close();
            return -1;
        }

        int cartTotal = getCheckedCartTotal(userId);
        int grandTotal = cartTotal + shippingPrice + tax;
        String orderCode = "MRC" + System.currentTimeMillis();

        long orderId = -1;

        db.beginTransaction();

        try {
            ContentValues orderValues = new ContentValues();
            orderValues.put("user_id", userId);
            orderValues.put("order_code", orderCode);
            orderValues.put("total_price", grandTotal);
            orderValues.put("shipping_price", shippingPrice);
            orderValues.put("tax", tax);
            orderValues.put("payment_method", paymentMethod);
            orderValues.put("shipping_method", shippingMethod);
            orderValues.put("address_id", addressId);
            orderValues.put("address", address);
            orderValues.put("status", STATUS_PACKING); //baru gw ganti dari confimed jadi packing
            orderValues.put("order_date", now());
            orderValues.put("estimated_arrival", estimatedArrival);

            orderId = db.insert("orders", null, orderValues);

            if (orderId == -1) {
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

                long itemResult = db.insert("order_items", null, itemValues);

                if (itemResult == -1) {
                    return -1;
                }
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

    public long createBuyNowOrder(
            int userId,
            CartItem item,
            String paymentMethod,
            String shippingMethod,
            int addressId,
            String fullAddress,
            int shippingCost,
            int tax,
            String estimatedArrival
    ) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        int totalPrice =
                (item.getProductPrice() * item.getQuantity())
                        + shippingCost
                        + tax;

        ContentValues orderValues =
                new ContentValues();

        orderValues.put("user_id", userId);
        orderValues.put("order_code", "MRC" + System.currentTimeMillis());
        orderValues.put("total_price", totalPrice);
        orderValues.put("shipping_price", shippingCost);
        orderValues.put("tax", tax);
        orderValues.put("payment_method", paymentMethod);
        orderValues.put("shipping_method", shippingMethod);
        orderValues.put("address_id", addressId);
        orderValues.put("address", fullAddress);
        orderValues.put("status", STATUS_PACKING);
        orderValues.put("order_date", now());
        orderValues.put("estimated_arrival", estimatedArrival);

        long orderId =
                db.insert(
                        "orders",
                        null,
                        orderValues
                );

        if(orderId == -1){
            return -1;
        }

        ContentValues detail =
                new ContentValues();

        detail.put("order_id", orderId);
        detail.put("product_id", item.getProductId());
        detail.put("product_name", item.getProductName());
        detail.put("product_image", item.getProductImage());
        detail.put("price", item.getProductPrice());
        detail.put("quantity", item.getQuantity());
        detail.put("type", item.getType());

        db.insert(
                "order_items",
                null,
                detail
        );

        return orderId;
    }

    public Cursor getOrdersByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM orders WHERE user_id = ? ORDER BY id DESC",
                new String[]{String.valueOf(userId)}
        );
    }

    public Cursor getAllOrders() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT o.*, " +
                        "u.name AS customer_name, " +
                        "u.username AS customer_username, " +
                        "u.email AS customer_email " +
                        "FROM orders o " +
                        "LEFT JOIN users u ON o.user_id = u.id " +
                        "ORDER BY o.id DESC",
                null
        );
    }

    public Cursor getOrderWithUser(long orderId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT o.*, " +
                        "u.name AS customer_name, " +
                        "u.username AS customer_username, " +
                        "u.email AS customer_email, " +
                        "u.phone AS customer_phone " +
                        "FROM orders o " +
                        "LEFT JOIN users u ON o.user_id = u.id " +
                        "WHERE o.id = ? LIMIT 1",
                new String[]{String.valueOf(orderId)}
        );
    }

    public Cursor getOrdersByUserId(int userId) {
        return getOrdersByUser(userId);
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

    public Cursor getOrderItemsByOrderId(long orderId) {
        return getOrderItems(orderId);
    }

    public Cursor getOrdersByStatus(int userId, String status) {
        SQLiteDatabase db = this.getReadableDatabase();

        status = normalizeOrderStatus(status);

        return db.rawQuery(
                "SELECT * FROM orders WHERE user_id = ? AND LOWER(status) = LOWER(?) ORDER BY id DESC",
                new String[]{String.valueOf(userId), status}
        );
    }

    public Cursor getActiveOrders(int userId){

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM orders " +
                        "WHERE user_id=? " +
                        "AND is_received=0 " +
                        "AND LOWER(status) NOT IN ('cancelled', 'canceled') " +
                        "ORDER BY id DESC",
                new String[]{
                        String.valueOf(userId)
                }
        );
    }

    public Cursor getPastOrders(int userId){

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM orders " +
                        "WHERE user_id=? " +
                        "AND (is_received=1 OR LOWER(status) IN ('cancelled', 'canceled')) " +
                        "ORDER BY id DESC",
                new String[]{
                        String.valueOf(userId)
                }
        );
    }

    public int getOrderCountByStatus(int userId, String status) {
        SQLiteDatabase db = this.getReadableDatabase();

        status = normalizeOrderStatus(status);

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) AS total FROM orders WHERE user_id = ? AND LOWER(status) = LOWER(?)",
                new String[]{String.valueOf(userId), status}
        );

        int total = 0;

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }

        cursor.close();
        return total;
    }
    public boolean updateOrderStatus(long orderId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        status = normalizeOrderStatus(status);

        ContentValues values = new ContentValues();
        values.put("status", status);

        if (STATUS_CANCELLED.equals(status)) {
            values.put("is_received", 1);
        } else {
            values.put("is_received", 0);
        }

        int result = db.update(
                "orders",
                values,
                "id = ?",
                new String[]{String.valueOf(orderId)}
        );

        return result > 0;
    }

    public boolean confirmOrderReceived(long orderId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("is_received", 1);

        int result = db.update(
                "orders",
                values,
                "id = ?",
                new String[]{String.valueOf(orderId)}
        );

        return result > 0;
    }

    public boolean isOrderReceived(long orderId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT is_received FROM orders WHERE id = ?",
                new String[]{String.valueOf(orderId)}
        );

        boolean received = false;

        if(cursor.moveToFirst()){
            received =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("is_received")
                    ) == 1;
        }

        cursor.close();

        return received;
    }

    public OrderItem getFirstOrderItem(long orderId){

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM order_items WHERE order_id=? LIMIT 1",
                new String[]{String.valueOf(orderId)}
        );

        OrderItem item = null;

        if(cursor.moveToFirst()){

            item = new OrderItem();

            item.setId(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            );

            item.setOrderId(
                    cursor.getInt(cursor.getColumnIndexOrThrow("order_id"))
            );

            item.setProductId(
                    cursor.getString(cursor.getColumnIndexOrThrow("product_id"))
            );

            item.setProductName(
                    cursor.getString(cursor.getColumnIndexOrThrow("product_name"))
            );

            item.setProductImage(
                    cursor.getString(cursor.getColumnIndexOrThrow("product_image"))
            );

            item.setPrice(
                    cursor.getInt(cursor.getColumnIndexOrThrow("price"))
            );

            item.setQuantity(
                    cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
            );
        }

        cursor.close();

        return item;
    }

    public void normalizeLegacyOrderStatuses(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues packingValues = new ContentValues();
        packingValues.put("status", STATUS_PACKING);

        db.update(
                "orders",
                packingValues,
                "user_id = ? AND LOWER(status) IN ('confirmed')",
                new String[]{String.valueOf(userId)}
        );

        ContentValues shippingValues = new ContentValues();
        shippingValues.put("status", STATUS_SHIPPING);

        db.update(
                "orders",
                shippingValues,
                "user_id = ? AND LOWER(status) IN ('shipped', 'in_transit')",
                new String[]{String.valueOf(userId)}
        );

        ContentValues deliveredValues = new ContentValues();
        deliveredValues.put("status", STATUS_DELIVERED);

        db.update(
                "orders",
                deliveredValues,
                "user_id = ? AND LOWER(status) IN ('delivery', 'complete', 'completed')",
                new String[]{String.valueOf(userId)}
        );

        ContentValues cancelledValues = new ContentValues();
        cancelledValues.put("status", STATUS_CANCELLED);
        cancelledValues.put("is_received", 1);

        db.update(
                "orders",
                cancelledValues,
                "user_id = ? AND LOWER(status) IN ('cancel', 'canceled')",
                new String[]{String.valueOf(userId)}
        );
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
                "user_id = ? AND LOWER(keyword) = LOWER(?)",
                new String[]{String.valueOf(userId), keyword}
        );

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("keyword", keyword);
        values.put("created_at", now());

        long result = db.insert("recent_searches", null, values);
        return result != -1;
    }

    public boolean insertRecentSearch(int userId, String keyword) {
        return addRecentSearch(userId, keyword);
    }

    public Cursor getRecentSearches(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM recent_searches WHERE user_id = ? ORDER BY id DESC LIMIT 10",
                new String[]{String.valueOf(userId)}
        );
    }

    public Cursor getRecentSearchesByUserId(int userId) {
        return getRecentSearches(userId);
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

        Cursor cursor = db.rawQuery(
                "SELECT id FROM settings WHERE user_id = ? LIMIT 1",
                new String[]{String.valueOf(userId)}
        );

        boolean alreadyExists = cursor.moveToFirst();
        cursor.close();

        if (alreadyExists) {
            return true;
        }

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("dark_mode", 0);
        values.put("notification_enabled", 1);
        values.put("selected_theme", "light");

        long result = db.insert("settings", null, values);
        return result != -1;
    }

    public boolean insertDefaultSettings(int userId) {
        return createDefaultSettings(userId);
    }

    public Cursor getSettings(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM settings WHERE user_id = ? LIMIT 1",
                new String[]{String.valueOf(userId)}
        );
    }

    public boolean getDarkMode(int userId) {
        Cursor cursor = getSettings(userId);

        boolean darkMode = false;

        if (cursor.moveToFirst()) {
            darkMode = cursor.getInt(cursor.getColumnIndexOrThrow("dark_mode")) == 1;
        }

        cursor.close();
        return darkMode;
    }

    public boolean updateDarkMode(int userId, boolean darkMode) {
        SQLiteDatabase db = this.getWritableDatabase();

        createDefaultSettings(userId);

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

        createDefaultSettings(userId);

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

    public boolean saveDarkModeSetting(int userId, boolean enabled) {
        return updateDarkMode(userId, enabled);
    }

    public boolean getDarkModeSetting(int userId) {
        return getDarkMode(userId);
    }
}