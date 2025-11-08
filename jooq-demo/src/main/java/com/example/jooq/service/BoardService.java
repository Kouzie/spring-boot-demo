package com.example.jooq.service;

import com.example.jooq.dto.AuthorDto;
import com.example.jooq.dto.BoardAuthorDto;
import com.example.jooq.dto.BoardDto;
import com.example.jooq.dto.BoardWithAuthorDto;
import com.example.jooq.dto.req.BoardCreateRequest;
import com.example.jooq.dto.req.BoardSearchRequest;
import com.example.jooq.dto.req.BoardUpdateRequest;
import com.example.jooq.entity.Author;
import com.example.jooq.entity.Board;
import com.example.jooq.repository.BoardRepository;
import com.example.jooq.vo.BoardAuthorVo;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.example.jooq.generated.tables.records.BoardRecord;
import static com.example.jooq.generated.Tables.AUTHOR;
import static com.example.jooq.generated.Tables.BOARD;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;  // Spring Data JDBC 인터페이스
    private final DSLContext dsl;  // JOOQ DSL (다이나믹 쿼리용)

    // 게시판 + 작성자 조회 (DTO 사용)
    public List<BoardWithAuthorDto> findAllWithAuthor() {
        return boardRepository.findAllWithAuthor();
    }

    // 게시판 ID로 조회: 게시판 + 작성자 (JOOQ JOIN 사용)
    public BoardAuthorDto findByIdWithAuthor(Long boardId) {
        Record record = dsl.select()
                .from(BOARD)
                .join(AUTHOR).on(BOARD.AUTHOR_ID.eq(AUTHOR.ID))
                .where(BOARD.ID.eq(boardId))
                .fetchOne();

        if (record == null) return null;

        Board board = record.into(BOARD).into(Board.class);
        Author author = record.into(AUTHOR).into(Author.class);
        BoardAuthorVo vo = new BoardAuthorVo(board, author, null);

        return convertToDto(vo);
    }

    // 게시판 검색 (다이나믹 파라미터): 게시판 + 작성자 (JOOQ 사용)
    public List<BoardAuthorDto> searchBoards(BoardSearchRequest request) {
        Condition condition = null;

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            condition = BOARD.TITLE.like("%" + request.getTitle() + "%");
        }

        if (request.getContent() != null && !request.getContent().isEmpty()) {
            condition = condition == null
                    ? BOARD.CONTENT.like("%" + request.getContent() + "%")
                    : condition.and(BOARD.CONTENT.like("%" + request.getContent() + "%"));
        }

        SelectJoinStep<Record> selectStep = dsl.select()
                .from(BOARD)
                .join(AUTHOR).on(BOARD.AUTHOR_ID.eq(AUTHOR.ID));

        if (condition != null) {
            return selectStep.where(condition)
                    .orderBy(BOARD.ID.desc())
                    .fetch()
                    .stream()
                    .map(record -> {
                        Board board = record.into(BOARD).into(Board.class);
                        Author author = record.into(AUTHOR).into(Author.class);
                        BoardAuthorVo vo = new BoardAuthorVo(board, author, null);
                        return convertToDto(vo);
                    })
                    .collect(Collectors.toList());
        } else {
            return selectStep.orderBy(BOARD.ID.desc())
                    .fetch()
                    .stream()
                    .map(record -> {
                        Board board = record.into(BOARD).into(Board.class);
                        Author author = record.into(AUTHOR).into(Author.class);
                        BoardAuthorVo vo = new BoardAuthorVo(board, author, null);
                        return convertToDto(vo);
                    })
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public boolean deleteBoard(Long boardId) {
        int deletedRows = dsl.deleteFrom(BOARD)
                .where(BOARD.ID.eq(boardId))
                .execute();
        return deletedRows > 0;
    }

    @Transactional
    public BoardDto createBoard(BoardCreateRequest request) {
        Instant now = Instant.now();

        // INSERT 실행 후 생성된 ID 반환 (returning() 사용)
        // 주의: MySQL/H2는 RETURNING 절을 지원하지 않을 수 있음
        Record1<Long> boardRecord = dsl.insertInto(BOARD)
                .set(BOARD.TITLE, request.getTitle())
                .set(BOARD.CONTENT, request.getContent())
                .set(BOARD.AUTHOR_ID, request.getAuthorId())
                .set(BOARD.CREATED_AT, now)
                .set(BOARD.UPDATED_AT, now)
                .set(BOARD.VIEW_COUNT, 0)
                .returningResult(BOARD.ID) // 자동으로 MySQL/H2 fallback: LAST_INSERT_ID() 사용
                .fetchOne();

        // returning() 이 지원되지 않는 경우 명시적 지정
        // Long id = dsl.select(DSL.function("LAST_INSERT_ID", Long.class))
        //         .fetchOne()
        //         .value1();

        Long boardId = boardRecord.getValue(BOARD.ID);
        Board board = findById(boardId);
        return convertToDto(board);
    }

    // 게시판 수정 (JOOQ 사용)
    @Transactional
    public BoardDto updateBoard(Long boardId, BoardUpdateRequest request) {
        int updatedRows = dsl.update(BOARD)
                .set(BOARD.TITLE, request.getTitle())
                .set(BOARD.CONTENT, request.getContent())
                .set(BOARD.UPDATED_AT, Instant.now())
                .where(BOARD.ID.eq(boardId))
                .execute();

        if (updatedRows == 0) {
            return null; // 게시판이 존재하지 않음
        }

        Board board = findById(boardId);
        return convertToDto(board);
    }

    // JOOQ로 단일 게시판 조회 (내부 메서드)
    private Board findById(Long boardId) {
        BoardRecord record = dsl.selectFrom(BOARD)
                .where(BOARD.ID.eq(boardId))
                .fetchOne();
        return record != null ? record.into(Board.class) : null;
    }

    // BoardAuthorVo를 BoardAuthorDto로 변환
    private BoardAuthorDto convertToDto(BoardAuthorVo vo) {
        if (vo == null) return null;

        BoardDto boardDto = convertToDto(vo.getBoard());
        AuthorDto authorDto = vo.getAuthor() != null ? convertAuthorToDto(vo.getAuthor()) : null;

        return new BoardAuthorDto(boardDto, authorDto);
    }

    // 엔티티를 DTO로 변환
    private BoardDto convertToDto(Board board) {
        BoardDto dto = new BoardDto();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setContent(board.getContent());
        dto.setAuthorId(board.getAuthorId());
        dto.setCreatedAt(board.getCreatedAt());
        dto.setUpdatedAt(board.getUpdatedAt());
        dto.setViewCount(board.getViewCount());
        return dto;
    }

    private AuthorDto convertAuthorToDto(Author author) {
        AuthorDto dto = new AuthorDto();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setEmail(author.getEmail());
        dto.setCreatedAt(author.getCreatedAt());
        dto.setUpdatedAt(author.getUpdatedAt());
        return dto;
    }
}
