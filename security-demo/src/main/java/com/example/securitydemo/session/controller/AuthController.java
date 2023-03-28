package com.example.securitydemo.session.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Profile("session")
@Controller
@RequestMapping("/auth")
public class AuthController {
    @GetMapping("/login_demo")
    public void login(HttpServletRequest request) {
        String referrer = request.getHeader("Referer");
        request.getSession().setAttribute("prevPage", referrer);
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