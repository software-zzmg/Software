package com.zzmg.topic_channel_platform.model;

public class ForumItem {
    private Long id;
    private String forumName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getForumName() { return forumName; }
    public void setForumName(String forumName) { this.forumName = forumName; }

    @Override
    public String toString() {
        return forumName;
    }
}
