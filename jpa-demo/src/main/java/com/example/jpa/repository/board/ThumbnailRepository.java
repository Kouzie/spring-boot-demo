package com.example.jpa.repository.board;

import com.example.jpa.model.baord.Board;
import com.example.jpa.model.baord.Thumbnail;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ThumbnailRepository extends CrudRepository<Thumbnail, Long> {
    List<Thumbnail> findAll();
    Thumbnail findByBoard(Board board);
}
