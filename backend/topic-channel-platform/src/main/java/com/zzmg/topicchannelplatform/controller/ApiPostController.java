package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.dto.PostListItemDTO;
import com.zzmg.topicchannelplatform.service.ThemePostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiPostController {

    private final ThemePostService postService;

    public ApiPostController(ThemePostService postService) {
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

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostListItemDTO> getPostDetail(@PathVariable Long id) {
        return postService.findApprovedPostById(id)
                .map(p -> ResponseEntity.ok(new PostListItemDTO(
                        p.getThemePostId(),
                        p.getTitle(),
                        p.getContent(),
                        p.getAuthor().getUserName(),
                        p.getForum().getForumName(),
                        p.getPublishTime()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
}
