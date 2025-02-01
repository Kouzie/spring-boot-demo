package com.example.jpa.board;

import com.example.jpa.board.model.Board;
import com.example.jpa.board.model.BoardRepository;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository repository;

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Board save(Board board) {
        log.info("save invoked");
        return repository.save(board);
    }

    @Transactional
    public Board findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found board, id:" + id));
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public Board updateBoard(Long id) {
        Board board = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("can not found id:" + id));
        board.update();
        return repository.save(board);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Board> findAll() {
        return repository.findAll();
    }
}
