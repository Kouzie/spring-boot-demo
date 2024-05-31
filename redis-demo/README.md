## preview

Redis 데모

## install

```shell
docker run -d --name demo-redis \
-p 6379:6379 \
redis:7.0.10-alpine3.17 
```

## cache 사용

### @Cacheable

redis에 캐싱된 데이터가 있으면 데이터 반환, 데이터가 없으면 DB에서 조회 후 redis에 캐싱.
런타임에 수행된다.

    @Cacheable(value = "testCache", key = "#id", unless = "#result.count < 100")

    value : 캐시 이름
    key : 캐시 키값
    condition : 캐시 저장 조건문
    unless : 캐시 저장 조건문

condition은 캐시 저장 조건문으로 명시된 해당 조건을 만족할때 캐시에 저장한다.
unless는 해당 조건을 만족할때 캐시에 저장하지 않는다.

### @CachePut

redis에 저장된 캐시 값을 수정한다.

    @CachePut(value = "testCache", key="#id")

    value : 캐시 이름
    key: 캐시 키값

Cacheable과 다르게 무조건 캐시에 저장하는 어노테이션. 이미 있는 캐시 데이터를 수정 가능하다.

### @CacheEvict

redis에 저장된 캐시 정보를 삭제한다.

    @CacheEvict(value = "testCache", key="#id")

    value : 캐시 이름
    key : 캐시 키값
    allEntries : 캐시 전체 삭제 여부

키값을 정의한 경우 해당 키값으로 저장된 캐시만 삭제되며, 정의되지 않은 경우 키값이 없이 저장된 캐시가 삭제된다. allEntries=true를 추가할 경우, 키값과 상관없이 testCache의 모든 캐시 데이터가 삭제된다.

---

## spring boot redis 적용

### gradle dependencies 추가

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
```
redis client인 `Lettuce`가 자동으로 추가된다.

### redis configuration 추가

캐시 사용의 활성화를 위해 configuration 파일에 `@EnableCaching` 어노테이션을 추가.

> config
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

  @Bean
  public CacheManager userCacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfigurationdefaultCacheConfig()
      .serializeKeysWith(RedisSerializationContext.SerializationPairfromSerializer(new StringRedisSerializer()))
      .serializeValuesWith(RedisSerializationContext.SerializationPairfromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())))
      .entryTtl(Duration.ofMinutes(3L));
    return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactor(connectionFactory).cacheDefaults(redisCacheConfiguration).build();
  }

  ...
}
```
Spring CacheManager타입의 userCacheManager를 빈으로 등록해주면 Spring에서는 캐싱할 때 로컬 캐시 대신 redis에 저장하게 됨.

entryTtl을 이용하여 캐시의 expire time을 지정할 수 있다.


> CustomerContractService

```java
//캐시 사용
@Transactional
@Cacheable(value = "customerCache", cacheManager = "userCacheManager")
public List<CustomerContract> findAllWithCache() throws InterruptedException {
    Thread.sleep(1500); //db 접근 여부 차이를 확인하기 위함
    return repository.findAll();
}

//캐시 사용 X
@Transactional
public List<CustomerContract> findAllWithoutCache() throwsInterruptedException {
    Thread.sleep(1500);
    return repository.findAll();
}

//캐시 키값 사용
@Transactional
@Cacheable(value = "customerCache", key="#contractCd")
public Optional<CustomerContract> findByContractWithCache(String contractCd) {
    return repository.findByContractCd(contractCd);
}

//캐시 삭제
@Transactional
@CacheEvict(value = "customerCache")
public void refresh() {
    log.info("cache clear");
}

//캐시 삭제 - 키값 사용
@Transactional
@CacheEvict(value = "customerCache", key="#contractCd")
public void refreshWithContract(String contractCd) {
    log.info("cache clear");
}
```
반복실행시 캐시를 사용하는 요청에서는 `Thread.sleep(1500)`이 처음 한 번만 실행. 캐시를 사용하지 않을 경우 db의 접근이 일어나기 때문에 `Thread.sleep(1500)`가 모든 요청마다 실행된다.


---

### Serialize
redis에서 객체는 내부적으로 직렬화 되어 저장되기 때문에 Entity에 implements Serializable을 추가해 주어야 한다.

```java
@Entity
public class CustomerContract implements Serializable {
  ...
}
```

---

### key 값 생성
@Cacheable(value="testCache", key="#id")
- key 값이 있는 경우
```
testCache::id값
```

@Cacheable(value="testCache")
- key 값이 없는 경우
```
testCache::SimpleKey []
```
@Cacheable(value="testCache")
- key 값은 없지만 parameter가 있는 경우
```
testCache::파라미터값
```
- parameter가 여러개일 경우
```
testCache::param1,param2... 형식으로 생성
```
---
### 기타 사항

- 같은 key 값에 다른 형태의 value 값이 들어가는 경우 `com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot deserialize value of type` ERROR 발생

## 분산락 테스트


```shell
for i in {1..100}; do curl -s http://localhost:8080/distribute-lock/test & done; wait
for i in {1..100}; do curl -s http://localhost:8080/distribute-lock/test/without/lock & done; wait
```