package com.zzmg.topicchannelplatform.dto;

public class ForumDetailDTO {
    private Long id;
    private String forumName;
    private String description;
    private String creatorName;
    private String creatorUserId;
    private boolean isCreator;
    private boolean joined;

    public ForumDetailDTO(Long id, String forumName, String description,
                          String creatorName, String creatorUserId,
                          boolean isCreator, boolean joined) {
        this.id = id;
        this.forumName = forumName;
        this.description = description;
        this.creatorName = creatorName;
        this.creatorUserId = creatorUserId;
        this.isCreator = isCreator;
        this.joined = joined;
    }

    public Long getId() { return id; }
    public String getForumName() { return forumName; }
    public String getDescription() { return description; }
    public String getCreatorName() { return creatorName; }
    public String getCreatorUserId() { return creatorUserId; }
    public boolean isCreator() { return isCreator; }
    public boolean isJoined() { return joined; }
}
