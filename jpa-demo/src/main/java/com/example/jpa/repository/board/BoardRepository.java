package com.example.jpa.repository.board;

import com.example.jpa.model.baord.Board;
import org.springframework.data.repository.CrudRepository;

public interface BoardRepository extends CrudRepository<Board, Long> {
}
