package com.example.securitydemo.common.service;

import com.example.securitydemo.common.model.Board;
import com.example.securitydemo.common.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository repository;

    @Transactional(readOnly = true)
    public Page<Board> findAll(Pageable page) {
        return repository.findAll(page);
    }

    @Transactional
    public Board save(Board board) {
        return repository.save(board);
    }

    @Transactional(readOnly = true)
    public Optional<Board> findById(Long bno) {
        return repository.findById(bno);
    }

    @Transactional
    public void deleteById(Long bno) {
        repository.deleteById(bno);
    }
}
