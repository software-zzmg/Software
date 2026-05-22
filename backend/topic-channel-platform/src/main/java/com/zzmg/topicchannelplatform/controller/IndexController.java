package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.service.CollectService;
import com.zzmg.topicchannelplatform.service.ForumService;
import com.zzmg.topicchannelplatform.service.ThemePostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class IndexController {

    private final ThemePostService postService;
    private final ForumService forumService;
    private final CollectService collectService;

    public IndexController(ThemePostService postService, ForumService forumService, CollectService collectService) {
        this.postService = postService;
        this.forumService = forumService;
        this.collectService = collectService;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        model.addAttribute("forums", forumService.findAll());
        model.addAttribute("posts", postService.findApproved());
        String userId = (String) session.getAttribute("userId");
        if (userId != null) {
            Set<Long> collectedIds = collectService.findByUserId(userId).stream()
                    .map(c -> c.getThemePost().getThemePostId())
                    .collect(Collectors.toSet());
            model.addAttribute("collectedPostIds", collectedIds);
        }
        return "index";
    }
}
