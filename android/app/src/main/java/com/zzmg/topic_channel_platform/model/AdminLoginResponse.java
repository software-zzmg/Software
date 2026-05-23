package com.zzmg.topic_channel_platform.model;

public class AdminLoginResponse {
    private boolean success;
    private String message;
    private String adminId;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
}
