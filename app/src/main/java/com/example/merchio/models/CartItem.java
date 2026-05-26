package com.example.merchio.models;

public class CartItem {

    private int id;
    private int userId;
    private String productId;
    private String productName;
    private String productImage;
    private int productPrice;
    private String type;
    private int quantity;
    private int stock;
    private boolean checked;
    private String createdAt;

    public CartItem() {
    }

    public CartItem(int id, int userId, String productId, String productName, String productImage, int productPrice, String type, int quantity, int stock, boolean checked, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.type = type;
        this.quantity = quantity;
        this.stock = stock;
        this.checked = checked;
        this.createdAt = createdAt;
    }

    public int getSubtotal() {
        return productPrice * quantity;
    }

    public int getId() {
        return id;
    }

    public int getCartId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCartId(int cartId) {
        this.id = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public int getUser_id() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUser_id(int userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public String getProduct_id() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProduct_id(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProduct_name() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProduct_name(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getProduct_image() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setProduct_image(String productImage) {
        this.productImage = productImage;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public int getProduct_price() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public void setProduct_price(int productPrice) {
        this.productPrice = productPrice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isChecked() {
        return checked;
    }

    public int getIsChecked() {
        return checked ? 1 : 0;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setIsChecked(int isChecked) {
        this.checked = isChecked == 1;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getCreated_at() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreated_at(String createdAt) {
        this.createdAt = createdAt;
    }
}