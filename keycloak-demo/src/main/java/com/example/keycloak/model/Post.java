package com.example.keycloak.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("POST")
public record Post(
        @Id String id,
        String title,
        String content,
        String author,
        @Version Integer version) {
}
