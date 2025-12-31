package com.example.keycloak.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listUsers_ShouldBeAccessibleByManager() throws Exception {
        mockMvc.perform(get("/manager/users")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_manager"))))
                .andExpect(status().isOk());
    }

    @Test
    void listUsers_ShouldReferbiddenForUser() throws Exception {
        mockMvc.perform(get("/manager/users")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void listUsers_ShouldBeUnauthorizedForAnonymous() throws Exception {
        mockMvc.perform(get("/manager/users"))
                .andExpect(status().isUnauthorized());
    }
}
