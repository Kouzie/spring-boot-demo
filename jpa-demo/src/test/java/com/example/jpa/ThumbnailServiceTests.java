package com.example.jpa;


import com.example.jpa.model.baord.Board;
import com.example.jpa.model.baord.Thumbnail;
import com.example.jpa.service.board.BoardService;
import com.example.jpa.service.board.ThumbnailService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

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
