package com.example.laborator5.socialnetwork.repository.database.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The class for the HikariCP
 */
public class DataSource {

    /**
     * The configuration for the HikariCP
     */
    private static HikariConfig config = new HikariConfig();

    /**
     * The connections pool
     */
    private static HikariDataSource ds;

    static {

        String url = System.getProperty("jdbc_url");

        config.setJdbcUrl(url);
        config.setUsername("postgres");
        config.setPassword("admin");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);

    }

    /**
     * The private constructor of the class
     */
    private DataSource() {
    }

    /**
     * Static method for getting a connection from the connection pool
     *
     * @return a connection from the connection pool
     * @throws SQLException - if an error occurs
     */
    public static Connection getConnection() throws SQLException {

        return ds.getConnection();
    }
}
