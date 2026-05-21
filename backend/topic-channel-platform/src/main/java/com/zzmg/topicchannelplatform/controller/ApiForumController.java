package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.dto.ForumDetailDTO;
import com.zzmg.topicchannelplatform.dto.ForumItemDTO;
import com.zzmg.topicchannelplatform.dto.ForumListItemDTO;
import com.zzmg.topicchannelplatform.entity.Forum;
import com.zzmg.topicchannelplatform.service.ForumMemberService;
import com.zzmg.topicchannelplatform.service.ForumService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiForumController {

    private final ForumService forumService;
    private final ForumMemberService forumMemberService;

    public ApiForumController(ForumService forumService, ForumMemberService forumMemberService) {
        this.forumService = forumService;
        this.forumMemberService = forumMemberService;
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
}
