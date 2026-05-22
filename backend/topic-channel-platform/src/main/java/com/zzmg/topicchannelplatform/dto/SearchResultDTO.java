package com.zzmg.topicchannelplatform.dto;

import java.util.List;

public class SearchResultDTO {
    private List<PostListItemDTO> posts;
    private List<ForumListItemDTO> forums;
    private List<UserSearchItemDTO> users;

    public SearchResultDTO(List<PostListItemDTO> posts,
                           List<ForumListItemDTO> forums,
                           List<UserSearchItemDTO> users) {
        this.posts = posts;
        this.forums = forums;
        this.users = users;
    }

    public List<PostListItemDTO> getPosts() { return posts; }
    public List<ForumListItemDTO> getForums() { return forums; }
    public List<UserSearchItemDTO> getUsers() { return users; }
}
