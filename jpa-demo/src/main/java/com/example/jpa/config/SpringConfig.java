package com.example.jpa.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

//    @Bean
//    public DataSource dataSource() {
//        HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/demo?&useUnicode=true&serverTimezone=Asia/Seoul");
//        dataSource.setUsername("root");
//        dataSource.setPassword("root");
//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        dataSource.setAutoCommit(false); // default true
//        dataSource.setPoolName("MyPool");
//        dataSource.getHikariConfigMXBean().setMinimumIdle(5); // 최소 유지할 커넥션 수
//        dataSource.getHikariConfigMXBean().setMaximumPoolSize(20); // 최대 커넥션 수
//        dataSource.getHikariConfigMXBean().setIdleTimeout(30000); // 커넥션이 유휴 상태로 유지되는 시간(ms)
//        dataSource.getHikariConfigMXBean().setConnectionTimeout(20000); // 커넥션 획득 대기 시간(ms)
//        dataSource.getHikariConfigMXBean().setMaxLifetime(1800000); // 커넥션 최대 수명(ms)
//        return dataSource;
//    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public Snowflake snowflake() {
        long datasourceId = 0;
        long workerId = 0;
        return new Snowflake(datasourceId, workerId);
    }
}
