package com.example.merchio.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private String id;
    private String name;
    private String category_id;
    private String category_name;
    private String brand;
    private String type;
    private int price;
    private int stock;
    private int sold_count;
    private String description;
    private String image_url;
    private int is_paidpromote;

    public Product() {
    }

    public Product(String id,
                   String name,
                   String category_id,
                   String category_name,
                   String brand,
                   String type,
                   int price,
                   int stock,
                   int sold_count,
                   String description,
                   String image_url,
                   int is_paidpromote) {

        this.id = id;
        this.name = name;
        this.category_id = category_id;
        this.category_name = category_name;
        this.brand = brand;
        this.type = type;
        this.price = price;
        this.stock = stock;
        this.sold_count = sold_count;
        this.description = description;
        this.image_url = image_url;
        this.is_paidpromote = is_paidpromote;
    }

    protected Product(Parcel in) {

        id = in.readString();
        name = in.readString();
        category_id = in.readString();
        category_name = in.readString();
        brand = in.readString();
        type = in.readString();
        price = in.readInt();
        stock = in.readInt();
        sold_count = in.readInt();
        description = in.readString();
        image_url = in.readString();
        is_paidpromote = in.readInt();
    }

    public static final Creator<Product> CREATOR =
            new Creator<Product>() {

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

    public String getCategory_id() {
        return category_id;
    }

    public String getCategoryId() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public void setCategoryId(String categoryId) {
        this.category_id = categoryId;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getCategoryName() {
        return category_name != null
                ? category_name
                : "";
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public void setCategoryName(String categoryName) {
        this.category_name = categoryName;
    }

    public String getBrand() {
        return brand != null
                ? brand
                : "";
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getSold_count() {
        return sold_count;
    }

    public int getSoldCount() {
        return sold_count;
    }

    public void setSold_count(int sold_count) {
        this.sold_count = sold_count;
    }

    public void setSoldCount(int soldCount) {
        this.sold_count = soldCount;
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

    public int getIs_paidpromote() {
        return is_paidpromote;
    }

    public int getIsPaidPromote() {
        return is_paidpromote;
    }

    public void setIs_paidpromote(int is_paidpromote) {
        this.is_paidpromote = is_paidpromote;
    }

    public void setIsPaidPromote(int isPaidPromote) {
        this.is_paidpromote = isPaidPromote;
    }

    @Override
    public void writeToParcel(Parcel dest,
                              int flags) {

        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(category_id);
        dest.writeString(category_name);
        dest.writeString(brand);
        dest.writeString(type);
        dest.writeInt(price);
        dest.writeInt(stock);
        dest.writeInt(sold_count);
        dest.writeString(description);
        dest.writeString(image_url);
        dest.writeInt(is_paidpromote);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}