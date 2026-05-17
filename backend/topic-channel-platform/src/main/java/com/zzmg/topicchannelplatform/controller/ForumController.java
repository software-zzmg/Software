package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.entity.Forum;
import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.service.ForumMemberService;
import com.zzmg.topicchannelplatform.service.ForumService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/forum")
public class ForumController {

    private final ForumService forumService;
    private final ForumMemberService forumMemberService;

    public ForumController(ForumService forumService, ForumMemberService forumMemberService) {
        this.forumService = forumService;
        this.forumMemberService = forumMemberService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(required = false) String keyword, Model model) {
        List<Forum> forums;
        if (keyword != null && !keyword.isEmpty()) {
            forums = forumService.findByKeyword(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            forums = forumService.findAll();
        }
        model.addAttribute("forums", forums);
        return "forum/list";
    }

    @GetMapping("/detail/{forumId}")
    public String detail(@PathVariable String forumId, HttpSession session, Model model) {
        return forumService.findById(forumId).map(forum -> {
            model.addAttribute("forum", forum);
            String userId = (String) session.getAttribute("userId");
            if (userId != null) {
                model.addAttribute("isMember", forumMemberService.isMember(userId, forumId));
                model.addAttribute("isCreator", userId.equals(forum.getCreator().getUserId()));
            }
            return "forum/detail";
        }).orElse("redirect:/forum/list");
    }

    @GetMapping("/create")
    public String createPage(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/user/login";
        }
        return "forum/create";
    }

    @PostMapping("/create")
    public String create(@RequestParam String forumName,
                         @RequestParam String content,
                         HttpSession session,
                         Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        Forum forum = new Forum();
        forum.setForumName(forumName);
        forum.setContent(content);
        OrdinaryUser creator = new OrdinaryUser();
        creator.setUserId(userId);
        forum.setCreator(creator);
        forumService.create(forum);
        forumMemberService.join(userId, forum.getForumId());
        return "redirect:/forum/detail/" + forum.getForumId();
    }

    @GetMapping("/edit/{forumId}")
    public String editPage(@PathVariable String forumId, HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        return forumService.findById(forumId).map(forum -> {
            if (!forum.getCreator().getUserId().equals(userId)) {
                return "redirect:/forum/list";
            }
            model.addAttribute("forum", forum);
            return "forum/edit";
        }).orElse("redirect:/forum/list");
    }

    @PostMapping("/edit")
    public String edit(@RequestParam String forumId,
                       @RequestParam String forumName,
                       @RequestParam String content,
                       HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        forumService.findById(forumId).ifPresent(forum -> {
            if (forum.getCreator().getUserId().equals(userId)) {
                forum.setForumName(forumName);
                forum.setContent(content);
                forumService.update(forum);
            }
        });
        return "redirect:/forum/detail/" + forumId;
    }

    @PostMapping("/dismiss")
    public String dismiss(@RequestParam String forumId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        forumService.findById(forumId).ifPresent(forum -> {
            if (forum.getCreator().getUserId().equals(userId)) {
                forumService.deleteById(forumId);
            }
        });
        return "redirect:/forum/list";
    }
}
