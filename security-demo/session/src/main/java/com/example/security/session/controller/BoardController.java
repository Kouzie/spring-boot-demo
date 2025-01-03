package com.example.security.session.controller;

import com.example.security.common.model.Board;
import com.example.security.common.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    public void list(Authentication authentication, Model model) {
        log.info(authentication.toString()); // UsernamePasswordAuthenticationToken
        Pageable page = PageRequest.of(0, 1000, Sort.by("bno"));
        Page<Board> result = boardService.findAll(page);
        model.addAttribute("result", result);
    }

    @GetMapping("/random")
    public String registerPOST(RedirectAttributes rttr) {
        Board board = Board.random();
        board = boardService.save(board);
        rttr.addFlashAttribute("msg", "success");
        return "redirect:/boards/list";
    }
}
