package com.zzmg.topicchannelplatform.dto;

public class LoginResponse {
    private boolean success;
    private String message;
    private String userId;
    private String userName;

    public LoginResponse(boolean success, String message, String userId, String userName) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.userName = userName;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
}
