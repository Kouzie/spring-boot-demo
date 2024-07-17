package com.example.sharding.sphere.demo.config;

import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

@Configuration
public class DbYamlConfig {

    @Bean
    public Snowflake snowflake() {
        return new Snowflake(1, 1);
    }

    @Bean
    public DataSource dataSource() throws SQLException, IOException {
        InputStream resource = new ClassPathResource("sharding-datasource-custom.yaml").getInputStream();
        DataSource dataSource = YamlShardingSphereDataSourceFactory.createDataSource(resource.readAllBytes());
        return dataSource;
    }
}
