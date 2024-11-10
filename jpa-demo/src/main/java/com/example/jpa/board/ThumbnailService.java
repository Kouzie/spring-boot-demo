package com.example.jpa.board;

import com.example.jpa.board.model.Board;
import com.example.jpa.board.model.Thumbnail;
import com.example.jpa.board.model.ThumbnailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(readOnly = true)
    public Thumbnail findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public List<Thumbnail> findAll() {
        return repository.findAll();
    }
}
