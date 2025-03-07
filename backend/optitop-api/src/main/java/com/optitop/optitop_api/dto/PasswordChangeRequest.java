package com.optitop.optitop_api.dto;

public class PasswordChangeRequest {
    private String currentPassword;
    private String newPassword;

    // Getters et Setters
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}