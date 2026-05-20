package com.zzmg.topicchannelplatform.dto;

public class LoginRequest {
    private String phoneNumber;
    private String userPassword;

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getUserPassword() { return userPassword; }
    public void setUserPassword(String userPassword) { this.userPassword = userPassword; }
}
