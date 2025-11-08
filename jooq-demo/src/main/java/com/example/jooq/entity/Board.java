package com.example.jooq.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("board")  // Spring Data JDBC용
public class Board {
    
    @Id
    @Column("id")
    private Long id;
    
    @Column("title")
    private String title;
    
    @Column("content")
    private String content;
    
    @Column("author_id")
    private Long authorId;
    
    @Column("created_at")
    private Instant createdAt;
    
    @Column("updated_at")
    private Instant updatedAt;
    
    @Column("view_count")
    private Integer viewCount;
}

