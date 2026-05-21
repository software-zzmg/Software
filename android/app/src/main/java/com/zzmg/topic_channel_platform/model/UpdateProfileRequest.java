package com.zzmg.topic_channel_platform.model;

public class UpdateProfileRequest {
    private String userName;
    private String realName;
    private String gender;
    private String birthday;
    private String idNumber;

    public UpdateProfileRequest(String userName, String realName, String gender,
                                String birthday, String idNumber) {
        this.userName = userName;
        this.realName = realName;
        this.gender = gender;
        this.birthday = birthday;
        this.idNumber = idNumber;
    }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
}
