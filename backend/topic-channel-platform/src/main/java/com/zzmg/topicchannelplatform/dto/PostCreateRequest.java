package com.zzmg.topicchannelplatform.dto;

public class PostCreateRequest {
    private Long forumId;
    private String title;
    private String content;

    public Long getForumId() { return forumId; }
    public void setForumId(Long forumId) { this.forumId = forumId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
