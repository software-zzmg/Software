package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.dto.ForumListItemDTO;
import com.zzmg.topicchannelplatform.dto.PostListItemDTO;
import com.zzmg.topicchannelplatform.dto.SearchResultDTO;
import com.zzmg.topicchannelplatform.dto.UserSearchItemDTO;
import com.zzmg.topicchannelplatform.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiSearchController {

    private final SearchService searchService;

    public ApiSearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public SearchResultDTO search(@RequestParam(required = false) String keyword) {
        String trimmed = keyword == null ? "" : keyword.trim();
        if (trimmed.isEmpty()) {
            return new SearchResultDTO(List.of(), List.of(), List.of());
        }
        List<PostListItemDTO> posts = searchService.searchPosts(trimmed).stream()
                .map(p -> new PostListItemDTO(
                        p.getThemePostId(),
                        p.getTitle(),
                        p.getContent(),
                        p.getAuthor().getUserName(),
                        p.getForum().getForumName(),
                        p.getPublishTime()
                ))
                .toList();
        List<ForumListItemDTO> forums = searchService.searchForums(trimmed).stream()
                .map(f -> new ForumListItemDTO(
                        f.getForumId(),
                        f.getForumName(),
                        f.getContent(),
                        f.getCreator().getUserName()
                ))
                .toList();
        List<UserSearchItemDTO> users = searchService.searchUsers(trimmed).stream()
                .map(u -> new UserSearchItemDTO(u.getUserId(), u.getUserName()))
                .toList();
        return new SearchResultDTO(posts, forums, users);
    }
}
