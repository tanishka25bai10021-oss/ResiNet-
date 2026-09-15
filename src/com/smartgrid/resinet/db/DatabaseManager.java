package com.smartgrid.resinet.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager - Singleton class handling JDBC connection lifecycle and SQLite schema initialization.
 * Fulfills CSE2006 Unit 5 (Database Applications with JDBC).
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private static final String DB_PATH = "data/resinet_grid.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_PATH;

    private DatabaseManager() {
        initDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(JDBC_URL);
    }

    private void initDatabase() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Table for Microgrid Nodes
            String createNodesTable = "CREATE TABLE IF NOT EXISTS energy_nodes (" +
                    "node_id VARCHAR(32) PRIMARY KEY, " +
                    "name VARCHAR(64) NOT NULL, " +
                    "node_type VARCHAR(32) NOT NULL, " +
                    "location_zone VARCHAR(32) NOT NULL, " +
                    "current_power_kw DOUBLE NOT NULL, " +
                    "is_active BOOLEAN NOT NULL" +
                    ");";

            // Table for Energy Transactions
            String createTxTable = "CREATE TABLE IF NOT EXISTS energy_transactions (" +
                    "transaction_id VARCHAR(64) PRIMARY KEY, " +
                    "seller_node_id VARCHAR(32) NOT NULL, " +
                    "buyer_node_id VARCHAR(32) NOT NULL, " +
                    "matched_power_kw DOUBLE NOT NULL, " +
                    "price_per_kwh DOUBLE NOT NULL, " +
                    "total_cost DOUBLE NOT NULL, " +
                    "timestamp DATETIME NOT NULL" +
                    ");";

            stmt.execute(createNodesTable);
            stmt.execute(createTxTable);
            System.out.println("[DatabaseManager] SQLite Schema initialized successfully at " + DB_PATH);
        } catch (SQLException e) {
            System.err.println("[DatabaseManager] DB Initialization error: " + e.getMessage());
        }
    }
}
