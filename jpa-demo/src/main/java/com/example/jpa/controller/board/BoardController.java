package com.example.jpa.controller.board;

import com.example.jpa.model.baord.Board;
import com.example.jpa.repository.board.BoardRepository;
import com.example.jpa.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards")
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

    @DeleteMapping("/{id}")
    public void removeBoardById(@PathVariable Long id) {
        boardService.deleteById(id);
    }

    @PatchMapping("/test/orphan/{id}")
    public Board testOrphan(@PathVariable Long id) {
        Board board = boardService.findById(id);
        board.testOrphan();
        board = boardService.save(board);
        return board;
    }
}
