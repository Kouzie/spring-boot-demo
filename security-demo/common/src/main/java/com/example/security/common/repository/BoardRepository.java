package com.example.security.common.repository;

import com.example.security.common.model.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface BoardRepository extends CrudRepository<Board, Long> {
    Page<Board> findAll(Pageable page);
}
