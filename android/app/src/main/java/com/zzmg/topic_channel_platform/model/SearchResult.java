package com.zzmg.topic_channel_platform.model;

import java.util.List;

public class SearchResult {
    private List<PostListItem> posts;
    private List<ForumListItem> forums;
    private List<UserSearchItem> users;

    public List<PostListItem> getPosts() { return posts; }
    public void setPosts(List<PostListItem> posts) { this.posts = posts; }
    public List<ForumListItem> getForums() { return forums; }
    public void setForums(List<ForumListItem> forums) { this.forums = forums; }
    public List<UserSearchItem> getUsers() { return users; }
    public void setUsers(List<UserSearchItem> users) { this.users = users; }
}
