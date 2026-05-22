package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.entity.Forum;
import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.service.CollectService;
import com.zzmg.topicchannelplatform.service.ForumMemberService;
import com.zzmg.topicchannelplatform.service.ForumService;
import com.zzmg.topicchannelplatform.service.ThemePostService;

import java.util.Set;
import java.util.stream.Collectors;
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

@Controller
@RequestMapping("/forum")
public class ForumController {

    private final ForumService forumService;
    private final ForumMemberService forumMemberService;
    private final ThemePostService postService;
    private final CollectService collectService;

    public ForumController(ForumService forumService,
                           ForumMemberService forumMemberService,
                           ThemePostService postService,
                           CollectService collectService) {
        this.forumService = forumService;
        this.forumMemberService = forumMemberService;
        this.postService = postService;
        this.collectService = collectService;
    }

    @GetMapping("/list")
    public String list() {
        return "redirect:/";
    }

    @GetMapping("/detail/{forumId}")
    public String detail(@PathVariable Long forumId, HttpSession session, Model model) {
        return forumService.findById(forumId).map(forum -> {
            String userId = (String) session.getAttribute("userId");
            boolean isCreator = userId != null && userId.equals(forum.getCreator().getUserId());
            boolean isAdmin = session.getAttribute("adminId") != null;
            if (!"审核通过".equals(forum.getAuditState()) && !isCreator && !isAdmin) {
                return "redirect:/forum/list";
            }
            model.addAttribute("forum", forum);
            model.addAttribute("posts", postService.findByForumId(forumId));
            if (userId != null) {
                model.addAttribute("isMember", forumMemberService.isMember(userId, forumId));
                model.addAttribute("isCreator", isCreator);
                Set<Long> collectedIds = collectService.findByUserId(userId).stream()
                        .map(c -> c.getThemePost().getThemePostId())
                        .collect(Collectors.toSet());
                model.addAttribute("collectedPostIds", collectedIds);
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
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        String userId = (String) session.getAttribute("userId");
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (userId == null) {
            if (isAjax) { writeJson(response, "{\"success\":false,\"error\":\"请先登录\"}"); return null; }
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
        if (isAjax) { writeJson(response, "{\"success\":true,\"toast\":\"已提交创建频道申请，等待审核成功后可见\"}"); return null; }
        redirectAttributes.addFlashAttribute("toast", "已提交创建频道申请，等待审核成功后可见");
        return "redirect:/";
    }

    @GetMapping("/edit/{forumId}")
    public String editPage(@PathVariable Long forumId, HttpSession session, Model model) {
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
    public String edit(@RequestParam Long forumId,
                       @RequestParam String forumName,
                       @RequestParam String content,
                       HttpSession session,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        String userId = (String) session.getAttribute("userId");
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (userId == null) {
            if (isAjax) { writeJson(response, "{\"success\":false,\"error\":\"请先登录\"}"); return null; }
            return "redirect:/user/login";
        }
        forumService.findById(forumId).ifPresent(forum -> {
            if (forum.getCreator().getUserId().equals(userId)) {
                forum.setForumName(forumName);
                forum.setContent(content);
                forumService.update(forum);
            }
        });
        if (isAjax) { writeJson(response, "{\"success\":true,\"toast\":\"频道信息已更新\"}"); return null; }
        return "redirect:/forum/detail/" + forumId;
    }

    @PostMapping("/dismiss")
    public String dismiss(@RequestParam Long forumId, HttpSession session,
                          RedirectAttributes redirectAttributes,
                          HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        String userId = (String) session.getAttribute("userId");
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (userId == null) {
            if (isAjax) { writeJson(response, "{\"success\":false,\"error\":\"请先登录\"}"); return null; }
            return "redirect:/user/login";
        }
        forumService.findById(forumId).ifPresent(forum -> {
            if (forum.getCreator().getUserId().equals(userId)) {
                forumService.deleteById(forumId);
            }
        });
        if (isAjax) { writeJson(response, "{\"success\":true,\"toast\":\"频道已解散\"}"); return null; }
        redirectAttributes.addFlashAttribute("toast", "频道已解散");
        return "redirect:/";
    }

    private void writeJson(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
    }
}
