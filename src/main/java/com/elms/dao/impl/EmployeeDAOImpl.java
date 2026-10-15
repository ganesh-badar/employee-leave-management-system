package com.elms.dao.impl;

import com.elms.config.DBConnectionManager;
import com.elms.dao.EmployeeDAO;
import com.elms.exception.DatabaseException;
import com.elms.model.Employee;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDAOImpl implements EmployeeDAO {

    private static final String BASE_SELECT =
            "SELECT e.id, e.user_id, e.employee_code, e.first_name, e.last_name, e.email, e.phone, " +
            "e.department_id, d.department_name, e.designation, e.joining_date, e.salary, e.status, " +
            "e.created_at, e.updated_at " +
            "FROM employees e " +
            "LEFT JOIN departments d ON e.department_id = d.id ";

    @Override
    public List<Employee> findAll(String search, Integer departmentId, String status) {
        List<Employee> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            String term = "%" + search.trim().toLowerCase() + "%";
            sql.append(" AND (LOWER(e.first_name) LIKE ? OR LOWER(e.last_name) LIKE ? OR LOWER(e.employee_code) LIKE ? OR LOWER(e.email) LIKE ?) ");
            params.add(term);
            params.add(term);
            params.add(term);
            params.add(term);
        }

        if (departmentId != null && departmentId > 0) {
            sql.append(" AND e.department_id = ? ");
            params.add(departmentId);
        }

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append(" AND e.status = ? ");
            params.add(status.trim().toUpperCase());
        }

        sql.append(" ORDER BY e.id DESC");

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
            throw new DatabaseException("Failed to query employee directory", e);
        }
        return list;
    }

    @Override
    public Optional<Employee> findById(int id) {
        String sql = BASE_SELECT + " WHERE e.id = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query employee by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Employee> findByUserId(int userId) {
        String sql = BASE_SELECT + " WHERE e.user_id = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query employee by user id: " + userId, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Employee> findByEmployeeCode(String code) {
        String sql = BASE_SELECT + " WHERE LOWER(e.employee_code) = LOWER(?)";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query employee by code: " + code, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        String sql = BASE_SELECT + " WHERE LOWER(e.email) = LOWER(?)";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query employee by email: " + email, e);
        }
        return Optional.empty();
    }

    @Override
    public int insert(Employee emp, Connection conn) {
        String sql = "INSERT INTO employees (user_id, employee_code, first_name, last_name, email, phone, " +
                "department_id, designation, joining_date, salary, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, emp.getUserId());
            ps.setString(2, emp.getEmployeeCode().trim().toUpperCase());
            ps.setString(3, emp.getFirstName().trim());
            ps.setString(4, emp.getLastName().trim());
            ps.setString(5, emp.getEmail().trim().toLowerCase());
            ps.setString(6, emp.getPhone());
            if (emp.getDepartmentId() != null && emp.getDepartmentId() > 0) {
                ps.setInt(7, emp.getDepartmentId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setString(8, emp.getDesignation().trim());
            ps.setDate(9, Date.valueOf(emp.getJoiningDate()));
            ps.setBigDecimal(10, emp.getSalary());
            ps.setString(11, emp.getStatus() != null ? emp.getStatus().name() : Employee.Status.ACTIVE.name());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    emp.setId(id);
                    return id;
                }
            }
            throw new DatabaseException("Creating employee failed, no ID obtained.");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert employee record: " + emp.getEmail(), e);
        }
    }

    @Override
    public boolean update(Employee emp, Connection conn) {
        String sql = "UPDATE employees SET first_name = ?, last_name = ?, phone = ?, " +
                "department_id = ?, designation = ?, joining_date = ?, salary = ?, status = ? " +
                "WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getFirstName().trim());
            ps.setString(2, emp.getLastName().trim());
            ps.setString(3, emp.getPhone());
            if (emp.getDepartmentId() != null && emp.getDepartmentId() > 0) {
                ps.setInt(4, emp.getDepartmentId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, emp.getDesignation().trim());
            ps.setDate(6, Date.valueOf(emp.getJoiningDate()));
            ps.setBigDecimal(7, emp.getSalary());
            ps.setString(8, emp.getStatus().name());
            ps.setInt(9, emp.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update employee id: " + emp.getId(), e);
        }
    }

    @Override
    public boolean updateStatus(int id, Employee.Status status, Connection conn) {
        String sql = "UPDATE employees SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update status for employee id: " + id, e);
        }
    }

    @Override
    public boolean delete(int id, Connection conn) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete employee id: " + id, e);
        }
    }

    @Override
    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM employees";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count total employees", e);
        }
        return 0;
    }

    @Override
    public int countActive() {
        String sql = "SELECT COUNT(*) FROM employees WHERE status = 'ACTIVE'";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count active employees", e);
        }
        return 0;
    }

    private Employee mapResultSet(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setId(rs.getInt("id"));
        e.setUserId(rs.getInt("user_id"));
        e.setEmployeeCode(rs.getString("employee_code"));
        e.setFirstName(rs.getString("first_name"));
        e.setLastName(rs.getString("last_name"));
        e.setEmail(rs.getString("email"));
        e.setPhone(rs.getString("phone"));
        int deptId = rs.getInt("department_id");
        if (!rs.wasNull()) {
            e.setDepartmentId(deptId);
        }
        e.setDepartmentName(rs.getString("department_name"));
        e.setDesignation(rs.getString("designation"));
        Date jd = rs.getDate("joining_date");
        if (jd != null) {
            e.setJoiningDate(jd.toLocalDate());
        }
        e.setSalary(rs.getBigDecimal("salary"));
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                e.setStatus(Employee.Status.valueOf(statusStr));
            } catch (IllegalArgumentException ex) {
                e.setStatus(Employee.Status.ACTIVE);
            }
        }
        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) e.setCreatedAt(ct.toLocalDateTime());
        Timestamp ut = rs.getTimestamp("updated_at");
        if (ut != null) e.setUpdatedAt(ut.toLocalDateTime());
        return e;
    }
}
