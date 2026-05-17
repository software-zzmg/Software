package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.entity.ThemePost;
import com.zzmg.topicchannelplatform.service.ForumService;
import com.zzmg.topicchannelplatform.service.ThemePostService;
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
@RequestMapping("/post")
public class ThemePostController {

    private final ThemePostService postService;
    private final ForumService forumService;

    public ThemePostController(ThemePostService postService, ForumService forumService) {
        this.postService = postService;
        this.forumService = forumService;
    }

    @GetMapping("/list/{forumId}")
    public String list(@PathVariable String forumId, Model model) {
        return forumService.findById(forumId).map(forum -> {
            model.addAttribute("forum", forum);
            List<ThemePost> posts = postService.findByForumId(forumId);
            model.addAttribute("posts", posts);
            return "post/list";
        }).orElse("redirect:/forum/list");
    }

    @GetMapping("/detail/{postId}")
    public String detail(@PathVariable String postId, HttpSession session, Model model) {
        return postService.findById(postId).map(post -> {
            model.addAttribute("post", post);
            String userId = (String) session.getAttribute("userId");
            model.addAttribute("isAuthor", userId != null && userId.equals(post.getAuthor().getUserId()));
            return "post/detail";
        }).orElse("redirect:/forum/list");
    }

    @GetMapping("/create/{forumId}")
    public String createPage(@PathVariable String forumId, HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        return forumService.findById(forumId).map(forum -> {
            model.addAttribute("forum", forum);
            return "post/edit";
        }).orElse("redirect:/forum/list");
    }

    @PostMapping("/create")
    public String create(@RequestParam String forumId,
                         @RequestParam String title,
                         @RequestParam String content,
                         HttpSession session,
                         Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        ThemePost post = new ThemePost();
        post.setForum(forumService.findById(forumId).orElse(null));
        post.setTitle(title);
        post.setContent(content);
        OrdinaryUser author = new OrdinaryUser();
        author.setUserId(userId);
        post.setAuthor(author);
        try {
            postService.publish(post, userId);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            forumService.findById(forumId).ifPresent(f -> model.addAttribute("forum", f));
            return "post/edit";
        }
        return "redirect:/post/detail/" + post.getThemePostId();
    }

    @GetMapping("/edit/{postId}")
    public String editPage(@PathVariable String postId, HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        return postService.findById(postId).map(post -> {
            if (!post.getAuthor().getUserId().equals(userId)) {
                return "redirect:/post/detail/" + postId;
            }
            model.addAttribute("post", post);
            model.addAttribute("forum", post.getForum());
            return "post/edit";
        }).orElse("redirect:/forum/list");
    }

    @PostMapping("/edit")
    public String edit(@RequestParam String postId,
                       @RequestParam String title,
                       @RequestParam String content,
                       HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        if (!postService.isAuthor(userId, postId)) {
            return "redirect:/post/detail/" + postId;
        }
        ThemePost post = new ThemePost();
        post.setThemePostId(postId);
        post.setTitle(title);
        post.setContent(content);
        postService.update(post);
        return "redirect:/post/detail/" + postId;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam String postId, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        String forumId = postService.findById(postId)
                .map(p -> p.getForum().getForumId())
                .orElse(null);
        if (postService.isAuthor(userId, postId)) {
            postService.deleteById(postId);
        }
        return forumId != null ? "redirect:/post/list/" + forumId : "redirect:/forum/list";
    }
}
