package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.service.AdministratorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdministratorService adminService;

    public AdminController(AdministratorService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String userId,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        return adminService.login(userId, password).map(admin -> {
            session.setAttribute("adminId", admin.getUserId());
            return "redirect:/admin";
        }).orElseGet(() -> {
            model.addAttribute("error", "管理员账号或密码错误");
            return "admin/login";
        });
    }
    // TODO: 管理员主界面可显示所有待审核（并计数），保留对帖子、频道、评论的单独筛选
    // TODO: 添加自动审核功能
    // TODO: 优化管理员界面
    @GetMapping("")
    public String dashboard(HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        return "admin/dashboard";
    }

    @GetMapping("/forums")
    public String forums(HttpSession session, Model model) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("forums", adminService.findPendingForums());
        return "admin/forums";
    }

    @PostMapping("/forums/{id}/approve")
    public String approveForum(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        adminService.auditForum(id, true);
        return "redirect:/admin/forums";
    }

    @PostMapping("/forums/{id}/reject")
    public String rejectForum(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        adminService.auditForum(id, false);
        return "redirect:/admin/forums";
    }

    @GetMapping("/posts")
    public String posts(HttpSession session, Model model) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("posts", adminService.findPendingPosts());
        return "admin/posts";
    }

    @PostMapping("/posts/{id}/approve")
    public String approvePost(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        adminService.auditThemePost(id, true);
        return "redirect:/admin/posts";
    }

    @PostMapping("/posts/{id}/reject")
    public String rejectPost(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        adminService.auditThemePost(id, false);
        return "redirect:/admin/posts";
    }

    @GetMapping("/comments")
    public String comments(HttpSession session, Model model) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("comments", adminService.findPendingComments());
        return "admin/comments";
    }

    @PostMapping("/comments/{id}/approve")
    public String approveComment(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        adminService.auditComment(id, true);
        return "redirect:/admin/comments";
    }

    @PostMapping("/comments/{id}/reject")
    public String rejectComment(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        adminService.auditComment(id, false);
        return "redirect:/admin/comments";
    }
}
