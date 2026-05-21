package com.zzmg.topicchannelplatform.dto;

import java.time.LocalDateTime;

public class CommentItemDTO {

    private Long id;
    private String content;
    private String authorName;
    private LocalDateTime publishTime;

    public CommentItemDTO(Long id, String content, String authorName, LocalDateTime publishTime) {
        this.id = id;
        this.content = content;
        this.authorName = authorName;
        this.publishTime = publishTime;
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getAuthorName() { return authorName; }
    public LocalDateTime getPublishTime() { return publishTime; }
}
