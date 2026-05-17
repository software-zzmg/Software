package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.service.ForumService;
import com.zzmg.topicchannelplatform.service.ThemePostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final ThemePostService postService;
    private final ForumService forumService;

    public IndexController(ThemePostService postService, ForumService forumService) {
        this.postService = postService;
        this.forumService = forumService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("forums", forumService.findAll());
        model.addAttribute("posts", postService.findApproved());
        return "index";
    }
}
