package com.zzmg.topic_channel_platform.model;

public class ApiResponse {
    private boolean success;
    private String message;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
