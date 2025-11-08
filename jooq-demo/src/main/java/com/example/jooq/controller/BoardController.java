package com.example.jooq.controller;

import com.example.jooq.dto.BoardAuthorDto;
import com.example.jooq.dto.req.BoardCreateRequest;
import com.example.jooq.dto.BoardDto;
import com.example.jooq.dto.req.BoardSearchRequest;
import com.example.jooq.dto.req.BoardUpdateRequest;
import com.example.jooq.dto.BoardWithAuthorDto;
import com.example.jooq.dto.CommentDto;
import com.example.jooq.service.BoardService;
import com.example.jooq.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    
    private final BoardService boardService;
    private final CommentService commentService;
    
    // 게시판 + 작성자
    @GetMapping
    public List<BoardWithAuthorDto> getAllBoardsWithAuthor() {
        return boardService.findAllWithAuthor();
    }
    
    // 게시판 ID로 조회: 게시판 + 작성자 + 댓글
    @GetMapping("/{id}")
    public Map<String, Object> getBoardByIdWithAuthorAndComments(@PathVariable Long id) {
        BoardAuthorDto boardAuthor = boardService.findByIdWithAuthor(id);
        List<CommentDto> comments = commentService.findByBoardId(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("board", boardAuthor.getBoard());
        result.put("author", boardAuthor.getAuthor());
        result.put("comments", comments);
        
        return result;
    }

    // 게시판 검색 (다이나믹 파라미터), 게시판 + 작성자
    @GetMapping("/search")
    public List<BoardAuthorDto> searchBoards(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String content) {
        BoardSearchRequest request = BoardSearchRequest.builder()
                .title(title)
                .author(author)
                .content(content)
                .build();
        return boardService.searchBoards(request);
    }

    // 게시판 생성 (JOOQ 사용)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoardDto createBoard(@RequestBody BoardCreateRequest request) {
        return boardService.createBoard(request);
    }

    // 게시판 수정 (JOOQ 사용)
    @PutMapping("/{id}")
    public BoardDto updateBoard(
            @PathVariable Long id,
            @RequestBody BoardUpdateRequest request) {
        BoardDto updatedBoard = boardService.updateBoard(id, request);
        if (updatedBoard == null) {
            throw new RuntimeException("게시판을 찾을 수 없습니다.");
        }
        return updatedBoard;
    }

    // 게시판 삭제 (JOOQ 사용)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBoard(@PathVariable Long id) {
        boolean deleted = boardService.deleteBoard(id);
        if (!deleted) {
            throw new RuntimeException("게시판을 찾을 수 없습니다.");
        }
    }
}

