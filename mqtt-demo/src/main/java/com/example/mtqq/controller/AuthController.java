package com.example.mtqq.controller;

import com.example.mtqq.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String ALLOW = "allow";
    private static final String DENY = "deny";

    @Autowired
    private AuthService authService;

    // Map을 이쁘게 포맷팅하는 헬퍼 메서드
    private String formatMap(Map<String, String> params) {
        return params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // 키 기준 정렬
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
    }

    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String authUser(@RequestParam Map<String, String> params) {
        log.info("Authenticating user: params={}", formatMap(params));
        String username = params.get("username");
        String password = params.get("password");
        String vhost = params.get("vhost");
        boolean enabled = false;
        if (StringUtils.hasText(vhost) && vhost.equals("mqtt")) {
            String clientId = params.get("client_id");
            enabled = authService.isCertificateEnabledMqtt(username, clientId);
        } else {
            enabled = authService.isCertificateEnabled(username, password);
        }

        if (enabled) {
            log.debug("User authentication successful: params={}", formatMap(params));
            return ALLOW;
        } else {
            log.warn("User authentication failed: params={}", formatMap(params));
            return DENY;
        }
    }

    @PostMapping(value = "/vhost", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String authVhost(@RequestParam Map<String, String> params) {
        log.info("Authorizing vhost access: params={}", formatMap(params));
        boolean enabled = true; // TODO: 실제 권한 확인 로직 구현

        if (enabled) {
            log.debug("Vhost access granted: params={}", formatMap(params));
            return ALLOW;
        } else {
            log.warn("Vhost access denied: params={}", formatMap(params));
            return DENY;
        }
    }

    @PostMapping(value = "/resource", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String authResource(@RequestParam Map<String, String> params) {
        log.info("Authorizing resource access: params={}", formatMap(params));
        boolean enabled = true; // TODO: 실제 권한 확인 로직 구현

        if (enabled) {
            log.debug("Resource access granted: params={}", formatMap(params));
            return ALLOW;
        } else {
            log.warn("Resource access denied: params={}", formatMap(params));
            return DENY;
        }
    }

    @PostMapping(value = "/topic", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String authTopic(@RequestParam Map<String, String> params) {
        log.info("Authorizing topic access: params={}", formatMap(params));
        boolean enabled = true; // TODO: 실제 권한 확인 로직 구현

        if (enabled) {
            log.debug("Topic access granted: params={}", formatMap(params));
            return ALLOW;
        } else {
            log.warn("Topic access denied: params={}", formatMap(params));
            return DENY;
        }
    }
}