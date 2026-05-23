package com.zzmg.topic_channel_platform.model;

public class SendCodeRequestAndroid {
    private String phoneNumber;
    private String email;

    public SendCodeRequestAndroid(String phoneNumber, String email) {
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
