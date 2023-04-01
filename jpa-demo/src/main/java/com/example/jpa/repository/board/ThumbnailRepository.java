package com.example.jpa.repository.board;

import com.example.jpa.model.baord.Board;
import com.example.jpa.model.baord.Thumbnail;
import org.springframework.data.repository.CrudRepository;

public interface ThumbnailRepository extends CrudRepository<Thumbnail, Long> {
    Thumbnail findByBoard(Board board);
}
