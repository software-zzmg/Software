package com.zzmg.topicchannelplatform.dto;

public class UserProfileDTO {
    private String userId;
    private String userName;
    private String phoneNumber;
    private String realName;
    private String gender;
    private String birthday;
    private String idNumber;

    public UserProfileDTO(String userId, String userName, String phoneNumber,
                          String realName, String gender, String birthday, String idNumber) {
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.realName = realName;
        this.gender = gender;
        this.birthday = birthday;
        this.idNumber = idNumber;
    }

    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getRealName() { return realName; }
    public String getGender() { return gender; }
    public String getBirthday() { return birthday; }
    public String getIdNumber() { return idNumber; }
}
