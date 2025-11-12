package com.example.jpa.board;

import com.example.jpa.board.model.Board;
import com.example.jpa.board.model.Thumbnail;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final ThumbnailService thumbnailService;

    @GetMapping("/thumbnail")
    public List<Thumbnail> getAllBoardThumbnail() {
        List<Thumbnail> thumbnail = thumbnailService.findAll();
        return thumbnail;
    }

    @GetMapping("/thumbnail/{thumbnailId}")
    public Thumbnail getBoardThumbnailById(@PathVariable Long thumbnailId) {
        Thumbnail thumbnail = thumbnailService.findById(thumbnailId);
        return thumbnail;
    }

    @PostMapping("/thumbnail")
    public Thumbnail addBoardThumbnail(@Valid @RequestBody AddThumbnailRequestDto requestDto) {
        Board board = boardService.findById(requestDto.getBoardId());
        Thumbnail thumbnail = new Thumbnail("https://picsum.photos/200/300", board);
        return thumbnailService.save(thumbnail);
    }

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

    @GetMapping("/search")
    public List<Board> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String writer,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime regdateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime regdateTo) {
        return boardService.search(title, writer, content, regdateFrom, regdateTo);
    }
}
