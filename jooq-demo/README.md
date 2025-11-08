## JOOQ Demo

### 엔드포인트

#### 1. 게시판 + 작성자 조회 (Spring Data JDBC @Query JOIN)
```shell
curl -X GET 'http://localhost:8080/api/boards' | jq
```

#### 2. 게시판 ID로 조회 (게시판 + 작성자 + 댓글) - JOOQ JOIN
```shell
curl -X GET 'http://localhost:8080/api/boards/1' | jq
curl -X GET 'http://localhost:8080/api/boards/2' | jq
curl -X GET 'http://localhost:8080/api/boards/3' | jq
```

#### 3. 게시판 검색 (다이나믹 쿼리) - JOOQ
```shell
# 제목으로 검색
curl -G 'http://localhost:8080/api/boards/search' --data-urlencode 'title=첫' | jq

# 내용으로 검색
curl -G 'http://localhost:8080/api/boards/search' --data-urlencode 'content=게시글' | jq

# 제목 + 내용으로 검색
curl -G 'http://localhost:8080/api/boards/search' --data-urlencode 'title=첫' --data-urlencode 'content=게시글' | jq
```

