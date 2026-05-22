package com.zzmg.topicchannelplatform.dto;

import java.time.LocalDateTime;

public class PendingCommentItemDTO {
    private Long id;
    private String content;
    private String authorName;
    private String postTitle;
    private LocalDateTime publishTime;

    public PendingCommentItemDTO(Long id, String content, String authorName,
                                  String postTitle, LocalDateTime publishTime) {
        this.id = id;
        this.content = content;
        this.authorName = authorName;
        this.postTitle = postTitle;
        this.publishTime = publishTime;
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getAuthorName() { return authorName; }
    public String getPostTitle() { return postTitle; }
    public LocalDateTime getPublishTime() { return publishTime; }
}
