package com.zzmg.topic_channel_platform.model;

public class CommentCreateRequest {
    private String content;

    public CommentCreateRequest(String content) {
        this.content = content;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
