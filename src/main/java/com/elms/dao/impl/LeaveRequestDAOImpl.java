package com.elms.dao.impl;

import com.elms.config.DBConnectionManager;
import com.elms.dao.LeaveRequestDAO;
import com.elms.exception.DatabaseException;
import com.elms.model.LeaveRequest;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LeaveRequestDAOImpl implements LeaveRequestDAO {

    private static final String BASE_SELECT =
            "SELECT lr.id, lr.employee_id, e.employee_code, e.first_name, e.last_name, d.department_name, " +
            "lr.leave_type_id, lt.type_code, lt.type_name, " +
            "lr.start_date, lr.end_date, lr.total_days, lr.reason, lr.emergency_contact, " +
            "lr.status, lr.admin_remarks, lr.reviewed_by, u.username AS reviewer_name, lr.reviewed_at, " +
            "lr.created_at, lr.updated_at " +
            "FROM leave_requests lr " +
            "JOIN employees e ON lr.employee_id = e.id " +
            "LEFT JOIN departments d ON e.department_id = d.id " +
            "JOIN leave_types lt ON lr.leave_type_id = lt.id " +
            "LEFT JOIN users u ON lr.reviewed_by = u.id ";

    @Override
    public List<LeaveRequest> findAll(LeaveRequest.Status status, Integer departmentId, Integer employeeId) {
        List<LeaveRequest> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (status != null) {
            sql.append(" AND lr.status = ? ");
            params.add(status.name());
        }

        if (departmentId != null && departmentId > 0) {
            sql.append(" AND e.department_id = ? ");
            params.add(departmentId);
        }

        if (employeeId != null && employeeId > 0) {
            sql.append(" AND lr.employee_id = ? ");
            params.add(employeeId);
        }

        sql.append(" ORDER BY lr.id DESC");

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query leave requests", e);
        }
        return list;
    }

    @Override
    public Optional<LeaveRequest> findById(int id) {
        String sql = BASE_SELECT + " WHERE lr.id = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query leave request by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public int insert(LeaveRequest lr, Connection conn) {
        String sql = "INSERT INTO leave_requests (employee_id, leave_type_id, start_date, end_date, total_days, reason, emergency_contact, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, lr.getEmployeeId());
            ps.setInt(2, lr.getLeaveTypeId());
            ps.setDate(3, Date.valueOf(lr.getStartDate()));
            ps.setDate(4, Date.valueOf(lr.getEndDate()));
            ps.setDouble(5, lr.getTotalDays());
            ps.setString(6, lr.getReason());
            ps.setString(7, lr.getEmergencyContact());
            ps.setString(8, lr.getStatus() != null ? lr.getStatus().name() : LeaveRequest.Status.PENDING.name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    lr.setId(id);
                    return id;
                }
            }
            throw new DatabaseException("Failed to obtain ID for leave request.");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert leave request for employee: " + lr.getEmployeeId(), e);
        }
    }

    @Override
    public boolean updateStatus(int id, LeaveRequest.Status status, String remarks, int reviewerId, Connection conn) {
        String sql = "UPDATE leave_requests SET status = ?, admin_remarks = ?, reviewed_by = ?, reviewed_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, remarks);
            ps.setInt(3, reviewerId);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update status for leave request id: " + id, e);
        }
    }

    @Override
    public boolean cancel(int id, int employeeId, Connection conn) {
        String sql = "UPDATE leave_requests SET status = 'CANCELLED' WHERE id = ? AND employee_id = ? AND status = 'PENDING'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to cancel leave request id: " + id, e);
        }
    }

    @Override
    public List<LeaveRequest> findOverlappingApprovedLeaves(int departmentId, LocalDate start, LocalDate end, int excludeRequestId) {
        List<LeaveRequest> list = new ArrayList<>();
        String sql = BASE_SELECT +
                " WHERE e.department_id = ? " +
                " AND lr.status = 'APPROVED' " +
                " AND lr.id != ? " +
                " AND ((lr.start_date <= ? AND lr.end_date >= ?) OR (lr.start_date >= ? AND lr.start_date <= ?))";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            ps.setInt(2, excludeRequestId);
            ps.setDate(3, Date.valueOf(end));
            ps.setDate(4, Date.valueOf(start));
            ps.setDate(5, Date.valueOf(start));
            ps.setDate(6, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query overlapping department leaves", e);
        }
        return list;
    }

    @Override
    public int countByStatus(LeaveRequest.Status status) {
        String sql = "SELECT COUNT(*) FROM leave_requests WHERE status = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count leave requests by status", e);
        }
        return 0;
    }

    @Override
    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM leave_requests";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count total leave requests", e);
        }
        return 0;
    }

    private LeaveRequest mapResultSet(ResultSet rs) throws SQLException {
        LeaveRequest lr = new LeaveRequest();
        lr.setId(rs.getInt("id"));
        lr.setEmployeeId(rs.getInt("employee_id"));
        lr.setEmployeeCode(rs.getString("employee_code"));
        String fn = rs.getString("first_name");
        String ln = rs.getString("last_name");
        lr.setEmployeeName((fn != null ? fn : "") + " " + (ln != null ? ln : ""));
        lr.setDepartmentName(rs.getString("department_name"));

        lr.setLeaveTypeId(rs.getInt("leave_type_id"));
        lr.setLeaveTypeCode(rs.getString("type_code"));
        lr.setLeaveTypeName(rs.getString("type_name"));

        Date sd = rs.getDate("start_date");
        if (sd != null) lr.setStartDate(sd.toLocalDate());
        Date ed = rs.getDate("end_date");
        if (ed != null) lr.setEndDate(ed.toLocalDate());

        lr.setTotalDays(rs.getDouble("total_days"));
        lr.setReason(rs.getString("reason"));
        lr.setEmergencyContact(rs.getString("emergency_contact"));

        String st = rs.getString("status");
        if (st != null) {
            try {
                lr.setStatus(LeaveRequest.Status.valueOf(st));
            } catch (IllegalArgumentException ignored) {
                lr.setStatus(LeaveRequest.Status.PENDING);
            }
        }

        lr.setAdminRemarks(rs.getString("admin_remarks"));
        int revBy = rs.getInt("reviewed_by");
        if (!rs.wasNull()) {
            lr.setReviewedBy(revBy);
        }
        lr.setReviewerName(rs.getString("reviewer_name"));
        Timestamp ra = rs.getTimestamp("reviewed_at");
        if (ra != null) lr.setReviewedAt(ra.toLocalDateTime());

        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) lr.setCreatedAt(ct.toLocalDateTime());
        Timestamp ut = rs.getTimestamp("updated_at");
        if (ut != null) lr.setUpdatedAt(ut.toLocalDateTime());

        return lr;
    }
}
