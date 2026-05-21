package com.zzmg.topicchannelplatform.dto;

public class ForumItemDTO {
    private Long id;
    private String forumName;

    public ForumItemDTO(Long id, String forumName) {
        this.id = id;
        this.forumName = forumName;
    }

    public Long getId() { return id; }
    public String getForumName() { return forumName; }
}
