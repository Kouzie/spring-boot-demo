package com.example.jooq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {
    private Long id;
    private String name;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;
}

