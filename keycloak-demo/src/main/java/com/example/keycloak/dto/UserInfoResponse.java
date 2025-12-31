package com.example.keycloak.dto;

import lombok.Builder;

import java.util.Map;

/**
 * OIDC UserInfo Endpoint 응답 DTO
 * 
 * 반환되는 정보 (표준 OIDC Claims):
 * - sub: 사용자 ID (Subject)
 * - preferred_username: 사용자명
 * - email: 이메일
 * - email_verified: 이메일 인증 여부
 * - name: 전체 이름
 * - given_name: 이름
 * - family_name: 성
 * - 기타 Keycloak에 설정된 추가 클레임
 */
@Builder
public record UserInfoResponse(
        Map<String, Object> userInfo,
        String apiError) {
    public static UserInfoResponse error(String message) {
        return UserInfoResponse.builder()
                .apiError(message)
                .build();
    }
}
