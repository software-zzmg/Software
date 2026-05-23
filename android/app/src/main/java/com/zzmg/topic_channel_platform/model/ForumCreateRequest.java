package com.zzmg.topic_channel_platform.model;

public class ForumCreateRequest {
    private String forumName;
    private String description;

    public ForumCreateRequest(String forumName, String description) {
        this.forumName = forumName;
        this.description = description;
    }

    public String getForumName() { return forumName; }
    public String getDescription() { return description; }
}
