package com.elms.dao.impl;

import com.elms.config.DBConnectionManager;
import com.elms.dao.AuditLogDAO;
import com.elms.exception.DatabaseException;
import com.elms.model.AuditLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAOImpl implements AuditLogDAO {

    @Override
    public int insert(AuditLog log, Connection conn) {
        String sql = "INSERT INTO audit_logs (entity_type, entity_id, action, performed_by_id, performed_by_name, details) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        boolean closeConn = false;
        try {
            if (conn == null || conn.isClosed()) {
                conn = DBConnectionManager.getInstance().getConnection();
                closeConn = true;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, log.getEntityType());
                ps.setInt(2, log.getEntityId());
                ps.setString(3, log.getAction());
                if (log.getPerformedById() != null && log.getPerformedById() > 0) {
                    ps.setInt(4, log.getPerformedById());
                } else {
                    ps.setNull(4, Types.INTEGER);
                }
                ps.setString(5, log.getPerformedByName() != null ? log.getPerformedByName() : "SYSTEM");
                ps.setString(6, log.getDetails());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        log.setId(id);
                        return id;
                    }
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to record audit log entry", e);
        } finally {
            if (closeConn && conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public List<AuditLog> findRecent(int limit) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT id, entity_type, entity_id, action, performed_by_id, performed_by_name, details, created_at " +
                "FROM audit_logs ORDER BY id DESC LIMIT ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 50);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditLog al = new AuditLog();
                    al.setId(rs.getInt("id"));
                    al.setEntityType(rs.getString("entity_type"));
                    al.setEntityId(rs.getInt("entity_id"));
                    al.setAction(rs.getString("action"));
                    int pId = rs.getInt("performed_by_id");
                    if (!rs.wasNull()) al.setPerformedById(pId);
                    al.setPerformedByName(rs.getString("performed_by_name"));
                    al.setDetails(rs.getString("details"));
                    Timestamp ct = rs.getTimestamp("created_at");
                    if (ct != null) al.setCreatedAt(ct.toLocalDateTime());
                    list.add(al);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query recent audit logs", e);
        }
        return list;
    }
}
