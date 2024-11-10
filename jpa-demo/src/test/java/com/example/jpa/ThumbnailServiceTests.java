package com.example.jpa;


import com.example.jpa.board.model.Board;
import com.example.jpa.board.model.Thumbnail;
import com.example.jpa.board.BoardService;
import com.example.jpa.board.ThumbnailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ThumbnailServiceTests {
    @Autowired
    BoardService boardService;

    @Autowired
    ThumbnailService thumbnailService;

    @Autowired
    ObjectMapper mapper;

    @Test
    void test() throws JsonProcessingException {
        Board board = Board.random();
        board = boardService.save(board);
        System.out.println("-----------------------");
        Thumbnail result = thumbnailService.findByBoardId(board);
        System.out.println(mapper.writeValueAsString(result));
    }
}
