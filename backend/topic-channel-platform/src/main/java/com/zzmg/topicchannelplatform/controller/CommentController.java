package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.entity.Comment;
import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.repository.CommentRepository;
import com.zzmg.topicchannelplatform.repository.ThemePostRepository;
import com.zzmg.topicchannelplatform.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final ThemePostRepository postRepository;
    private final CommentRepository commentRepository;

    public CommentController(CommentService commentService,
                             ThemePostRepository postRepository,
                             CommentRepository commentRepository) {
        this.commentService = commentService;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @PostMapping("/create")
    public String create(@RequestParam Long postId,
                         @RequestParam String content,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        Comment comment = new Comment();
        comment.setThemePost(postRepository.getReferenceById(postId));
        comment.setContent(content);
        OrdinaryUser author = new OrdinaryUser();
        author.setUserId(userId);
        comment.setAuthor(author);
        try {
            commentService.publish(comment, userId);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
        }
        redirectAttributes.addFlashAttribute("toast", "发布中，等待审核成功后可见");
        return "redirect:/post/detail/" + postId;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long commentId, HttpSession session,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        String userId = (String) session.getAttribute("userId");
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (userId == null) {
            if (isAjax) { writeJson(response, "{\"success\":false,\"error\":\"请先登录\"}"); return null; }
            return "redirect:/user/login";
        }
        Long postId = commentRepository.findById(commentId)
                .map(c -> c.getThemePost().getThemePostId())
                .orElse(null);
        if (postId != null && commentService.canDelete(userId, commentId)) {
            commentService.deleteById(commentId);
        }
        if (isAjax) { writeJson(response, "{\"success\":true,\"toast\":\"评论已删除\"}"); return null; }
        redirectAttributes.addFlashAttribute("toast", "评论已删除");
        return postId != null ? "redirect:/post/detail/" + postId : "redirect:/forum/list";
    }

    private void writeJson(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
    }
}
