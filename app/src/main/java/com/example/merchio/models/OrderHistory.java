package com.example.merchio.models;

public class OrderHistory {

    public long orderId;
    public String productName;
    public String image;
    public String status;
    public int totalPrice;
    public boolean isReceived;

    public OrderHistory(
            long orderId,
            String productName,
            String image,
            String status,
            int totalPrice,
            boolean isReceived
    ){
        this.orderId = orderId;
        this.productName = productName;
        this.image = image;
        this.status = status;
        this.totalPrice = totalPrice;
        this.isReceived = isReceived;
    }
}