package com.example.jpa.controller.board;

import com.example.jpa.model.baord.Board;
import com.example.jpa.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/{id}")
    public Board getBoardById(@PathVariable Long id) {
        Board board = boardService.findById(id);
        return board;
    }

    @PostMapping("/random")
    public Board addRandomBoard() {
        Board board = Board.random();
        return boardService.save(board);
    }
}
