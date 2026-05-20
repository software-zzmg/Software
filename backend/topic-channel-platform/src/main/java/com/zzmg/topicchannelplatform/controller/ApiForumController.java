package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.dto.ForumItemDTO;
import com.zzmg.topicchannelplatform.service.ForumMemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiForumController {

    private final ForumMemberService forumMemberService;

    public ApiForumController(ForumMemberService forumMemberService) {
        this.forumMemberService = forumMemberService;
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
}
