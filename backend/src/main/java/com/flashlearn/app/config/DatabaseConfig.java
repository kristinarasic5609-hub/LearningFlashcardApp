package com.flashlearn.app.config;

import com.flashlearn.app.utility.DatabaseConnection;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import jakarta.annotation.PostConstruct;

@Configuration
public class DatabaseConfig {

    private final DataSource dataSource;

    public DatabaseConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    void initializeDatabaseConnection() {
        DatabaseConnection.getInstance(dataSource);
    }
}
