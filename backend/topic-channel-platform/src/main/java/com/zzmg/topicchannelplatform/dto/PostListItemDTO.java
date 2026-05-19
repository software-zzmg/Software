package com.zzmg.topicchannelplatform.dto;

import java.time.LocalDateTime;

public class PostListItemDTO {

    private Long id;
    private String title;
    private String content;
    private String authorName;
    private String forumName;
    private LocalDateTime publishTime;

    public PostListItemDTO(Long id, String title, String content,
                           String authorName, String forumName,
                           LocalDateTime publishTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.forumName = forumName;
        this.publishTime = publishTime;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthorName() { return authorName; }
    public String getForumName() { return forumName; }
    public LocalDateTime getPublishTime() { return publishTime; }
}
