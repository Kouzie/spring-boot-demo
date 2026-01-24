## preview

> <https://www.baeldung.com/nats-java-client>  
> <https://docs.nats.io/>  
> <https://github.com/nats-io/nats.java>

NATS 메시징 시스템 데모 프로젝트

### 모듈 구조

- **common-config**: 공통 설정 모듈
  - NATS Connection 설정
  - NatsComponent (Core NATS publish/subscribe)
  - 공통 application.yml

- **core-demo**: Core NATS 전용 모듈
  - Core NATS pub/sub 기능
  - `sync`, `async` 프로파일 지원
  - Request-Reply 패턴 지원

- **jetstream-demo**: JetStream 전용 모듈
  - JetStream publish (메시지 지속성)
  - JetStream consumer (메시지 수신)
  - 스케줄러는 Core NATS 사용 (유실 가능)  

## install

```shell
# install nats cli
$ brew install nats-io/nats-tools/nats
```

## Docker Compose로 실행

```shell
$ cd docker
$ docker-compose up -d
```


`default.conf` 에서 각종 `nats` 관련 설정 가능  
> <https://docs.nats.io/running-a-nats-service/configuration>

## publish message

메세지 `sub` `pub` 테스트

```shell
$ nats sub test --server=nats://localhost:4222 --user=admin --password=password

$ curl --location 'http://localhost:8080/nats/publish' \
--header 'Content-Type: application/json' \
--data '{
    "subject": "default.subject",
    "message": "hello world"
}'
````

메세지 `reply` `pub` 테스트

```shell
$ nats reply test --server=nats://localhost:4222 --user=admin --password=password "OK RECEIVED"

$ nats sub reply.test --server=nats://localhost:4222 --user=admin --password=password

$ curl --location 'http://localhost:8080/nats/publish' \
--header 'Content-Type: application/json' \
--data '{
    "subject": "default.subject",
    "message": "hello world",
    "replyTo": "reply.test"
}'
````

## subscribe subject

```shell
curl -X POST http://localhost:8080/nats/subscribe \
-H "Content-Type: application/json" \
-d '{"subject": "demo.subject"}'

curl -X DELETE http://localhost:8080/nats/unsubscribe \
-H "Content-Type: application/json" \
-d '{"subject": "demo.subject"}'
```

## JetStream과 Core NATS 메시지 교환

### 현재 설정

**Stream Configuration**
- Stream 이름: `default-stream`
- 포함된 subjects: `jetstream.>`, `test.>`, `default.>`

**Consumer 설정**
- **Core NATS**: `default.subject` 구독 (NatsAsyncConsumer)
- **JetStream**: `jetstream.>` 구독 (JetStreamConsumer)

### 메시지 교환 시나리오

#### 1. JetStream으로 발행 → Core NATS로 수신

**케이스 A: Stream에 포함된 subject**
```
JetStream.publish("test.subject", "message")
→ Stream에 저장됨 ✅
→ Core NATS subscriber도 받을 수 있음 ✅ (Stream이 test.>를 포함)
```

**케이스 B: Stream에 포함되지 않은 subject**
```
JetStream.publish("other.subject", "message")
→ Stream에 저장되지 않음 ❌ (에러 발생 가능)
→ Core NATS subscriber도 받지 못함 ❌
```

#### 2. Core NATS로 발행 → JetStream으로 수신

**케이스 A: Stream에 포함된 subject**
```
Core NATS.publish("test.subject", "message")
→ Stream에 저장되지 않음 ❌ (Core NATS는 Stream에 저장 안함)
→ JetStream consumer는 받을 수 없음 ❌
→ Core NATS subscriber만 받을 수 있음 ✅
```

**케이스 B: Stream에 포함되지 않은 subject**
```
Core NATS.publish("other.subject", "message")
→ Stream에 저장되지 않음 ❌
→ JetStream consumer는 받을 수 없음 ❌
→ Core NATS subscriber만 받을 수 있음 ✅
```

### 중요 사항

1. **JetStream으로 발행된 메시지**:
   - Stream의 subject 패턴에 포함되어야 저장됨
   - 저장되면 JetStream consumer가 받을 수 있음
   - 동시에 Core NATS subscriber도 받을 수 있음 (같은 subject라면)

2. **Core NATS로 발행된 메시지**:
   - Stream에 저장되지 않음
   - Core NATS subscriber만 받을 수 있음
   - JetStream consumer는 받을 수 없음

3. **메시지 지속성**:
   - JetStream으로 발행 → Stream에 저장 → 재시도/지속성 보장 ✅
   - Core NATS로 발행 → 저장 안됨 → 재시도/지속성 없음 ❌

### 권장 사항

- **유실되면 안 되는 메시지**: JetStream 사용
- **유실되어도 되는 메시지**: Core NATS 사용
- **혼용하지 말 것**: 같은 목적의 메시지는 동일한 방식 사용