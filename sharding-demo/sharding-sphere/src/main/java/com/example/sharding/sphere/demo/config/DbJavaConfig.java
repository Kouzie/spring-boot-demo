/*
package com.example.sharding.sphere.demo.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.AlgorithmConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.NoneShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class DbJavaConfig {
    @Bean
    public DataSource dataSource() throws SQLException {
        // Create ShardingSphereDataSource
        DataSource dataSource = ShardingSphereDataSourceFactory.createDataSource(createDataSourceMap(),
                Collections.singleton(createShardingRuleConfiguration()),
                new Properties());
        return dataSource;
    }

    private Map<String, DataSource> createDataSourceMap() {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        HikariDataSource dataSource1 = new HikariDataSource();
        dataSource1.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource1.setJdbcUrl("jdbc:mysql://localhost:3306/db1");
        dataSource1.setUsername("root");
        dataSource1.setPassword("root");
        dataSource1.setMaximumPoolSize(20); // default 10
        dataSource1.setMinimumIdle(5);
        dataSourceMap.put("db1", dataSource1);

        HikariDataSource dataSource2 = new HikariDataSource();
        dataSource2.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource2.setJdbcUrl("jdbc:mysql://localhost:3307/db2");
        dataSource2.setUsername("root");
        dataSource2.setPassword("root");
        dataSource2.setMaximumPoolSize(20); // default 10
        dataSource2.setMinimumIdle(5);

        dataSourceMap.put("db2", dataSource2);

        return dataSourceMap;
    }

    // Configure split_pay_entity table rule
    private ShardingTableRuleConfiguration getSplitPayTableRuleConfiguration() {
        ShardingTableRuleConfiguration result = new ShardingTableRuleConfiguration("split_pay_entity", "db${1..2}.split_pay_entity");
        result.setDatabaseShardingStrategy(new StandardShardingStrategyConfiguration("id", "snowflake-algorithm")); // Configure database sharding strategy
        result.setTableShardingStrategy(new NoneShardingStrategyConfiguration());
        return result;
    }

    // Configure split_pay_perform_entity table rule
    private ShardingTableRuleConfiguration getSplitPayPerformTableRuleConfiguration() {
        ShardingTableRuleConfiguration result = new ShardingTableRuleConfiguration("split_pay_perform_entity", "db${1..2}.split_pay_perform_entity");
        result.setDatabaseShardingStrategy(new StandardShardingStrategyConfiguration("id", "snowflake-algorithm")); // Configure database sharding strategy
        result.setTableShardingStrategy(new NoneShardingStrategyConfiguration());
        return result;
    }

    private ShardingRuleConfiguration createShardingRuleConfiguration() {
        ShardingRuleConfiguration result = new ShardingRuleConfiguration();
        result.getTables().add(getSplitPayTableRuleConfiguration());
        result.getTables().add(getSplitPayPerformTableRuleConfiguration());
        result.setDefaultDatabaseShardingStrategy(new StandardShardingStrategyConfiguration("id", "snowflake-algorithm"));
        result.setDefaultTableShardingStrategy(new StandardShardingStrategyConfiguration("id", "snowflake-algorithm"));

        // result.getBindingTableGroups().add(new ShardingTableReferenceRuleConfiguration("foo", "t_order, t_order_item"));
        Properties props = new Properties();
        props.setProperty("strategy", "STANDARD");
        props.setProperty("algorithmClassName", "com.example.practice.domain.split.pay.config.SnowflakeShardingAlgorithm");
        result.getShardingAlgorithms().put("snowflake-algorithm", new AlgorithmConfiguration("CLASS_BASED", props));

        return result;
    }
}
*/
