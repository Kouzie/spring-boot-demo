package com.example.jooq.repository;

import com.example.jooq.entity.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 단순 조회용 Comment Repository 인터페이스
 * Spring Data JDBC의 메서드 이름 기반 쿼리 사용
 */
@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByBoardId(Long boardId);
    List<Comment> findByBoardIdIn(List<Long> boardIds);
}

