package com.zzmg.topicchannelplatform.dto;

public class AdminLoginResponse {
    private boolean success;
    private String message;
    private String adminId;

    public AdminLoginResponse(boolean success, String message, String adminId) {
        this.success = success;
        this.message = message;
        this.adminId = adminId;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getAdminId() { return adminId; }
}
