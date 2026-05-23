package com.zzmg.topic_channel_platform.model;

public class SendCodeRequest {
    private String phoneNumber;

    public SendCodeRequest(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPhoneNumber() { return phoneNumber; }
}
