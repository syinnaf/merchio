package com.example.merchio.models;

public class Setting {

    private int id;
    private int userId;
    private boolean darkMode;
    private boolean notificationEnabled;
    private String selectedTheme;

    public Setting() {
    }

    public Setting(int id, int userId, boolean darkMode, boolean notificationEnabled, String selectedTheme) {
        this.id = id;
        this.userId = userId;
        this.darkMode = darkMode;
        this.notificationEnabled = notificationEnabled;
        this.selectedTheme = selectedTheme;
    }

    public int getId() {
        return id;
    }

    public int getSettingId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSettingId(int settingId) {
        this.id = settingId;
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

    public boolean isDarkMode() {
        return darkMode;
    }

    public int getDarkModeValue() {
        return darkMode ? 1 : 0;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public void setDarkMode(int darkMode) {
        this.darkMode = darkMode == 1;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public int getNotificationEnabledValue() {
        return notificationEnabled ? 1 : 0;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public void setNotificationEnabled(int notificationEnabled) {
        this.notificationEnabled = notificationEnabled == 1;
    }

    public String getSelectedTheme() {
        return selectedTheme;
    }

    public String getSelected_theme() {
        return selectedTheme;
    }

    public void setSelectedTheme(String selectedTheme) {
        this.selectedTheme = selectedTheme;
    }

    public void setSelected_theme(String selectedTheme) {
        this.selectedTheme = selectedTheme;
    }
}