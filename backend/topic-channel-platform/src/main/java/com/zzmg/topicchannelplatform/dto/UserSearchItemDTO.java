package com.zzmg.topicchannelplatform.dto;

public class UserSearchItemDTO {
    private String userId;
    private String userName;

    public UserSearchItemDTO(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
}
