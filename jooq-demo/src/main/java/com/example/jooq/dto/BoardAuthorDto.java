package com.example.jooq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Board + Author JOIN 결과를 담는 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardAuthorDto {
    private BoardDto board;
    private AuthorDto author;
}

