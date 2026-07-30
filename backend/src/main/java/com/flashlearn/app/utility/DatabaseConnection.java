package com.flashlearn.app.utility;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Singleton database connection accessor.
 * Ensures only one DatabaseConnection instance exists for the application.
 */
public final class DatabaseConnection {

    private static DatabaseConnection instance;
    private final DataSource dataSource;

    private DatabaseConnection(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static synchronized DatabaseConnection getInstance(DataSource dataSource) {
        if (instance == null) {
            instance = new DatabaseConnection(dataSource);
        }
        return instance;
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseConnection has not been initialized");
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
