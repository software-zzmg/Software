package com.zzmg.topic_channel_platform.model;

public class PendingPostItem {
    private Long id;
    private String title;
    private String content;
    private String authorName;
    private String forumName;
    private String publishTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getForumName() { return forumName; }
    public void setForumName(String forumName) { this.forumName = forumName; }
    public String getPublishTime() { return publishTime; }
    public void setPublishTime(String publishTime) { this.publishTime = publishTime; }
}
