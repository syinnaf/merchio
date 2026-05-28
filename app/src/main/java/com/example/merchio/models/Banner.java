package com.example.merchio.models;

public class Banner {

    private int id;
    private String title;
    private String subtitle;
    private String image_url;
    private String product_id;

    public Banner() {
    }

    public Banner(int id,
                  String title,
                  String subtitle,
                  String image_url,
                  String product_id) {

        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.image_url = image_url;
        this.product_id = product_id;
    }

    public int getId() {
        return id;
    }

    public int getBannerId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBannerId(int bannerId) {
        this.id = bannerId;
    }

    public String getTitle() {
        return title;
    }

    public String getBannerTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBannerTitle(String bannerTitle) {
        this.title = bannerTitle;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getBannerSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setBannerSubtitle(String bannerSubtitle) {
        this.subtitle = bannerSubtitle;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getImageUrl() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setImageUrl(String imageUrl) {
        this.image_url = imageUrl;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getProductId() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setProductId(String productId) {
        this.product_id = productId;
    }
}