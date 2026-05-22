package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.dto.ForumCreateRequest;
import com.zzmg.topicchannelplatform.dto.ForumDetailDTO;
import com.zzmg.topicchannelplatform.dto.ForumItemDTO;
import com.zzmg.topicchannelplatform.dto.ForumListItemDTO;
import com.zzmg.topicchannelplatform.dto.PostListItemDTO;
import com.zzmg.topicchannelplatform.entity.Forum;
import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.service.ForumMemberService;
import com.zzmg.topicchannelplatform.service.ForumService;
import com.zzmg.topicchannelplatform.service.ThemePostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiForumController {

    private final ForumService forumService;
    private final ForumMemberService forumMemberService;
    private final ThemePostService postService;

    public ApiForumController(ForumService forumService,
                              ForumMemberService forumMemberService,
                              ThemePostService postService) {
        this.forumService = forumService;
        this.forumMemberService = forumMemberService;
        this.postService = postService;
    }

    @GetMapping("/forums")
    public List<ForumListItemDTO> getForums() {
        return forumService.findAll().stream()
                .map(f -> new ForumListItemDTO(
                        f.getForumId(),
                        f.getForumName(),
                        f.getContent(),
                        f.getCreator().getUserName()
                ))
                .toList();
    }

    @GetMapping("/forums/{id}")
    public ResponseEntity<?> getForumDetail(@PathVariable Long id, HttpSession session) {
        return forumService.findById(id)
                .filter(f -> "审核通过".equals(f.getAuditState()))
                .map(f -> {
                    String userId = (String) session.getAttribute("userId");
                    boolean joined = userId != null && forumMemberService.isMember(userId, id);
                    return ResponseEntity.ok(new ForumDetailDTO(
                            f.getForumId(),
                            f.getForumName(),
                            f.getContent(),
                            f.getCreator().getUserName(),
                            joined
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/forums/my")
    public ResponseEntity<?> getMyForums(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        List<ForumItemDTO> forums = forumMemberService.findByUserId(userId).stream()
                .filter(m -> "审核通过".equals(m.getForum().getAuditState()))
                .map(m -> new ForumItemDTO(m.getForum().getForumId(), m.getForum().getForumName()))
                .toList();
        return ResponseEntity.ok(forums);
    }

    @GetMapping("/forums/{forumId}/posts")
    public ResponseEntity<?> getForumPosts(@PathVariable Long forumId) {
        if (forumService.findById(forumId)
                .filter(f -> "审核通过".equals(f.getAuditState()))
                .isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<PostListItemDTO> posts = postService.findByForumId(forumId).stream()
                .sorted(Comparator.comparing(
                        p -> p.getPublishTime(), Comparator.nullsLast(Comparator.reverseOrder())))
                .map(p -> new PostListItemDTO(
                        p.getThemePostId(),
                        p.getTitle(),
                        p.getContent(),
                        p.getAuthor().getUserName(),
                        p.getForum().getForumName(),
                        p.getPublishTime()
                ))
                .toList();
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/forums/{id}/join")
    public ResponseEntity<Map<String, Object>> join(
            @PathVariable Long id, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        Forum forum = forumService.findById(id)
                .filter(f -> "审核通过".equals(f.getAuditState()))
                .orElse(null);
        if (forum == null) {
            return ResponseEntity.notFound().build();
        }
        if (forumMemberService.isMember(userId, id)) {
            return ResponseEntity.ok(
                    Map.of("success", false, "message", "已经加入该频道"));
        }
        forumMemberService.join(userId, id);
        return ResponseEntity.ok(
                Map.of("success", true, "message", "加入成功"));
    }

    @PostMapping("/forums/{id}/leave")
    public ResponseEntity<Map<String, Object>> leave(
            @PathVariable Long id, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        if (!forumMemberService.isMember(userId, id)) {
            return ResponseEntity.ok(
                    Map.of("success", false, "message", "尚未加入该频道"));
        }
        forumMemberService.leave(userId, id);
        return ResponseEntity.ok(
                Map.of("success", true, "message", "已退出频道"));
    }

    @PostMapping("/forums")
    public ResponseEntity<Map<String, Object>> createForum(
            @RequestBody ForumCreateRequest request, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        String forumName = request.getForumName();
        String description = request.getDescription();
        if (forumName == null || forumName.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "频道名不能为空"));
        }
        if (forumName.length() > 50) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "频道名长度不能超过50"));
        }
        if (description == null || description.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "频道简介不能为空"));
        }
        if (description.length() > 500) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "频道简介长度不能超过500"));
        }
        Forum forum = new Forum();
        forum.setForumName(forumName);
        forum.setContent(description);
        OrdinaryUser creator = new OrdinaryUser();
        creator.setUserId(userId);
        forum.setCreator(creator);
        forumService.create(forum);
        forumMemberService.join(userId, forum.getForumId());
        return ResponseEntity.ok(
                Map.of("success", true, "message", "频道创建申请已提交，等待审核通过后可见"));
    }
}
