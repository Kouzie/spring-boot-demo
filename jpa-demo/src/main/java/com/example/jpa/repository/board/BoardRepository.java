package com.example.jpa.repository.board;

import com.example.jpa.model.baord.Board;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends CrudRepository<Board, Long> {

    List<Board> findAll();

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Override
    Optional<Board> findById(Long id);
}
