## preview

JPA 관련 클래스, 어노테이션 데모

### Board API

```shell
# 전체 조회
curl -XGET 'http://localhost:8080/boards'

# 단건 조회
curl -XGET 'http://localhost:8080/boards/1'

# 랜덤 게시글 생성
curl -XPOST 'http://localhost:8080/boards/random'

# QueryDSL 동적 검색 (제목)
curl -XGET 'http://localhost:8080/boards/search?title=test'

# QueryDSL 동적 검색 (작성자)
curl -XGET 'http://localhost:8080/boards/search?writer=john'

# QueryDSL 동적 검색 (여러 조건)
curl -XGET 'http://localhost:8080/boards/search?title=test&writer=john&content=example'

# QueryDSL 동적 검색 (날짜 범위)
curl -XGET 'http://localhost:8080/boards/search?regdateFrom=2024-01-01T00:00:00&regdateTo=2024-12-31T23:59:59'

# Thumbnail 조회
curl -XGET 'http://localhost:8080/boards/thumbnail'
curl -XGET 'http://localhost:8080/boards/thumbnail/1'
curl -XPOST 'http://localhost:8080/boards/thumbnail' \
--header 'Content-Type: application/json' \
--data '{ "boardId": 1 }' 
```

### Book API

```shell
# 전체 조회
curl -XGET 'http://localhost:8080/books'

# Criteria API 동적 검색 (작성자 이름)
curl -XGET 'http://localhost:8080/books/search?authorName=Jennie'

# Criteria API 동적 검색 (여러 조건)
curl -XGET 'http://localhost:8080/books/search?authorName=Jennie&title=Repudia'

# Criteria API 동적 검색 (날짜 범위)
curl -XGET 'http://localhost:8080/books/search?createdFrom=2024-01-01&createdTo=2024-12-31'
```

## 분산락 테스트

```shell
for i in {1..100}; do curl -s http://localhost:8080/distribute-lock/test & done; wait
for i in {1..100}; do curl -s http://localhost:8080/distribute-lock/test/without/lock & done; wait
```
