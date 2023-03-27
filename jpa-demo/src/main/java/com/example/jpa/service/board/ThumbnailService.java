package com.example.jpa.service.board;

import com.example.jpa.model.baord.Board;
import com.example.jpa.model.baord.Thumbnail;
import com.example.jpa.repository.board.ThumbnailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ThumbnailService {
    private final ThumbnailRepository repository;

    @Transactional(readOnly = true)
    public Thumbnail findByBoardId(Board board) {
        return repository.findByBoard(board);
    }

    @Transactional
    public Thumbnail save(Thumbnail thumbnail) {
        return repository.save(thumbnail);
    }
}
