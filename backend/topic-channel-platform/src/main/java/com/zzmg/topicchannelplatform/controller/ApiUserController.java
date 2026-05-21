package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.dto.ChangePasswordRequest;
import com.zzmg.topicchannelplatform.dto.ForumListItemDTO;
import com.zzmg.topicchannelplatform.dto.LoginRequest;
import com.zzmg.topicchannelplatform.dto.LoginResponse;
import com.zzmg.topicchannelplatform.dto.PostListItemDTO;
import com.zzmg.topicchannelplatform.dto.UpdateProfileRequest;
import com.zzmg.topicchannelplatform.dto.UserProfileDTO;
import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.service.CollectService;
import com.zzmg.topicchannelplatform.service.ForumMemberService;
import com.zzmg.topicchannelplatform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiUserController {

    private final UserService userService;
    private final CollectService collectService;
    private final ForumMemberService forumMemberService;

    public ApiUserController(UserService userService,
                             CollectService collectService,
                             ForumMemberService forumMemberService) {
        this.userService = userService;
        this.collectService = collectService;
        this.forumMemberService = forumMemberService;
    }

    @PostMapping("/users/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpSession session) {
        Optional<OrdinaryUser> user = userService.login(
                request.getPhoneNumber(), request.getUserPassword());
        if (user.isPresent()) {
            OrdinaryUser u = user.get();
            session.setAttribute("userId", u.getUserId());
            return new LoginResponse(true, "登录成功", u.getUserId(), u.getUserName());
        }
        session.invalidate();
        return new LoginResponse(false, "手机号或密码错误", null, null);
    }

    @GetMapping("/users/me")
    public ResponseEntity<?> getMe(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        OrdinaryUser user = userService.findById(userId).orElseThrow();
        return ResponseEntity.ok(new UserProfileDTO(
                user.getUserId(),
                user.getUserName(),
                user.getPhoneNumber(),
                user.getRealName(),
                user.getGender(),
                user.getBirthday() != null
                        ? user.getBirthday().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        : null,
                user.getIdNumber()
        ));
    }

    @PutMapping("/users/me")
    public ResponseEntity<Map<String, Object>> updateMe(
            @RequestBody UpdateProfileRequest request, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        OrdinaryUser user = new OrdinaryUser();
        user.setUserId(userId);
        user.setUserName(request.getUserName());
        user.setRealName(request.getRealName());
        user.setGender(request.getGender());
        user.setIdNumber(request.getIdNumber());
        if (request.getBirthday() != null && !request.getBirthday().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(request.getBirthday(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (date.getYear() < 100 || date.getYear() > 2026) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("success", false, "message", "年份必须在 100-2026 之间"));
                }
                user.setBirthday(date.atStartOfDay());
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "生日格式或日期不正确，请使用 yyyy-MM-dd"));
            }
        }
        userService.updateInfo(user);
        return ResponseEntity.ok(Map.of("success", true, "message", "保存成功"));
    }

    @PostMapping("/users/me/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestBody ChangePasswordRequest request, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        if (request.getOldPassword() == null || request.getOldPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "请输入原密码"));
        }
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "请输入新密码"));
        }
        if (request.getConfirmPassword() == null || request.getConfirmPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "请确认新密码"));
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "两次密码输入不一致"));
        }
        boolean ok = userService.changePassword(userId,
                request.getOldPassword(), request.getNewPassword());
        if (!ok) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "原密码错误"));
        }
        return ResponseEntity.ok(Map.of("success", true, "message", "密码修改成功"));
    }

    @GetMapping("/users/me/forums")
    public ResponseEntity<?> getMyForums(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        List<ForumListItemDTO> forums = forumMemberService.findByUserId(userId).stream()
                .filter(m -> "审核通过".equals(m.getForum().getAuditState()))
                .map(m -> new ForumListItemDTO(
                        m.getForum().getForumId(),
                        m.getForum().getForumName(),
                        m.getForum().getContent(),
                        m.getForum().getCreator().getUserName()
                ))
                .toList();
        return ResponseEntity.ok(forums);
    }

    @PostMapping("/users/me/delete")
    public ResponseEntity<Map<String, Object>> deleteMe(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        userService.deleteUser(userId);
        session.invalidate();
        return ResponseEntity.ok(Map.of("success", true, "message", "账号已注销"));
    }

    @PostMapping("/users/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("success", true, "message", "已退出登录"));
    }

    @GetMapping("/users/me/collects")
    public ResponseEntity<?> getMyCollects(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "请先登录"));
        }
        List<PostListItemDTO> collects = collectService.findByUserId(userId).stream()
                .filter(c -> "审核通过".equals(c.getThemePost().getAuditState()))
                .map(c -> new PostListItemDTO(
                        c.getThemePost().getThemePostId(),
                        c.getThemePost().getTitle(),
                        c.getThemePost().getContent(),
                        c.getThemePost().getAuthor().getUserName(),
                        c.getThemePost().getForum().getForumName(),
                        c.getThemePost().getPublishTime()
                ))
                .toList();
        return ResponseEntity.ok(collects);
    }
}
