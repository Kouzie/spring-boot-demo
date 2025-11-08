package com.example.jooq.service;

import com.example.jooq.dto.CommentDto;
import com.example.jooq.entity.Comment;
import com.example.jooq.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    
    // 게시판 ID로 댓글 조회
    public List<CommentDto> findByBoardId(Long boardId) {
        return commentRepository.findByBoardId(boardId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // 여러 게시판 ID로 댓글 조회
    public List<CommentDto> findByBoardIdIn(List<Long> boardIds) {
        return commentRepository.findByBoardIdIn(boardIds).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // 엔티티를 DTO로 변환
    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setBoardId(comment.getBoardId());
        dto.setContent(comment.getContent());
        dto.setAuthor(comment.getAuthor());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }
}

