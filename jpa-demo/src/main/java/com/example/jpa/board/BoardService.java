package com.example.jpa.board;

import com.example.jpa.board.model.Board;
import com.example.jpa.board.model.BoardRepository;
import com.example.jpa.board.model.QBoard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository repository;
    private final JPAQueryFactory queryFactory;

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Board save(Board board) {
        log.info("save invoked");
        return repository.save(board);
    }

    @Transactional
    public Board findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found board, id:" + id));
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public Board updateBoard(Long id) {
        Board board = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("can not found id:" + id));
        board.update();
        return repository.save(board);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Board> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Board> search(String title, String writer, String content, 
                              LocalDateTime regdateFrom, LocalDateTime regdateTo) {
        BooleanBuilder builder = new BooleanBuilder();

        // 제목 검색 (LIKE 검색)
        if (title != null && !title.isEmpty()) {
            builder.and(QBoard.board.title.containsIgnoreCase(title));
        }

        // 작성자 검색 (LIKE 검색)
        if (writer != null && !writer.isEmpty()) {
            builder.and(QBoard.board.writer.containsIgnoreCase(writer));
        }

        // 내용 검색 (LIKE 검색)
        if (content != null && !content.isEmpty()) {
            builder.and(QBoard.board.content.containsIgnoreCase(content));
        }

        // 등록일 시작 범위
        if (regdateFrom != null) {
            builder.and(QBoard.board.regdate.goe(regdateFrom));
        }

        // 등록일 종료 범위
        if (regdateTo != null) {
            builder.and(QBoard.board.regdate.loe(regdateTo));
        }

        return queryFactory
                .selectFrom(QBoard.board)
                .where(builder)
                .orderBy(QBoard.board.regdate.desc())
                .fetch();
    }
}
