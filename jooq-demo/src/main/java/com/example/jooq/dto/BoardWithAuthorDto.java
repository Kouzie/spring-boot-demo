package com.example.jooq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Board + Author JOIN 결과를 담는 VO (Lombok 생성자 사용)
 * Spring Data JDBC는 생성자 매개변수 순서와 컬럼 순서를 매칭함
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardWithAuthorDto {
    // Board 필드
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer viewCount;
    
    // Author 필드
    private Long authorIdValue;  // author.id (컬럼명 충돌 방지)
    private String authorName;
    private String authorEmail;
    private Instant authorCreatedAt;
    private Instant authorUpdatedAt;
}

