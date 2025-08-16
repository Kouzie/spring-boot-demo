package com.demo.cache.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.impl.config.persistence.DefaultPersistenceConfiguration;
import org.ehcache.jsr107.Eh107Configuration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean(name = "localCacheManager")
    public CacheManager localCacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        return cacheManager;
    }

    @Bean(name = "caffeineCacheManager")
    public CacheManager caffeineCacheManager() {
        Caffeine<Object, Object> defaultConfig = Caffeine.newBuilder()
                .maximumSize(100) // 캐시 최대 항목 수
                .expireAfterWrite(10, TimeUnit.MINUTES) // 쓰기 후 10분 뒤 만료
                .recordStats(); // 캐시 통계 기록 (선택 사항)
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("customerCache");
        cacheManager.setCaffeine(defaultConfig);
        return cacheManager;
    }

    @Primary
    @Bean(name = "ehCacheManager")
    public CacheManager ehCacheManager() {
        CachingProvider provider = Caching.getCachingProvider();
        EhcacheCachingProvider ehcacheProvider = (EhcacheCachingProvider) provider;
        DefaultConfiguration defaultConfiguration = new DefaultConfiguration(ehcacheProvider.getDefaultClassLoader(),
                new DefaultPersistenceConfiguration(new File("cache/directory")));
        javax.cache.CacheManager cacheManager = ehcacheProvider.getCacheManager(ehcacheProvider.getDefaultURI(), defaultConfiguration);

        CacheConfiguration<Object, Object> configuration = CacheConfigurationBuilder.newCacheConfigurationBuilder(
                        Object.class, // key type
                        Object.class, // value type
                        ResourcePoolsBuilder.newResourcePoolsBuilder()
                                .heap(1000, EntryUnit.ENTRIES)
                                .offheap(10, MemoryUnit.MB)
                                .disk(1, MemoryUnit.GB)
                                .build())
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(60)))
                .withDefaultDiskStoreThreadPool()
                .build();
        cacheManager.createCache("customerCache",
                Eh107Configuration.fromEhcacheCacheConfiguration(configuration));
        return new JCacheCacheManager(cacheManager);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory("localhost", 6379);
        connectionFactory.start();
        return connectionFactory;
    }

    @Bean(name = "redisCacheManager")
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(RedisSerializer.json()))
                .entryTtl(Duration.ofMinutes(3L));
        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }
}
