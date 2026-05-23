package com.zzmg.topic_channel_platform.model;

public class RegisterRequest {
    private String phoneNumber;
    private String email;
    private String userName;
    private String password;
    private String confirmPassword;
    private String verificationCode;

    public RegisterRequest(String phoneNumber, String email, String userName, String password,
                           String confirmPassword, String verificationCode) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.verificationCode = verificationCode;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }
    public String getVerificationCode() { return verificationCode; }
}
