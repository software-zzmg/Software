package com.zzmg.topic_channel_platform.model;

public class ForumDetail {
    private Long id;
    private String forumName;
    private String description;
    private String creatorName;
    private boolean joined;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getForumName() { return forumName; }
    public void setForumName(String forumName) { this.forumName = forumName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public boolean isJoined() { return joined; }
    public void setJoined(boolean joined) { this.joined = joined; }
}
