package com.example.jooq.vo;

import com.example.jooq.entity.Author;
import com.example.jooq.entity.Board;
import com.example.jooq.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Board + Author JOIN 결과를 담는 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardAuthorVo {
    private Board board;
    private Author author;
    private List<Comment> comments;
}

