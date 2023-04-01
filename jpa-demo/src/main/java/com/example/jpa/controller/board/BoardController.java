package com.example.jpa.controller.board;

import com.example.jpa.model.baord.Board;
import com.example.jpa.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public List<Board> getAll() {
        return boardService.findAll();
    }

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

    @PatchMapping("/{id}")
    public Board updateBoard(@PathVariable Long id) {
        Board board = boardService.updateBoard(id);
        return board;
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
