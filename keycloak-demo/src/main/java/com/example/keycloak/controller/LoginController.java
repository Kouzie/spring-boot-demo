package com.example.keycloak.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/select-realm")
    public String selectRealmPage() {
        return "select-realm";
    }

    @PostMapping("/select-realm")
    public String postSelectRealm(@RequestParam String realm) {
        // Redirect to the OAuth2 authorization endpoint with the specific
        // registrationId (realm name)
        return "redirect:/oauth2/authorization/" + realm;
    }
}
