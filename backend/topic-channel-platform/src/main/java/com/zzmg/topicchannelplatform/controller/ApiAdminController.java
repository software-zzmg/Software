package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.dto.AdminLoginRequest;
import com.zzmg.topicchannelplatform.dto.AdminLoginResponse;
import com.zzmg.topicchannelplatform.dto.PendingCommentItemDTO;
import com.zzmg.topicchannelplatform.dto.PendingForumItemDTO;
import com.zzmg.topicchannelplatform.dto.PendingPostItemDTO;
import com.zzmg.topicchannelplatform.entity.Administrator;
import com.zzmg.topicchannelplatform.service.AdministratorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class ApiAdminController {

    private final AdministratorService adminService;

    public ApiAdminController(AdministratorService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public AdminLoginResponse login(@RequestBody AdminLoginRequest request, HttpSession session) {
        Optional<Administrator> admin = adminService.login(
                request.getAdminId(), request.getPassword());
        if (admin.isPresent()) {
            session.setAttribute("adminId", admin.get().getUserId());
            return new AdminLoginResponse(true, "管理员登录成功", admin.get().getUserId());
        }
        return new AdminLoginResponse(false, "管理员账号或密码错误", null);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.removeAttribute("adminId");
        return ResponseEntity.ok(Map.of("success", true, "message", "管理员已退出登录"));
    }

    private boolean isAdmin(HttpSession session) {
        return session.getAttribute("adminId") != null;
    }

    @GetMapping("/forums/pending")
    public ResponseEntity<?> pendingForums(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先进行管理员登录"));
        }
        List<PendingForumItemDTO> forums = adminService.findPendingForums().stream()
                .map(f -> new PendingForumItemDTO(
                        f.getForumId(), f.getForumName(),
                        f.getContent(), f.getCreator().getUserName()))
                .toList();
        return ResponseEntity.ok(forums);
    }

    @PostMapping("/forums/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveForum(
            @PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先进行管理员登录"));
        }
        adminService.auditForum(id, true);
        return ResponseEntity.ok(Map.of("success", true, "message", "频道审核通过"));
    }

    @PostMapping("/forums/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectForum(
            @PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先进行管理员登录"));
        }
        adminService.auditForum(id, false);
        return ResponseEntity.ok(Map.of("success", true, "message", "频道已驳回"));
    }

    @GetMapping("/posts/pending")
    public ResponseEntity<?> pendingPosts(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先进行管理员登录"));
        }
        List<PendingPostItemDTO> posts = adminService.findPendingPosts().stream()
                .map(p -> new PendingPostItemDTO(
                        p.getThemePostId(), p.getTitle(), p.getContent(),
                        p.getAuthor().getUserName(), p.getForum().getForumName(),
                        p.getPublishTime()))
                .toList();
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/posts/{id}/approve")
    public ResponseEntity<Map<String, Object>> approvePost(
            @PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先进行管理员登录"));
        }
        adminService.auditThemePost(id, true);
        return ResponseEntity.ok(Map.of("success", true, "message", "帖子审核通过"));
    }

    @PostMapping("/posts/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectPost(
            @PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先进行管理员登录"));
        }
        adminService.auditThemePost(id, false);
        return ResponseEntity.ok(Map.of("success", true, "message", "帖子已驳回"));
    }

    @GetMapping("/comments/pending")
    public ResponseEntity<?> pendingComments(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先进行管理员登录"));
        }
        List<PendingCommentItemDTO> comments = adminService.findPendingComments().stream()
                .map(c -> new PendingCommentItemDTO(
                        c.getCommentId(), c.getContent(),
                        c.getAuthor().getUserName(),
                        c.getThemePost().getTitle(),
                        c.getPublishTime()))
                .toList();
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/comments/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveComment(
            @PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先进行管理员登录"));
        }
        adminService.auditComment(id, true);
        return ResponseEntity.ok(Map.of("success", true, "message", "评论审核通过"));
    }

    @PostMapping("/comments/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectComment(
            @PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先进行管理员登录"));
        }
        adminService.auditComment(id, false);
        return ResponseEntity.ok(Map.of("success", true, "message", "评论已驳回"));
    }
}
