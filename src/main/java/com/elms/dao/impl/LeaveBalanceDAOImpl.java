package com.elms.dao.impl;

import com.elms.config.DBConnectionManager;
import com.elms.dao.LeaveBalanceDAO;
import com.elms.exception.DatabaseException;
import com.elms.model.LeaveBalance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LeaveBalanceDAOImpl implements LeaveBalanceDAO {

    private static final String BASE_SELECT =
            "SELECT lb.id, lb.employee_id, lb.leave_type_id, lt.type_code, lt.type_name, lt.is_paid, " +
            "lb.cal_year, lb.total_allocated, lb.used_days, lb.pending_days, lb.created_at, lb.updated_at " +
            "FROM leave_balances lb " +
            "JOIN leave_types lt ON lb.leave_type_id = lt.id ";

    @Override
    public List<LeaveBalance> findByEmployeeAndYear(int employeeId, int year) {
        List<LeaveBalance> list = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE lb.employee_id = ? AND lb.cal_year = ? ORDER BY lt.id ASC";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query leave balances for employee id: " + employeeId, e);
        }
        return list;
    }

    @Override
    public Optional<LeaveBalance> findByEmployeeAndType(int employeeId, int leaveTypeId, int year, Connection conn) {
        String sql = BASE_SELECT + " WHERE lb.employee_id = ? AND lb.leave_type_id = ? AND lb.cal_year = ?";
        boolean closeConn = false;
        try {
            if (conn == null || conn.isClosed()) {
                conn = DBConnectionManager.getInstance().getConnection();
                closeConn = true;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, employeeId);
                ps.setInt(2, leaveTypeId);
                ps.setInt(3, year);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSet(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query specific leave balance", e);
        } finally {
            if (closeConn && conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
        return Optional.empty();
    }

    @Override
    public int insert(LeaveBalance balance, Connection conn) {
        String sql = "INSERT INTO leave_balances (employee_id, leave_type_id, cal_year, total_allocated, used_days, pending_days) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, balance.getEmployeeId());
            ps.setInt(2, balance.getLeaveTypeId());
            ps.setInt(3, balance.getYear());
            ps.setDouble(4, balance.getTotalAllocated());
            ps.setDouble(5, balance.getUsedDays());
            ps.setDouble(6, balance.getPendingDays());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    balance.setId(id);
                    return id;
                }
            }
            throw new DatabaseException("Failed to obtain ID for leave balance.");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert leave balance for employee: " + balance.getEmployeeId(), e);
        }
    }

    @Override
    public boolean addPendingDays(int employeeId, int leaveTypeId, int year, double days, Connection conn) {
        String sql = "UPDATE leave_balances SET pending_days = pending_days + ? " +
                "WHERE employee_id = ? AND leave_type_id = ? AND cal_year = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, days);
            ps.setInt(2, employeeId);
            ps.setInt(3, leaveTypeId);
            ps.setInt(4, year);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to reserve pending leave days", e);
        }
    }

    @Override
    public boolean commitPendingToUsed(int employeeId, int leaveTypeId, int year, double days, Connection conn) {
        String sql = "UPDATE leave_balances SET " +
                "pending_days = CASE WHEN (pending_days - ?) >= 0 THEN (pending_days - ?) ELSE 0 END, " +
                "used_days = used_days + ? " +
                "WHERE employee_id = ? AND leave_type_id = ? AND cal_year = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, days);
            ps.setDouble(2, days);
            ps.setDouble(3, days);
            ps.setInt(4, employeeId);
            ps.setInt(5, leaveTypeId);
            ps.setInt(6, year);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to commit pending leave to used days", e);
        }
    }

    @Override
    public boolean releasePendingDays(int employeeId, int leaveTypeId, int year, double days, Connection conn) {
        String sql = "UPDATE leave_balances SET " +
                "pending_days = CASE WHEN (pending_days - ?) >= 0 THEN (pending_days - ?) ELSE 0 END " +
                "WHERE employee_id = ? AND leave_type_id = ? AND cal_year = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, days);
            ps.setDouble(2, days);
            ps.setInt(3, employeeId);
            ps.setInt(4, leaveTypeId);
            ps.setInt(5, year);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to release pending leave days", e);
        }
    }

    @Override
    public boolean updateAllocation(int employeeId, int leaveTypeId, int year, double totalAllocated, Connection conn) {
        String sql = "UPDATE leave_balances SET total_allocated = ? WHERE employee_id = ? AND leave_type_id = ? AND cal_year = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, totalAllocated);
            ps.setInt(2, employeeId);
            ps.setInt(3, leaveTypeId);
            ps.setInt(4, year);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update leave allocation", e);
        }
    }

    private LeaveBalance mapResultSet(ResultSet rs) throws SQLException {
        LeaveBalance lb = new LeaveBalance();
        lb.setId(rs.getInt("id"));
        lb.setEmployeeId(rs.getInt("employee_id"));
        lb.setLeaveTypeId(rs.getInt("leave_type_id"));
        lb.setLeaveTypeCode(rs.getString("type_code"));
        lb.setLeaveTypeName(rs.getString("type_name"));
        lb.setPaid(rs.getBoolean("is_paid"));
        lb.setYear(rs.getInt("cal_year"));
        lb.setTotalAllocated(rs.getDouble("total_allocated"));
        lb.setUsedDays(rs.getDouble("used_days"));
        lb.setPendingDays(rs.getDouble("pending_days"));
        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) lb.setCreatedAt(ct.toLocalDateTime());
        Timestamp ut = rs.getTimestamp("updated_at");
        if (ut != null) lb.setUpdatedAt(ut.toLocalDateTime());
        return lb;
    }
}
