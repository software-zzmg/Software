package com.zzmg.topicchannelplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserInfoForm {

    @NotBlank(message = "用户名不能为空")
    private String userName;

    private String realName;

    private String gender;

    private String birthday;

    @Pattern(regexp = "^(|[1-9]\\d{5}(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx])$", message = "身份证格式不正确")
    private String idNumber;

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
