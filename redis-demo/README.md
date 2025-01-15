## 분산락 테스트


```shell
for i in {1..100}; do curl -s http://localhost:8080/distribute-lock/test & done; wait
for i in {1..100}; do curl -s http://localhost:8080/distribute-lock/test/aop & done; wait
for i in {1..100}; do curl -s http://localhost:8080/distribute-lock/test/util & done; wait
```

```shell
curl -s http://localhost:8080/distribute-lock/count
```

## Redis 객체 변환 테스트

```shell
curl -X POST http://localhost:8080/redis-template/normal \
-H "Content-Type: application/json" \
-d '{
  "nickname": "johnny",
  "username": "john_doe",
  "email": "john@example.com",
  "age": 30,
  "desc": "Software Developer"
}'
```

```shell
curl -X GET "http://localhost:8080/redis-template/normal/john_doe"
```