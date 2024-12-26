package com.example.mongodb.config;

import com.example.mongodb.model.alarm.UserAlarmReadConverter;
import com.example.mongodb.model.notification.NotificationReadConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
@Configuration
public class MongoClientConfig extends AbstractMongoClientConfiguration {

    @Value("${mongodb.host}")
    private String host;
    @Value("${mongodb.username}")
    private String username;
    @Value("${mongodb.password}")
    private String password;
    @Value("${mongodb.database}")
    private String databaseName;

    @Bean
    public MongoClient mongoClient() {
        String connection = "mongodb://" + username + ":" + password + "@" + host + ":27017/" + databaseName + "?replicaSet=rs0";
        return MongoClients.create(new ConnectionString(connection)); // MongoDB 연결 URI
    }

    /**
     * Replica Set 에서 동작할 MongoTransactionManager Bean 생성
     */
    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Bean
    public TransactionTemplate transactionTemplate(MongoTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    public MongoCustomConversions customConversions() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(
                new ParameterNamesModule(), // 기본생성자 없어도 직렬화
                new JavaTimeModule() // JSR310 모듈 등록
        );
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new MongoCustomConversions(List.of(
                new NotificationReadConverter(objectMapper),
                new UserAlarmReadConverter(objectMapper)
        ));
    }

    /**
     * class 필드 제거를 위해 AbstractMongoClientConfiguration 오버라이딩
     * AbstractMongoClientConfiguration 에서 아래 Bean 등록해줌
     * - MongoClient
     * - MongoTemplate
     */
    @Override
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory databaseFactory,
                                                       MongoCustomConversions customConversions,
                                                       MongoMappingContext mappingContext) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(databaseFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mappingContext);
        converter.setCustomConversions(customConversions);
        converter.setCodecRegistryProvider(databaseFactory);
        // _class 필드 제거
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    /**
     * @Index 어노테이션
     */
    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
