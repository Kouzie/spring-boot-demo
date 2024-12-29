package com.example.security.rest.controller;

import com.example.security.common.model.MemberEntity;
import com.example.security.common.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sample")
@RequiredArgsConstructor
public class SampleController {

    private final MemberService service;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "This is accessible by ROLE_ADMIN only.";
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public String managerEndpoint() {
        return "This is accessible by ROLE_MANAGER and above.";
    }

    @GetMapping("/uname/{uname}")
    public String userEndpoint(@PathVariable String uname) {
        return service.findByUnameSecurity(uname).toString();
    }

    /**
     * SecurityContext 에 저장된 authentication 객체 가져옴
     */
    @GetMapping("/user/{id}")
    @PreAuthorize("@cssecu.hasAccess(authentication, #id)")
    public String accessResource(@PathVariable Long id) {
        return "Resource with ID: " + id;
    }

    /**
     * PermissionEvaluator 구현체인 CustomPermissionEvaluator.hasPermission 함수 사용
     * */
    @PostMapping("/user/{id}")
    @PreAuthorize("hasPermission(#id, 'MemberEntity', 'READ')")
    public MemberEntity updateResource(@PathVariable Long id) {
        return service.findById(id);
    }

}