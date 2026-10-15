package com.elms.dao.impl;

import com.elms.config.DBConnectionManager;
import com.elms.dao.DepartmentDAO;
import com.elms.exception.DatabaseException;
import com.elms.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentDAOImpl implements DepartmentDAO {

    @Override
    public List<Department> findAll() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT d.id, d.department_code, d.department_name, d.description, d.created_at, d.updated_at, " +
                "COUNT(e.id) AS emp_count " +
                "FROM departments d " +
                "LEFT JOIN employees e ON d.id = e.department_id AND e.status != 'TERMINATED' " +
                "GROUP BY d.id, d.department_code, d.department_name, d.description, d.created_at, d.updated_at " +
                "ORDER BY d.department_name ASC";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Department d = mapResultSet(rs);
                d.setEmployeeCount(rs.getInt("emp_count"));
                list.add(d);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch departments", e);
        }
        return list;
    }

    @Override
    public Optional<Department> findById(int id) {
        String sql = "SELECT d.id, d.department_code, d.department_name, d.description, d.created_at, d.updated_at, " +
                "COUNT(e.id) AS emp_count " +
                "FROM departments d " +
                "LEFT JOIN employees e ON d.id = e.department_id AND e.status != 'TERMINATED' " +
                "WHERE d.id = ? " +
                "GROUP BY d.id, d.department_code, d.department_name, d.description, d.created_at, d.updated_at";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Department d = mapResultSet(rs);
                    d.setEmployeeCount(rs.getInt("emp_count"));
                    return Optional.of(d);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find department by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Department> findByCode(String code) {
        String sql = "SELECT id, department_code, department_name, description, created_at, updated_at FROM departments WHERE LOWER(department_code) = LOWER(?)";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find department by code: " + code, e);
        }
        return Optional.empty();
    }

    @Override
    public int insert(Department department) {
        String sql = "INSERT INTO departments (department_code, department_name, description) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, department.getDepartmentCode().trim().toUpperCase());
            ps.setString(2, department.getDepartmentName().trim());
            ps.setString(3, department.getDescription());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    department.setId(id);
                    return id;
                }
            }
            throw new DatabaseException("Failed to obtain ID for department.");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert department: " + department.getDepartmentName(), e);
        }
    }

    @Override
    public boolean update(Department department) {
        String sql = "UPDATE departments SET department_name = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, department.getDepartmentName().trim());
            ps.setString(2, department.getDescription());
            ps.setInt(3, department.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update department: " + department.getId(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM departments WHERE id = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Cannot delete department with associated records.", e);
        }
    }

    private Department mapResultSet(ResultSet rs) throws SQLException {
        Department d = new Department();
        d.setId(rs.getInt("id"));
        d.setDepartmentCode(rs.getString("department_code"));
        d.setDepartmentName(rs.getString("department_name"));
        d.setDescription(rs.getString("description"));
        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) d.setCreatedAt(ct.toLocalDateTime());
        Timestamp ut = rs.getTimestamp("updated_at");
        if (ut != null) d.setUpdatedAt(ut.toLocalDateTime());
        return d;
    }
}
