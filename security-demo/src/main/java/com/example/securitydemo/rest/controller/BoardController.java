package com.example.securitydemo.rest.controller;

import com.example.securitydemo.common.model.Board;
import com.example.securitydemo.common.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Profile("rest")
@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    public List<Board> list() {
        return boardService.findAll(PageRequest.of(0, 1000)).getContent();
    }

    @GetMapping("/random")
    public Board registerPOST() {
        Board board = Board.random();
        board = boardService.save(board);
        return board;
    }
}
