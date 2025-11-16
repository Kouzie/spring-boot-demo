## Kafka Demo

Apache Kafka와 Spring Kafka를 테스트하기 위한 데모 프로젝트입니다.

```bash
curl -X POST http://localhost:8081/api/messages \
  -H "Content-Type: application/json" \
  -d '{
    "topic": "test1",
    "message": "테스트 메시지",
    "sender": "test-user",
    "priority": 1
  }'

curl -X POST http://localhost:8081/api/messages \
  -H "Content-Type: application/json" \
  -d '{
    "topic": "test1",
    "key": "user-123",
    "message": "테스트 메시지 (with key)",
    "sender": "test-user",
    "priority": 2
  }'

curl -X POST http://localhost:8081/api/messages \
  -H "Content-Type: application/json" \
  -d '{
    "topic": "test2",
    "key": "user-456",
    "message": "test2 토픽 메시지",
    "sender": "test-user",
    "priority": 3
  }'
```

### 토픽 자동 생성

Kafka는 기본적으로 `auto.create.topics.enable=true` 설정이 활성화되어 있습니다.

**동작 방식:**
- 존재하지 않는 토픽에 메시지를 발행하면 **자동으로 토픽이 생성**됩니다
- 자동 생성된 토픽은 기본 설정으로 생성됩니다:
    - Partition 수: `num.partitions` (기본값: 1)
    - Replication Factor: `default.replication.factor` (기본값: 1)

**예시:**
```bash
# 존재하지 않는 토픽에 메시지 발행
curl -X POST http://localhost:8081/api/messages \
  -H "Content-Type: application/json" \
  -d '{
    "topic": "new-topic",
    "message": "새 토픽 자동 생성 테스트",
    "sender": "test-user",
    "priority": 1
  }'

# 결과: "new-topic" 토픽이 자동으로 생성되고 메시지가 발행됨
```
