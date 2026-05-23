package com.zzmg.topicchannelplatform.dto;

import java.time.LocalDateTime;

public class PostListItemDTO {

    private Long id;
    private String title;
    private String content;
    private String authorName;
    private String forumName;
    private LocalDateTime publishTime;
    private boolean canDelete;

    public PostListItemDTO(Long id, String title, String content,
                           String authorName, String forumName,
                           LocalDateTime publishTime) {
        this(id, title, content, authorName, forumName, publishTime, false);
    }

    public PostListItemDTO(Long id, String title, String content,
                           String authorName, String forumName,
                           LocalDateTime publishTime, boolean canDelete) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.forumName = forumName;
        this.publishTime = publishTime;
        this.canDelete = canDelete;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthorName() { return authorName; }
    public String getForumName() { return forumName; }
    public LocalDateTime getPublishTime() { return publishTime; }
    public boolean isCanDelete() { return canDelete; }
}
