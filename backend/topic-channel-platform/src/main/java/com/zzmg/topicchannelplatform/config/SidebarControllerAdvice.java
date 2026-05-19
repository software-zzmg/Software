package com.zzmg.topicchannelplatform.config;

import com.zzmg.topicchannelplatform.entity.ForumMember;
import com.zzmg.topicchannelplatform.service.ForumMemberService;
import com.zzmg.topicchannelplatform.service.ForumService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class SidebarControllerAdvice {

    private final ForumMemberService forumMemberService;
    private final ForumService forumService;

    public SidebarControllerAdvice(ForumMemberService forumMemberService,
                                   ForumService forumService) {
        this.forumMemberService = forumMemberService;
        this.forumService = forumService;
    }

    @ModelAttribute
    public void addSidebarData(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("userId") : null;

        if (userId != null) {
            List<ForumMember> memberships = forumMemberService.findByUserId(userId);
            model.addAttribute("sidebarJoinedForums",
                    memberships.stream()
                            .map(ForumMember::getForum)
                            .filter(f -> "审核通过".equals(f.getAuditState()))
                            .toList());
        }

        model.addAttribute("sidebarAllForums", forumService.findAll());
    }
}
