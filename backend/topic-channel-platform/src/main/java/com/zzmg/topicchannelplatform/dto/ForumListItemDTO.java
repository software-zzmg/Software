package com.zzmg.topicchannelplatform.dto;

public class ForumListItemDTO {
    private Long id;
    private String forumName;
    private String description;
    private String creatorName;

    public ForumListItemDTO(Long id, String forumName, String description, String creatorName) {
        this.id = id;
        this.forumName = forumName;
        this.description = description;
        this.creatorName = creatorName;
    }

    public Long getId() { return id; }
    public String getForumName() { return forumName; }
    public String getDescription() { return description; }
    public String getCreatorName() { return creatorName; }
}
