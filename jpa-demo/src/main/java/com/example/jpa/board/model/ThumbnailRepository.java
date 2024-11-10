package com.example.jpa.board.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ThumbnailRepository extends CrudRepository<Thumbnail, Long> {
    List<Thumbnail> findAll();
    Thumbnail findByBoard(Board board);
}
