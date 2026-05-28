package com.example.merchio.models;

public class Category {

    private int id;
    private String name;
    private String icon_url;

    public Category() {
    }

    public Category(int id,
                    String name,
                    String icon_url) {

        this.id = id;
        this.name = name;
        this.icon_url = icon_url;
    }

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCategoryId(int categoryId) {
        this.id = categoryId;
    }

    public String getName() {
        return name;
    }

    public String getCategoryName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategoryName(String categoryName) {
        this.name = categoryName;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public String getIconUrl() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public void setIconUrl(String iconUrl) {
        this.icon_url = iconUrl;
    }
}