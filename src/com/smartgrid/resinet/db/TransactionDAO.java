package com.smartgrid.resinet.db;

import com.smartgrid.resinet.model.EnergyTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for EnergyTransaction history and SQL analytics queries.
 */
public class TransactionDAO {
    private final DatabaseManager dbManager;

    public TransactionDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean insertTransaction(EnergyTransaction tx) throws SQLException {
        String sql = "INSERT INTO energy_transactions (transaction_id, seller_node_id, buyer_node_id, matched_power_kw, price_per_kwh, total_cost, timestamp) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tx.getTransactionId());
            pstmt.setString(2, tx.getSellerNodeId());
            pstmt.setString(3, tx.getBuyerNodeId());
            pstmt.setDouble(4, tx.getMatchedPowerKW());
            pstmt.setDouble(5, tx.getPricePerKWh());
            pstmt.setDouble(6, tx.getTotalCost());
            pstmt.setString(7, tx.getTimestamp().toString());
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<EnergyTransaction> getAllTransactions() throws SQLException {
        List<EnergyTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM energy_transactions ORDER BY timestamp DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                transactions.add(new EnergyTransaction(
                        rs.getString("transaction_id"),
                        rs.getString("seller_node_id"),
                        rs.getString("buyer_node_id"),
                        rs.getDouble("matched_power_kw"),
                        rs.getDouble("price_per_kwh"),
                        rs.getDouble("total_cost"),
                        LocalDateTime.parse(rs.getString("timestamp"))
                ));
            }
        }
        return transactions;
    }

    public double getTotalEnergyTradedKW() throws SQLException {
        String sql = "SELECT SUM(matched_power_kw) FROM energy_transactions";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    public double getTotalTradingVolumeUSD() throws SQLException {
        String sql = "SELECT SUM(total_cost) FROM energy_transactions";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
}
