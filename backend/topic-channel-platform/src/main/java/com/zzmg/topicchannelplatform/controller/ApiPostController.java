package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.dto.CommentCreateRequest;
import com.zzmg.topicchannelplatform.dto.CommentItemDTO;
import com.zzmg.topicchannelplatform.dto.PostCreateRequest;
import com.zzmg.topicchannelplatform.dto.PostListItemDTO;
import com.zzmg.topicchannelplatform.entity.Comment;
import com.zzmg.topicchannelplatform.entity.ThemePost;
import com.zzmg.topicchannelplatform.service.CollectService;
import com.zzmg.topicchannelplatform.service.CommentService;
import com.zzmg.topicchannelplatform.service.ThemePostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiPostController {

    private final ThemePostService postService;
    private final CommentService commentService;
    private final CollectService collectService;

    public ApiPostController(ThemePostService postService,
                             CommentService commentService,
                             CollectService collectService) {
        this.postService = postService;
        this.commentService = commentService;
        this.collectService = collectService;
    }

    @PostMapping("/posts")
    public ResponseEntity<Map<String, Object>> createPost(
            @Valid @RequestBody PostCreateRequest request, BindingResult bindingResult,
            HttpSession session) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message",
                            bindingResult.getFieldError().getDefaultMessage()));
        }
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        try {
            ThemePost post = new ThemePost();
            post.setTitle(request.getTitle());
            post.setContent(request.getContent());
            postService.publishPost(post, userId, request.getForumId());
            return ResponseEntity.ok(
                    Map.of("success", true, "message", "发布中，等待审核成功后可见"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
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

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentItemDTO>> getComments(@PathVariable Long postId,
                                                             HttpSession session) {
        if (postService.findApprovedPostById(postId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        String userId = (String) session.getAttribute("userId");
        return ResponseEntity.ok(
                commentService.findApprovedCommentsByPostId(postId).stream()
                        .map(c -> new CommentItemDTO(
                                c.getCommentId(),
                                c.getContent(),
                                c.getAuthor().getUserName(),
                                c.getPublishTime(),
                                userId != null && userId.equals(c.getAuthor().getUserId())
                        ))
                        .toList()
        );
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(
            @PathVariable Long postId, @PathVariable Long commentId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        Comment comment = commentService.findById(commentId).orElse(null);
        if (comment == null || !comment.getThemePost().getThemePostId().equals(postId)) {
            return ResponseEntity.notFound().build();
        }
        if (!userId.equals(comment.getAuthor().getUserId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("success", false, "message", "只能删除自己的评论"));
        }
        commentService.deleteById(commentId);
        return ResponseEntity.ok(Map.of("success", true, "message", "评论已删除"));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Object>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request, BindingResult bindingResult,
            HttpSession session) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message",
                            bindingResult.getFieldError().getDefaultMessage()));
        }
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        if (postService.findApprovedPostById(postId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            commentService.publishComment(postId, userId, request.getContent());
            return ResponseEntity.ok(
                    Map.of("success", true, "message", "发布中，等待审核成功后可见"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/posts/{postId}/collect-status")
    public ResponseEntity<?> getCollectStatus(@PathVariable Long postId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        if (postService.findApprovedPostById(postId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                Map.of("collected", collectService.isCollected(userId, postId)));
    }

    @PostMapping("/posts/{postId}/collect")
    public ResponseEntity<Map<String, Object>> collect(
            @PathVariable Long postId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        if (postService.findApprovedPostById(postId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (collectService.isCollected(userId, postId)) {
            return ResponseEntity.ok(
                    Map.of("success", false, "message", "已经收藏过了"));
        }
        collectService.collect(userId, postId);
        return ResponseEntity.ok(
                Map.of("success", true, "message", "收藏成功"));
    }

    @PostMapping("/posts/{postId}/collect/cancel")
    public ResponseEntity<Map<String, Object>> cancelCollect(
            @PathVariable Long postId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        if (postService.findApprovedPostById(postId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!collectService.isCollected(userId, postId)) {
            return ResponseEntity.ok(
                    Map.of("success", false, "message", "尚未收藏"));
        }
        collectService.cancelCollect(userId, postId);
        return ResponseEntity.ok(
                Map.of("success", true, "message", "已取消收藏"));
    }
}
