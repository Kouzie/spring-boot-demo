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
@Table("comment")
public class Comment {
    
    @Id
    @Column("id")
    private Long id;
    
    @Column("board_id")
    private Long boardId;
    
    @Column("content")
    private String content;
    
    @Column("author")
    private String author;
    
    @Column("created_at")
    private Instant createdAt;
    
    @Column("updated_at")
    private Instant updatedAt;
}

