package com.example.merchio.models;

public class RecentSearch {

    private int id;
    private int userId;
    private String keyword;
    private String createdAt;

    public RecentSearch() {
    }

    public RecentSearch(int id, int userId, String keyword, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.keyword = keyword;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getSearchId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSearchId(int searchId) {
        this.id = searchId;
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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