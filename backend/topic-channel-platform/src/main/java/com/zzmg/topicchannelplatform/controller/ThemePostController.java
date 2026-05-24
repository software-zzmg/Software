package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.entity.Comment;
import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.entity.ThemePost;
import com.zzmg.topicchannelplatform.service.CollectService;
import com.zzmg.topicchannelplatform.service.CommentService;
import com.zzmg.topicchannelplatform.service.ForumMemberService;
import com.zzmg.topicchannelplatform.service.ForumService;
import com.zzmg.topicchannelplatform.service.ThemePostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/post")
public class ThemePostController {

    private final ThemePostService postService;
    private final ForumService forumService;
    private final CommentService commentService;
    private final ForumMemberService forumMemberService;
    private final CollectService collectService;

    public ThemePostController(ThemePostService postService, ForumService forumService,
                               CommentService commentService, ForumMemberService forumMemberService,
                               CollectService collectService) {
        this.postService = postService;
        this.forumService = forumService;
        this.commentService = commentService;
        this.forumMemberService = forumMemberService;
        this.collectService = collectService;
    }

    @GetMapping("/list/{forumId}")
    public String list(@PathVariable Long forumId) {
        return "redirect:/forum/detail/" + forumId;
    }

    @GetMapping("/detail/{postId}")
    public String detail(@PathVariable Long postId, HttpSession session, Model model) {
        return postService.findById(postId).map(post -> {
            String userId = (String) session.getAttribute("userId");
            boolean isAuthor = userId != null && userId.equals(post.getAuthor().getUserId());
            boolean isForumCreator = userId != null && userId.equals(post.getForum().getCreator().getUserId());
            boolean isAdmin = session.getAttribute("adminId") != null;
            if (!"审核通过".equals(post.getAuditState()) && !isAuthor && !isForumCreator && !isAdmin) {
                return "redirect:/forum/list";
            }
            model.addAttribute("post", post);
            model.addAttribute("currentUserId", userId);
            model.addAttribute("isAuthor", isAuthor);
            model.addAttribute("isForumCreator", isForumCreator);
            model.addAttribute("isMember", userId != null && forumMemberService.isMember(userId, post.getForum().getForumId()));
            model.addAttribute("isCollected", userId != null && collectService.isCollected(userId, postId));
            List<Comment> comments = commentService.findByThemePostId(postId);
            model.addAttribute("comments", comments);
            return "post/detail";
        }).orElse("redirect:/forum/list");
    }

    @GetMapping("/create/{forumId}")
    public String createPage(@PathVariable Long forumId, HttpSession session, Model model) {
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
    public String create(@RequestParam Long forumId,
                         @RequestParam String title,
                         @RequestParam String content,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        String userId = (String) session.getAttribute("userId");
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (userId == null) {
            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"error\":\"请先登录\"}");
                return null;
            }
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
            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
                return null;
            }
            model.addAttribute("error", e.getMessage());
            forumService.findById(forumId).ifPresent(f -> model.addAttribute("forum", f));
            return "post/edit";
        }
        if (isAjax) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":true,\"toast\":\"发布中，等待审核成功后可见\"}");
            return null;
        }
        redirectAttributes.addFlashAttribute("toast", "发布中，等待审核成功后可见");
        return "redirect:/forum/detail/" + forumId;
    }

    @GetMapping("/edit/{postId}")
    public String editPage(@PathVariable Long postId, HttpSession session, Model model) {
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
    public String edit(@RequestParam Long postId,
                       @RequestParam String title,
                       @RequestParam String content,
                       HttpSession session,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        String userId = (String) session.getAttribute("userId");
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (userId == null) {
            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"error\":\"请先登录\"}");
                return null;
            }
            return "redirect:/user/login";
        }
        if (!postService.isAuthor(userId, postId)) {
            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"error\":\"无权编辑此帖子\"}");
                return null;
            }
            return "redirect:/post/detail/" + postId;
        }
        ThemePost post = new ThemePost();
        post.setThemePostId(postId);
        post.setTitle(title);
        post.setContent(content);
        postService.update(post);
        if (isAjax) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":true,\"toast\":\"帖子已更新\"}");
            return null;
        }
        return "redirect:/post/detail/" + postId;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long postId, HttpSession session,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        String userId = (String) session.getAttribute("userId");
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (userId == null) {
            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"error\":\"请先登录\"}");
                return null;
            }
            return "redirect:/user/login";
        }
        Long forumId = postService.findById(postId)
                .map(p -> p.getForum().getForumId())
                .orElse(null);
        if (postService.canDelete(userId, postId)) {
            postService.deleteById(postId);
        }
        if (isAjax) {
            String redirect = forumId != null ? "/forum/detail/" + forumId : "/forum/list";
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":true,\"toast\":\"帖子已删除\",\"redirect\":\"" + redirect + "\"}");
            return null;
        }
        redirectAttributes.addFlashAttribute("toast", "帖子已删除");
        return forumId != null ? "redirect:/forum/detail/" + forumId : "redirect:/forum/list";
    }
}
