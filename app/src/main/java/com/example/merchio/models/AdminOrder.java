package com.example.merchio.models;

public class AdminOrder {
    private long id;
    private String orderCode;
    private String customerName;
    private String customerEmail;
    private String orderDate;
    private int totalPrice;
    private int shippingPrice;
    private int tax;
    private String paymentMethod;
    private String address;
    private String status;

    public AdminOrder(long id, String orderCode, String customerName, String customerEmail,
                      String orderDate, int totalPrice, int shippingPrice, int tax,
                      String paymentMethod, String address, String status) {
        this.id = id;
        this.orderCode = orderCode;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.shippingPrice = shippingPrice;
        this.tax = tax;
        this.paymentMethod = paymentMethod;
        this.address = address;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getShippingPrice() {
        return shippingPrice;
    }

    public int getTax() {
        return tax;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getAddress() {
        return address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
