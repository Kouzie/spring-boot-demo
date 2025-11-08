-- 사용자(Author) 테이블 생성
CREATE TABLE IF NOT EXISTS author (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 게시판 테이블 생성 (author_id 추가)
CREATE TABLE IF NOT EXISTS board (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    view_count INT DEFAULT 0,
    FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE
);

-- 댓글 테이블 생성
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    author VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES board(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_board_id ON comment(board_id);
CREATE INDEX IF NOT EXISTS idx_board_created_at ON board(created_at);
CREATE INDEX IF NOT EXISTS idx_board_author_id ON board(author_id);
CREATE INDEX IF NOT EXISTS idx_author_email ON author(email);

-- 테스트 데이터 삽입
INSERT INTO author (name, email) VALUES
('홍길동', 'hong@example.com'),
('김철수', 'kim@example.com'),
('이영희', 'lee@example.com');

INSERT INTO board (title, content, author_id, view_count) VALUES
('첫 번째 게시글', '첫 번째 게시글 내용입니다.', 1, 10),
('두 번째 게시글', '두 번째 게시글 내용입니다.', 2, 5),
('세 번째 게시글', '세 번째 게시글 내용입니다.', 1, 20),
('네 번째 게시글', '네 번째 게시글 내용입니다.', 3, 15),
('다섯 번째 게시글', '다섯 번째 게시글 내용입니다.', 2, 8);

INSERT INTO comment (board_id, content, author) VALUES
(1, '첫 번째 게시글의 첫 번째 댓글', 'commenter1'),
(1, '첫 번째 게시글의 두 번째 댓글', 'commenter2'),
(2, '두 번째 게시글의 첫 번째 댓글', 'commenter1'),
(3, '세 번째 게시글의 첫 번째 댓글', 'commenter3'),
(3, '세 번째 게시글의 두 번째 댓글', 'commenter2'),
(3, '세 번째 게시글의 세 번째 댓글', 'commenter1');

