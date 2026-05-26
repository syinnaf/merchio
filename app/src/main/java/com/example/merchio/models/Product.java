package com.example.merchio.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private String id;
    private String name;
    private String description;
    private int price;
    private String image_url;
    private String type;
    private int stock;
    private String categoryName;
    private String brand;

    public Product() {
    }

    public Product(String id, String name, String description, int price, String image_url, String type, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image_url = image_url;
        this.type = type;
        this.stock = stock;
    }

    protected Product(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readInt();
        image_url = in.readString();
        type = in.readString();
        stock = in.readInt();
        categoryName = in.readString();
        brand = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getId() {
        return id;
    }

    public int getProductIdAsInt() {
        try {
            return Integer.parseInt(id);
        } catch (Exception e) {
            return 0;
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = String.valueOf(id);
    }

    public String getName() {
        return name;
    }

    public String getProductName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProductName(String productName) {
        this.name = productName;
    }

    public String getDescription() {
        return description;
    }

    public String getProductDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProductDescription(String productDescription) {
        this.description = productDescription;
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

    public String getImage_url() {
        return image_url;
    }

    public String getImageUrl() {
        return image_url;
    }

    public String getProductImage() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setImageUrl(String imageUrl) {
        this.image_url = imageUrl;
    }

    public void setProductImage(String productImage) {
        this.image_url = productImage;
    }

    public String getType() {
        return type;
    }

    public String getProductType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setProductType(String productType) {
        this.type = productType;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCategoryName() {
        return categoryName != null ? categoryName : "";
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrand() {
        return brand != null ? brand : "";
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(price);
        dest.writeString(image_url);
        dest.writeString(type);
        dest.writeInt(stock);
        dest.writeString(categoryName);
        dest.writeString(brand);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}