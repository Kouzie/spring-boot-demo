## preview

Redis 데모

## install

```shell
docker run -d --name demo-redis \
-p 6379:6379 \
redis:7.0.10-alpine3.17 
```

## spring boot redis 적용

### gradle dependencies 추가

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
```
redis client인 `Lettuce`가 자동으로 추가된다.

### redis configuration 추가

```java
@EnableCaching //캐시 사용 활성화
@Configuration
public class RedisConfig {
  @Value("${spring.redis.host}")
  private String host;

  @Value("${spring.redis.port}")
  private int port;

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(host, port);
  }
}
```

### Serialize
redis에서 객체는 내부적으로 직렬화 되어 저장되기 때문에 Entity에 implements Serializable을 추가해 주어야 한다.

```java
@Entity
public class CustomerContract implements Serializable {
  ...
}
```

## 분산락 테스트


```shell
for i in {1..100}; do curl -s http://localhost:8080/distribute-lock/test & done; wait
for i in {1..100}; do curl -s http://localhost:8080/distribute-lock/test/aop & done; wait
for i in {1..100}; do curl -s http://localhost:8080/distribute-lock/test/util & done; wait
```

```shell
curl -s http://localhost:8080/distribute-lock/count
```