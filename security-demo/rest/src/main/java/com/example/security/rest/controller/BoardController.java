package com.example.security.rest.controller;

import com.example.security.common.config.CustomSecurityUser;
import com.example.security.common.model.Board;
import com.example.security.common.service.BoardService;
import com.example.security.rest.config.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    public List<Board> list(@LoginUser CustomSecurityUser user) {
        log.info(user.toString());
        return boardService.findAll(PageRequest.of(0, 1000)).getContent();
    }

    @GetMapping("/random")
    public Board registerPOST() {
        Board board = Board.random();
        board = boardService.save(board);
        return board;
    }
}
