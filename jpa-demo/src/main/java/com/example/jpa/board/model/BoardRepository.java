package com.example.jpa.board.model;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends CrudRepository<Board, Long> {

    List<Board> findAll();

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Override
    Optional<Board> findById(Long id);
}
