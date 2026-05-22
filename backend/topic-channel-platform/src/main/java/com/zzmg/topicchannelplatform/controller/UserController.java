package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.dto.UserInfoForm;
import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.service.UserService;
import com.zzmg.topicchannelplatform.service.VerificationCodeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final VerificationCodeService verificationCodeService;

    public UserController(UserService userService,
                           VerificationCodeService verificationCodeService) {
        this.userService = userService;
        this.verificationCodeService = verificationCodeService;
    }
    @GetMapping("/login")
    public String loginPage() {
        return "user/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "user/register";
    }

    @GetMapping("/info")
    public String infoPage(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        userService.findById(userId).ifPresent(u -> model.addAttribute("user", u));
        return "user/userinfo-detail";
    }

    @GetMapping("/info/edit")
    public String editInfoPage(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        userService.findById(userId).ifPresent(u -> {
            if (!model.containsAttribute("userInfoForm")) {
                UserInfoForm form = new UserInfoForm();
                form.setUserName(u.getUserName());
                form.setRealName(u.getRealName());
                form.setGender(u.getGender());
                form.setBirthday(u.getBirthday() != null ? u.getBirthday().toLocalDate().toString() : "");
                form.setIdNumber(u.getIdNumber());
                model.addAttribute("userInfoForm", form);
            }
        });
        return "user/userinfo-edit";
    }

    @GetMapping("/password/change")
    public String changePasswordPage(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/user/login";
        }
        return "user/userpsd-edit";
    }

    @GetMapping("/password/forgot")
    public String forgotPasswordPage() {
        return "user/userpsd-forget";
    }

    @PostMapping("/login")
    public String login(@RequestParam String phoneNumber,
                        @RequestParam String userPassword,
                        HttpSession session,
                        Model model) {
        Optional<OrdinaryUser> user = userService.login(phoneNumber, userPassword);
        if (user.isPresent()) {
            session.setAttribute("userId", user.get().getUserId());
            return "redirect:/";
        }
        model.addAttribute("error", "手机号或密码错误");
        model.addAttribute("phoneNumber", phoneNumber);
        return "user/login";
    }

    @PostMapping("/register/send-code")
    public String sendRegisterCode(@RequestParam String phoneNumber,
                                   HttpSession session,
                                   Model model) {
        model.addAttribute("phoneNumber", phoneNumber);
        if (!phoneNumber.matches("1\\d{10}")) {
            model.addAttribute("error", "手机号格式错误");
            return "user/register";
        }
        if (userService.findByPhone(phoneNumber).isPresent()) {
            model.addAttribute("error", "该手机号已被注册");
            return "user/register";
        }
        verificationCodeService.sendCode("register", phoneNumber, session);
        model.addAttribute("codeSent", true);
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String phoneNumber,
                           @RequestParam String userName,
                           @RequestParam String userPassword,
                           @RequestParam String confirmPassword,
                           @RequestParam String verificationCode,
                           HttpSession session,
                           Model model) {
        model.addAttribute("phoneNumber", phoneNumber);
        model.addAttribute("userName", userName);
        if (!userPassword.equals(confirmPassword)) {
            model.addAttribute("error", "两次输入的密码不一致");
            model.addAttribute("codeSent", true);
            return "user/register";
        }
        String error = verificationCodeService.validate("register", phoneNumber,
                verificationCode, session);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("codeSent", true);
            return "user/register";
        }
        OrdinaryUser user = new OrdinaryUser();
        user.setPhoneNumber(phoneNumber);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        if (!userService.register(user)) {
            model.addAttribute("error", "该手机号已被注册");
            model.addAttribute("codeSent", true);
            return "user/register";
        }
        return "redirect:/user/login";
    }

    @PostMapping("/info/update")
    public String updateInfo(@Valid @ModelAttribute("userInfoForm") UserInfoForm form,
                             BindingResult bindingResult,
                             HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        if (bindingResult.hasErrors()) {
            return "user/userinfo-edit";
        }
        OrdinaryUser user = new OrdinaryUser();
        user.setUserId(userId);
        user.setUserName(form.getUserName());
        user.setRealName(form.getRealName());
        user.setGender(form.getGender());
        user.setIdNumber(form.getIdNumber());
        String birthday = form.getBirthday();
        if (birthday != null && !birthday.isBlank()) {
            try {
                user.setBirthday(LocalDate.parse(birthday).atStartOfDay());
            } catch (DateTimeParseException ex) {
                bindingResult.rejectValue("birthday", "birthday.format", "日期格式不正确");
                return "user/userinfo-edit";
            }
        }
        userService.updateInfo(user);
        return "redirect:/user/info";
    }

    @PostMapping("/password/change")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "两次输入的新密码不一致");
            model.addAttribute("errorField", "newPassword");
            return "user/userpsd-edit";
        }
        if (!userService.changePassword(userId, oldPassword, newPassword)) {
            model.addAttribute("error", "原密码错误");
            model.addAttribute("errorField", "oldPassword");
            return "user/userpsd-edit";
        }
        return "redirect:/user/info";
    }

    @PostMapping("/password/forgot/send-code")
    public String sendResetCode(@RequestParam String phoneNumber,
                                HttpSession session,
                                Model model) {
        model.addAttribute("phoneNumber", phoneNumber);
        if (userService.findByPhone(phoneNumber).isEmpty()) {
            model.addAttribute("error", "该手机号未注册");
            return "user/userpsd-forget";
        }
        verificationCodeService.sendCode("reset", phoneNumber, session);
        model.addAttribute("resetPhone", phoneNumber);
        model.addAttribute("codeSent", true);
        return "user/userpsd-forget";
    }

    @PostMapping("/password/reset")
    public String resetPassword(@RequestParam String phoneNumber,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                @RequestParam String verificationCode,
                                HttpSession session,
                                Model model) {
        model.addAttribute("phoneNumber", phoneNumber);
        model.addAttribute("resetPhone", phoneNumber);
        model.addAttribute("codeSent", true);
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "两次输入的密码不一致");
            return "user/userpsd-forget";
        }
        String error = verificationCodeService.validate("reset", phoneNumber,
                verificationCode, session);
        if (error != null) {
            model.addAttribute("error", error);
            return "user/userpsd-forget";
        }
        userService.resetPassword(phoneNumber, newPassword);
        return "redirect:/user/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String deleteUser(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId != null) {
            userService.deleteUser(userId);
            session.invalidate();
        }
        return "redirect:/user/login";
    }
}
