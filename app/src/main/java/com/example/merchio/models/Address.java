package com.example.merchio.models;

public class Address {

    private int id;
    private int userId;
    private String recipientName;
    private String phone;
    private String address;
    private String city;
    private String postalCode;
    private boolean defaultAddress;

    public Address() {
    }

    public Address(int id, int userId, String recipientName, String phone, String address, String city, String postalCode, boolean defaultAddress) {
        this.id = id;
        this.userId = userId;
        this.recipientName = recipientName;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.defaultAddress = defaultAddress;
    }

    public String getFullAddress() {
        return recipientName + ", " + phone + "\n" + address + ", " + city + ", " + postalCode;
    }

    public int getId() {
        return id;
    }

    public int getAddressId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAddressId(int addressId) {
        this.id = addressId;
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

    public String getRecipientName() {
        return recipientName;
    }

    public String getRecipient_name() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setRecipient_name(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getAddress() {
        return address;
    }

    public String getAddressText() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddressText(String addressText) {
        this.address = addressText;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPostal_code() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setPostal_code(String postalCode) {
        this.postalCode = postalCode;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public boolean isDefault() {
        return defaultAddress;
    }

    public int getIsDefault() {
        return defaultAddress ? 1 : 0;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public void setDefault(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public void setIsDefault(int isDefault) {
        this.defaultAddress = isDefault == 1;
    }
}