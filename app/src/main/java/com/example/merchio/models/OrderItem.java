package com.example.merchio.models;

public class OrderItem {

    private int id;
    private int orderId;
    private String productId;
    private String productName;
    private String productImage;
    private int price;
    private int quantity;
    private String type;

    public OrderItem() {
    }

    public OrderItem(int id, int orderId, String productId, String productName, String productImage, int price, int quantity, String type) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.price = price;
        this.quantity = quantity;
        this.type = type;
    }

    public int getSubtotal() {
        return price * quantity;
    }

    public int getId() {
        return id;
    }

    public int getOrderItemId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrderItemId(int orderItemId) {
        this.id = orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getOrder_id() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setOrder_id(int orderId) {
        this.orderId = orderId;
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

    public int getPrice() {
        return price;
    }

    public int getProductPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setProductPrice(int productPrice) {
        this.price = productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}