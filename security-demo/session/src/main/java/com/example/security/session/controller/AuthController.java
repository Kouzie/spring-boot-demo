package com.example.security.session.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login_demo")
    public void login(HttpServletRequest request) {
        String referrer = request.getHeader("Referer");
        request.getSession().setAttribute("prevPage", referrer); // 로그인 이전 페이지 이동을 위해 session 에 url 저장
        // /resource/template/auth/login_demo.html 생성 필요
    }

    @GetMapping("/access_denied")
    public void accessDenied() {
        // /resource/template/auth/access_denied.html 생성 필요
    }

    @PostMapping("/login_success")
    public String loginSuccess() {
        return "redirect:/boards/list";
    }
}