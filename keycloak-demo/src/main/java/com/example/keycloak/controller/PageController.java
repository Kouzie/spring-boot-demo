package com.example.keycloak.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@Controller
public class PageController {

    @GetMapping("/board")
    public String boardPage(@AuthenticationPrincipal OidcUser principal, Model model) {
        if (principal != null) {
            model.addAttribute("username", principal.getPreferredUsername());
        }
        return "board";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/profile";
    }
}
