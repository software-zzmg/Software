package com.zzmg.topic_channel_platform.model;

import com.google.gson.annotations.SerializedName;

public class ForumDetail {
    private Long id;
    private String forumName;
    private String description;
    private String creatorName;
    private String creatorUserId;
    @SerializedName("creator")
    private boolean isCreator;
    private boolean joined;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getForumName() { return forumName; }
    public void setForumName(String n) { this.forumName = n; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String n) { this.creatorName = n; }
    public String getCreatorUserId() { return creatorUserId; }
    public void setCreatorUserId(String id) { this.creatorUserId = id; }
    public boolean isCreator() { return isCreator; }
    public void setCreator(boolean c) { this.isCreator = c; }
    public boolean isJoined() { return joined; }
    public void setJoined(boolean j) { this.joined = j; }
}
