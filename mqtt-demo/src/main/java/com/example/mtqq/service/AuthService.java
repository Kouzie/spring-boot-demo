package com.example.mtqq.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    // 예: CN과 활성화 여부를 매핑한 데이터베이스
    private final Map<String, Boolean> certStatus = new HashMap<>();
    private final Map<String, Boolean> saslStatus = new HashMap<>();

    @PostConstruct
    public void init() {
        certStatus.put("my-client-123", true);
        certStatus.put("my-client-456", true);
        saslStatus.put("guest#guest", true);
    }

    /**
     * username 은 CN
     * clientId 는 입력값
     * */
    public boolean isCertificateEnabledMqtt(String username, String clientId) {
        if (clientId == null || clientId.isEmpty() || username == null || username.isEmpty()) {
            return false;
        }
        // 인증서의 중복사용을 막기 위해 CN 과 clientId 는 일치해야함
        if (!clientId.equals(username)) {
            return false;
        }
        return certStatus.getOrDefault(username, false);
    }

    public boolean isCertificateEnabled(String username, String password) {
        return saslStatus.getOrDefault(username + "#" + password, false);
    }
}
