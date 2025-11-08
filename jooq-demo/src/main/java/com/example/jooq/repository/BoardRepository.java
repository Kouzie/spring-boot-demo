package com.example.jooq.repository;

import com.example.jooq.entity.Board;
import com.example.jooq.dto.BoardWithAuthorDto;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends CrudRepository<Board, Long> {
    // 생성자 기반 자동 매핑 시도 (컬럼 순서와 생성자 매개변수 순서 일치 필요)
    @Query("""
            SELECT b.id, b.title, b.content, b.author_id, b.created_at, b.updated_at, b.view_count,
                   a.id AS author_id_value, a.name AS author_name,
                   a.email AS author_email, a.created_at AS author_created_at,
                   a.updated_at AS author_updated_at
            FROM board b
            INNER JOIN author a ON b.author_id = a.id
            ORDER BY b.id DESC
            """)
    List<BoardWithAuthorDto> findAllWithAuthor();
}
