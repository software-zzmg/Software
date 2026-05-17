package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.entity.ForumMember;
import com.zzmg.topicchannelplatform.service.ForumMemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/forum-member")
public class ForumMemberController {

    private final ForumMemberService forumMemberService;

    public ForumMemberController(ForumMemberService forumMemberService) {
        this.forumMemberService = forumMemberService;
    }

    @PostMapping("/join")
    public String join(@RequestParam String forumId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        if (!forumMemberService.isMember(userId, forumId)) {
            try {
                forumMemberService.join(userId, forumId);
            } catch (IllegalStateException ignored) {
            }
        }
        return "redirect:/forum/detail/" + forumId;
    }

    @PostMapping("/leave")
    public String leave(@RequestParam String forumId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        forumMemberService.leave(userId, forumId);
        return "redirect:/forum/detail/" + forumId;
    }

    @GetMapping("/my")
    public String myForums(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        List<ForumMember> memberships = forumMemberService.findByUserId(userId);
        model.addAttribute("memberships", memberships);
        return "forum-member/my";
    }
}
