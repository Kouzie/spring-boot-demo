package com.example.jpa;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class AppStartupRunner implements CommandLineRunner {

    private final DataSource dataSource;

    public AppStartupRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            System.out.println("JDBC URL: " + hikariDataSource.getJdbcUrl());
        }
    }
}