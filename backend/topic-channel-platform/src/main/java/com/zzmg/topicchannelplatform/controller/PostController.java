package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.dto.PostListItemDTO;
import com.zzmg.topicchannelplatform.service.ThemePostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {

    private final ThemePostService postService;

    public PostController(ThemePostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public List<PostListItemDTO> getPosts() {
        return postService.findApprovedPosts().stream()
                .map(p -> new PostListItemDTO(
                        p.getThemePostId(),
                        p.getTitle(),
                        p.getContent(),
                        p.getAuthor().getUserName(),
                        p.getForum().getForumName(),
                        p.getPublishTime()
                ))
                .toList();
    }
}
