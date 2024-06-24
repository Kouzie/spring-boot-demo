package com.example.sharding.sphere.demo.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.AlgorithmConfiguration;
import org.apache.shardingsphere.sharding.algorithm.keygen.SnowflakeKeyGenerateAlgorithm;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.NoneShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class DbYamlConfig {
    @Bean
    public DataSource dataSource() throws SQLException, IOException {
        // Create ShardingSphereDataSource
        ClassPathResource resource = new ClassPathResource("sharding-datasource.yaml"); // Indicate YAML file
        DataSource dataSource = YamlShardingSphereDataSourceFactory.createDataSource(resource.getFile());
        return dataSource;
    }

    @Bean
    public SnowflakeKeyGenerateAlgorithm snowflakeKeyGenerateAlgorithm() {
        return new SnowflakeKeyGenerateAlgorithm();
    }


}
