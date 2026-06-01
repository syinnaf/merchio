package com.example.merchio.models;

public class Order {

    private int id;
    private int userId;
    private String orderCode;
    private int totalPrice;
    private int shippingPrice;
    private int tax;
    private String paymentMethod;
    private String shippingMethod;
    private int addressId;
    private String address;
    private String status;
    private String orderDate;
    private String estimatedArrival;

    private int isReceived;

    public int getIsReceived() {
        return isReceived;
    }

    public void setIsReceived(int isReceived) {
        this.isReceived = isReceived;
    }

    public Order() {
    }

    public Order(int id, int userId, String orderCode, int totalPrice, int shippingPrice, int tax, String paymentMethod, String shippingMethod, int addressId, String address, String status, String orderDate, String estimatedArrival) {
        this.id = id;
        this.userId = userId;
        this.orderCode = orderCode;
        this.totalPrice = totalPrice;
        this.shippingPrice = shippingPrice;
        this.tax = tax;
        this.paymentMethod = paymentMethod;
        this.shippingMethod = shippingMethod;
        this.addressId = addressId;
        this.address = address;
        this.status = status;
        this.orderDate = orderDate;
        this.estimatedArrival = estimatedArrival;
    }

    public boolean isDelivered() {
        return status != null && status.equalsIgnoreCase("delivered");
    }

    public boolean isActive() {
        return status != null && !status.equalsIgnoreCase("delivered");
    }

    public int getId() {
        return id;
    }

    public int getOrderId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrderId(int orderId) {
        this.id = orderId;
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

    public String getOrderCode() {
        return orderCode;
    }

    public String getOrder_code() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public void setOrder_code(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getTotal_price() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTotal_price(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getShippingPrice() {
        return shippingPrice;
    }

    public int getShipping_price() {
        return shippingPrice;
    }

    public void setShippingPrice(int shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public void setShipping_price(int shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPayment_method() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPayment_method(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public String getShipping_method() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public void setShipping_method(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public int getAddressId() {
        return addressId;
    }

    public int getAddress_id() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public void setAddress_id(int addressId) {
        this.addressId = addressId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getOrder_date() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setOrder_date(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getEstimatedArrival() {
        return estimatedArrival;
    }

    public String getEstimated_arrival() {
        return estimatedArrival;
    }

    public void setEstimatedArrival(String estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    public void setEstimated_arrival(String estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }
}