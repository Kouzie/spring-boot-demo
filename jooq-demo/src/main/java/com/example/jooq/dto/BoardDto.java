package com.example.jooq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer viewCount;
}
