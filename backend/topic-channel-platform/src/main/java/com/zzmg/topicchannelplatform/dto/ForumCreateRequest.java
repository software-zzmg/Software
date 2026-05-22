package com.zzmg.topicchannelplatform.dto;

public class ForumCreateRequest {
    private String forumName;
    private String description;

    public String getForumName() { return forumName; }
    public void setForumName(String forumName) { this.forumName = forumName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
