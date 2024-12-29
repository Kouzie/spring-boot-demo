package com.example.security.rest.config;

import com.example.security.common.model.MemberEntity;
import com.example.security.common.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {
    private final MemberRepository repository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject == null) return false;
        String username = authentication.getName();
        String requiredPermission = (String) permission;
        // member entity 에 대한 사용자 확인 및 권한 확인
        if (targetDomainObject instanceof MemberEntity entity) {
            return entity.getUname().equals(username) && "READ".equals(requiredPermission);
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if ("MemberEntity".equalsIgnoreCase(targetType)) {
            Long id = (Long) targetId;
            String requiredPermission = (String) permission;
            return repository.findById(id)
                    .filter(entity -> entity.getUname().equals(authentication.getName()) && "READ".equalsIgnoreCase(requiredPermission))
                    .isPresent();
        }
        return false;
    }
}