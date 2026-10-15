package com.elms.dao.impl;

import com.elms.config.DBConnectionManager;
import com.elms.dao.LeaveTypeDAO;
import com.elms.exception.DatabaseException;
import com.elms.model.LeaveType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LeaveTypeDAOImpl implements LeaveTypeDAO {

    @Override
    public List<LeaveType> findAll() {
        List<LeaveType> list = new ArrayList<>();
        String sql = "SELECT id, type_code, type_name, description, default_days_per_year, is_paid, created_at FROM leave_types ORDER BY id ASC";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query leave types", e);
        }
        return list;
    }

    @Override
    public Optional<LeaveType> findById(int id) {
        String sql = "SELECT id, type_code, type_name, description, default_days_per_year, is_paid, created_at FROM leave_types WHERE id = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query leave type by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<LeaveType> findByCode(String code) {
        String sql = "SELECT id, type_code, type_name, description, default_days_per_year, is_paid, created_at FROM leave_types WHERE LOWER(type_code) = LOWER(?)";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query leave type by code: " + code, e);
        }
        return Optional.empty();
    }

    private LeaveType mapResultSet(ResultSet rs) throws SQLException {
        LeaveType lt = new LeaveType();
        lt.setId(rs.getInt("id"));
        lt.setTypeCode(rs.getString("type_code"));
        lt.setTypeName(rs.getString("type_name"));
        lt.setDescription(rs.getString("description"));
        lt.setDefaultDaysPerYear(rs.getInt("default_days_per_year"));
        lt.setPaid(rs.getBoolean("is_paid"));
        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) lt.setCreatedAt(ct.toLocalDateTime());
        return lt;
    }
}
