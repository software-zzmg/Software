package com.zzmg.topicchannelplatform.controller;

import com.zzmg.topicchannelplatform.dto.LoginRequest;
import com.zzmg.topicchannelplatform.dto.LoginResponse;
import com.zzmg.topicchannelplatform.entity.OrdinaryUser;
import com.zzmg.topicchannelplatform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiUserController {

    private final UserService userService;

    public ApiUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpSession session) {
        Optional<OrdinaryUser> user = userService.login(
                request.getPhoneNumber(), request.getUserPassword());
        if (user.isPresent()) {
            OrdinaryUser u = user.get();
            session.setAttribute("userId", u.getUserId());
            return new LoginResponse(true, "登录成功", u.getUserId(), u.getUserName());
        }
        return new LoginResponse(false, "手机号或密码错误", null, null);
    }
}
