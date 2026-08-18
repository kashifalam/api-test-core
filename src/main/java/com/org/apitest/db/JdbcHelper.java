package com.org.apitest.db;

import com.org.apitest.config.EnvironmentConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcHelper implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcHelper.class);

    private final HikariDataSource dataSource;

    public JdbcHelper(EnvironmentConfig.JdbcConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getJdbcUrl());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setMaximumPoolSize(config.getMaxPoolSize());
        hikariConfig.setPoolName("api-test-jdbc");
        this.dataSource = new HikariDataSource(hikariConfig);
    }

    public Map<String, Object> queryForMap(String sql, Object... params) {
        List<Map<String, Object>> rows = queryForList(sql, params);
        if (rows.isEmpty()) {
            return Map.of();
        }
        return rows.getFirst();
    }

    public List<Map<String, Object>> queryForList(String sql, Object... params) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            bindParams(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(meta.getColumnLabel(i), rs.getObject(i));
                    }
                    results.add(row);
                }
                return results;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("JDBC query failed: " + sql, e);
        }
    }

    public int executeUpdate(String sql, Object... params) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            bindParams(stmt, params);
            int updated = stmt.executeUpdate();
            LOG.debug("Executed update, rows affected: {}", updated);
            return updated;
        } catch (SQLException e) {
            throw new IllegalStateException("JDBC update failed: " + sql, e);
        }
    }

    private void bindParams(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
