package com.zzmg.topic_channel_platform.model;

public class LoginRequest {
    private String phoneNumber;
    private String userPassword;

    public LoginRequest(String phoneNumber, String userPassword) {
        this.phoneNumber = phoneNumber;
        this.userPassword = userPassword;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getUserPassword() { return userPassword; }
    public void setUserPassword(String userPassword) { this.userPassword = userPassword; }
}
