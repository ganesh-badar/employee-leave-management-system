package com.elms.service;

import com.elms.config.DBConnectionManager;
import com.elms.dao.AuditLogDAO;
import com.elms.dao.EmployeeDAO;
import com.elms.dao.LeaveBalanceDAO;
import com.elms.dao.LeaveRequestDAO;
import com.elms.dao.UserDAO;
import com.elms.dao.impl.AuditLogDAOImpl;
import com.elms.dao.impl.EmployeeDAOImpl;
import com.elms.dao.impl.LeaveBalanceDAOImpl;
import com.elms.dao.impl.LeaveRequestDAOImpl;
import com.elms.dao.impl.UserDAOImpl;
import com.elms.exception.DatabaseException;
import com.elms.exception.ValidationException;
import com.elms.model.AuditLog;
import com.elms.model.Employee;
import com.elms.model.LeaveRequest;
import com.elms.util.ValidationUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Enterprise service handling Employee CRUD operations, status transitions, and HR metrics.
 */
public class EmployeeService {

    private final EmployeeDAO employeeDAO = new EmployeeDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();
    private final LeaveBalanceDAO leaveBalanceDAO = new LeaveBalanceDAOImpl();
    private final LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();

    public Optional<Employee> getEmployeeById(int id) {
        return employeeDAO.findById(id);
    }

    public Optional<Employee> getEmployeeByUserId(int userId) {
        return employeeDAO.findByUserId(userId);
    }

    public List<Employee> getAllEmployees(String search, Integer departmentId, String status) {
        return employeeDAO.findAll(search, departmentId, status);
    }

    public void updateEmployee(Employee emp, int performedByUserId, String performedByName) {
        ValidationUtil.requireNonEmpty(emp.getFirstName(), "First name");
        ValidationUtil.requireNonEmpty(emp.getLastName(), "Last name");
        ValidationUtil.requireNonEmpty(emp.getDesignation(), "Designation");
        ValidationUtil.validatePhone(emp.getPhone());

        Employee existing = employeeDAO.findById(emp.getId())
                .orElseThrow(() -> new ValidationException("Employee not found with id: " + emp.getId()));

        Connection conn = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            employeeDAO.update(emp, conn);

            // Audit
            String details = String.format("Updated employee %s: Dept=%s, Desig=%s, Salary=%s, Status=%s",
                    emp.getEmployeeCode(), emp.getDepartmentId(), emp.getDesignation(), emp.getSalary(), emp.getStatus());
            AuditLog log = new AuditLog("EMPLOYEE", emp.getId(), "UPDATE", performedByUserId, performedByName, details);
            auditLogDAO.insert(log, conn);

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new DatabaseException("Failed to update employee details", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public void toggleEmployeeStatus(int employeeId, Employee.Status newStatus, int performedByUserId, String performedByName) {
        Employee emp = employeeDAO.findById(employeeId)
                .orElseThrow(() -> new ValidationException("Employee record not found."));

        Connection conn = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            employeeDAO.updateStatus(employeeId, newStatus, conn);
            // Deactivate or activate associated user credentials
            boolean userActive = (newStatus == Employee.Status.ACTIVE);
            userDAO.updateStatus(emp.getUserId(), userActive);

            String details = "Status changed from " + emp.getStatus() + " to " + newStatus;
            AuditLog log = new AuditLog("EMPLOYEE", employeeId, "STATUS_CHANGE", performedByUserId, performedByName, details);
            auditLogDAO.insert(log, conn);

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new DatabaseException("Failed to toggle employee status", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public void updateLeaveAllocation(int employeeId, int leaveTypeId, int year, double totalAllocated, int performedByUserId, String performedByName) {
        if (totalAllocated < 0) {
            throw new ValidationException("totalAllocated", "Quota cannot be negative.");
        }
        leaveBalanceDAO.updateAllocation(employeeId, leaveTypeId, year, totalAllocated, null);
        AuditLog log = new AuditLog("LEAVE_BALANCE", employeeId, "ADJUST_QUOTA", performedByUserId, performedByName,
                String.format("Adjusted leave quota for type %d (Year %d) to %.1f days", leaveTypeId, year, totalAllocated));
        auditLogDAO.insert(log, null);
    }

    public Map<String, Object> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalEmployees", employeeDAO.countTotal());
        metrics.put("activeEmployees", employeeDAO.countActive());
        metrics.put("pendingLeaves", leaveRequestDAO.countByStatus(LeaveRequest.Status.PENDING));
        metrics.put("approvedLeaves", leaveRequestDAO.countByStatus(LeaveRequest.Status.APPROVED));
        metrics.put("rejectedLeaves", leaveRequestDAO.countByStatus(LeaveRequest.Status.REJECTED));
        metrics.put("totalLeaves", leaveRequestDAO.countTotal());
        return metrics;
    }
}
