package com.smartgrid.resinet.db;

import com.smartgrid.resinet.model.EnergyNode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for EnergyNode CRUD operations via JDBC PreparedStatement.
 */
public class EnergyNodeDAO {
    private final DatabaseManager dbManager;

    public EnergyNodeDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean insertNode(EnergyNode node) throws SQLException {
        String sql = "INSERT OR REPLACE INTO energy_nodes (node_id, name, node_type, location_zone, current_power_kw, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, node.getNodeId());
            pstmt.setString(2, node.getName());
            pstmt.setString(3, node.getNodeType());
            pstmt.setString(4, node.getLocationZone());
            pstmt.setDouble(5, node.getCurrentPowerKW());
            pstmt.setBoolean(6, node.isActive());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateNodePower(String nodeId, double powerKW) throws SQLException {
        String sql = "UPDATE energy_nodes SET current_power_kw = ? WHERE node_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, powerKW);
            pstmt.setString(2, nodeId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public int getActiveNodeCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM energy_nodes WHERE is_active = 1";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<String> getAllNodeSummaries() throws SQLException {
        List<String> summaries = new ArrayList<>();
        String sql = "SELECT node_id, name, node_type, location_zone, current_power_kw FROM energy_nodes";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                summaries.add(String.format("[%s] %s | Type: %s | Zone: %s | Power: %.2f kW",
                        rs.getString("node_id"),
                        rs.getString("name"),
                        rs.getString("node_type"),
                        rs.getString("location_zone"),
                        rs.getDouble("current_power_kw")));
            }
        }
        return summaries;
    }
}
