package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.dto.LoginRequest;
import com.zzmg.topicchannelplatform.dto.LoginResponse;
import com.zzmg.topicchannelplatform.dto.PostListItemDTO;
import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.service.CollectService;
import com.zzmg.topicchannelplatform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiUserController {

    private final UserService userService;
    private final CollectService collectService;

    public ApiUserController(UserService userService, CollectService collectService) {
        this.userService = userService;
        this.collectService = collectService;
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
        return new LoginResponse(false, "手机号或密码错误", null, null);
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
