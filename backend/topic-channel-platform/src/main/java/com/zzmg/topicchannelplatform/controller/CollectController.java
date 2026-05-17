package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.entity.Collect;
import com.zzmg.topicchannelplatform.service.CollectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/collect")
public class CollectController {

    private final CollectService collectService;

    public CollectController(CollectService collectService) {
        this.collectService = collectService;
    }

    @PostMapping("/add")
    public String add(@RequestParam String postId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        collectService.collect(userId, postId);
        return "redirect:/post/detail/" + postId;
    }

    @PostMapping("/cancel")
    public String cancel(@RequestParam String postId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        collectService.cancelCollect(userId, postId);
        return "redirect:/post/detail/" + postId;
    }

    @GetMapping("/my")
    public String my(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        List<Collect> collects = collectService.findByUserId(userId);
        model.addAttribute("collects", collects);
        return "collect/list";
    }
}
