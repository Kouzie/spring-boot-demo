package com.example.jpa.service.board;

import com.example.jpa.model.baord.Board;
import com.example.jpa.model.baord.Thumbnail;
import com.example.jpa.repository.board.BoardRepository;
import com.example.jpa.repository.board.ThumbnailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository repository;

    @Transactional
    public Board save(Board board) {
        return repository.save(board);
    }

    @Transactional(readOnly = true)
    public Board findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found board, id:" + id));
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
