package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.entity.ForumMember;
import com.zzmg.topicchannelplatform.service.ForumMemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/forum-member")
public class ForumMemberController {

    private final ForumMemberService forumMemberService;

    public ForumMemberController(ForumMemberService forumMemberService) {
        this.forumMemberService = forumMemberService;
    }

    @PostMapping("/join")
    public String join(@RequestParam Long forumId, HttpSession session,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        String userId = (String) session.getAttribute("userId");
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (userId == null) {
            if (isAjax) { writeJson(response, "{\"success\":false,\"error\":\"请先登录\"}"); return null; }
            return "redirect:/user/login";
        }
        if (!forumMemberService.isMember(userId, forumId)) {
            try {
                forumMemberService.join(userId, forumId);
            } catch (IllegalStateException ignored) {
            }
        }
        if (isAjax) { writeJson(response, "{\"success\":true,\"toast\":\"已加入频道\"}"); return null; }
        return "redirect:/forum/detail/" + forumId;
    }

    @PostMapping("/leave")
    public String leave(@RequestParam Long forumId, HttpSession session,
                        HttpServletRequest request,
                        HttpServletResponse response) throws IOException {
        String userId = (String) session.getAttribute("userId");
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (userId == null) {
            if (isAjax) { writeJson(response, "{\"success\":false,\"error\":\"请先登录\"}"); return null; }
            return "redirect:/user/login";
        }
        forumMemberService.leave(userId, forumId);
        if (isAjax) { writeJson(response, "{\"success\":true,\"toast\":\"已退出频道\"}"); return null; }
        return "redirect:/forum/detail/" + forumId;
    }

    private void writeJson(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
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
