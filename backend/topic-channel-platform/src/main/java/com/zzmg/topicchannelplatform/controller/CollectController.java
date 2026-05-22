package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.entity.Collect;
import com.zzmg.topicchannelplatform.service.CollectService;
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
@RequestMapping("/collect")
public class CollectController {

    private final CollectService collectService;

    public CollectController(CollectService collectService) {
        this.collectService = collectService;
    }

    @PostMapping("/add")
    public String add(@RequestParam Long postId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        collectService.collect(userId, postId);
        return "redirect:/post/detail/" + postId;
    }

    @PostMapping("/cancel")
    public String cancel(@RequestParam Long postId, HttpSession session,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        String userId = (String) session.getAttribute("userId");
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (userId == null) {
            if (isAjax) { writeJson(response, "{\"success\":false,\"error\":\"请先登录\"}"); return null; }
            return "redirect:/user/login";
        }
        collectService.cancelCollect(userId, postId);
        if (isAjax) { writeJson(response, "{\"success\":true,\"toast\":\"已取消收藏\"}"); return null; }
        return "redirect:/post/detail/" + postId;
    }

    private void writeJson(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
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
