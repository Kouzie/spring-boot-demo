package com.example.sharding.routing.demo.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DbConfig {
    // LazyConnectionDataSourceProxy
    @Bean
    @Primary
    public DataSource lazyDataSource(DataSource routingDataSource) {
        LazyConnectionDataSourceProxy lazyConnectionDataSourceProxy =
                new LazyConnectionDataSourceProxy(routingDataSource);
        return lazyConnectionDataSourceProxy;
    }

    // AbstractRoutingDataSource
    @Bean
    public DataSource routingDataSource() {
        Map<Object, Object> targetDataSources = createDataSourceMap();
        DataSource defaultDataSource = createDefaultDataSource();
        DemoDataSourceRouter sourceRouter = new DemoDataSourceRouter();
        sourceRouter.setTargetDataSources(targetDataSources);
        sourceRouter.setDefaultTargetDataSource(defaultDataSource);
        return sourceRouter;
    }

    @Bean
    public DataSource createDefaultDataSource() {
        HikariDataSource defaultDataSource = new HikariDataSource();
        defaultDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        defaultDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/demo_ds_0");
        defaultDataSource.setUsername("root");
        defaultDataSource.setPassword("root");
        defaultDataSource.setMaximumPoolSize(20); // default 10
        defaultDataSource.setMinimumIdle(5);
        return defaultDataSource;
    }

    private Map<Object, Object> createDataSourceMap() {
        Map<Object, Object> dataSourceMap = new HashMap<>();
        HikariDataSource dataSource0 = new HikariDataSource();
        dataSource0.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource0.setJdbcUrl("jdbc:mysql://localhost:3306/demo_ds_0");
        dataSource0.setUsername("root");
        dataSource0.setPassword("root");
        dataSource0.setMaximumPoolSize(20); // default 10
        dataSource0.setMinimumIdle(5);

        HikariDataSource dataSource1 = new HikariDataSource();
        dataSource1.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource1.setJdbcUrl("jdbc:mysql://localhost:3307/demo_ds_1");
        dataSource1.setUsername("root");
        dataSource1.setPassword("root");
        dataSource1.setMaximumPoolSize(20); // default 10
        dataSource1.setMinimumIdle(5);

        dataSourceMap.put(DemoDatabase.demo_ds_0, dataSource0);
        dataSourceMap.put(DemoDatabase.demo_ds_1, dataSource1);

        return dataSourceMap;
    }

    @Bean
    public Snowflake snowflake() {
        return new Snowflake(1);
    }
}
