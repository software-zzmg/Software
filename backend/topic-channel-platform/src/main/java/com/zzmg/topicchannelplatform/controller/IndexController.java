package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.service.ThemePostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final ThemePostService postService;

    public IndexController(ThemePostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("posts", postService.findApproved());
        return "index";
    }
}
