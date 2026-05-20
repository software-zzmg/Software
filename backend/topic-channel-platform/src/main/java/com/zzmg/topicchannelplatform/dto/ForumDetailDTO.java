package com.zzmg.topicchannelplatform.dto;

public class ForumDetailDTO {
    private Long id;
    private String forumName;
    private String description;
    private String creatorName;
    private boolean joined;

    public ForumDetailDTO(Long id, String forumName, String description,
                          String creatorName, boolean joined) {
        this.id = id;
        this.forumName = forumName;
        this.description = description;
        this.creatorName = creatorName;
        this.joined = joined;
    }

    public Long getId() { return id; }
    public String getForumName() { return forumName; }
    public String getDescription() { return description; }
    public String getCreatorName() { return creatorName; }
    public boolean isJoined() { return joined; }
}
