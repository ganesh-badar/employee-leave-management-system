package com.elms.service;

import com.elms.config.DBConnectionManager;
import com.elms.dao.AuditLogDAO;
import com.elms.dao.EmployeeDAO;
import com.elms.dao.LeaveBalanceDAO;
import com.elms.dao.LeaveRequestDAO;
import com.elms.dao.LeaveTypeDAO;
import com.elms.dao.impl.AuditLogDAOImpl;
import com.elms.dao.impl.EmployeeDAOImpl;
import com.elms.dao.impl.LeaveBalanceDAOImpl;
import com.elms.dao.impl.LeaveRequestDAOImpl;
import com.elms.dao.impl.LeaveTypeDAOImpl;
import com.elms.exception.DatabaseException;
import com.elms.exception.ValidationException;
import com.elms.model.AuditLog;
import com.elms.model.Employee;
import com.elms.model.LeaveBalance;
import com.elms.model.LeaveRequest;
import com.elms.model.LeaveType;
import com.elms.util.DateUtil;
import com.elms.util.ValidationUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Enterprise Leave Service managing applications, approval workflows, balance integrity, and conflict checking.
 */
public class LeaveService {

    private final LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAOImpl();
    private final LeaveBalanceDAO leaveBalanceDAO = new LeaveBalanceDAOImpl();
    private final LeaveTypeDAO leaveTypeDAO = new LeaveTypeDAOImpl();
    private final EmployeeDAO employeeDAO = new EmployeeDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();

    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeDAO.findAll();
    }

    public List<LeaveBalance> getEmployeeBalances(int employeeId, int year) {
        return leaveBalanceDAO.findByEmployeeAndYear(employeeId, year);
    }

    public List<LeaveRequest> getEmployeeLeaves(int employeeId) {
        return leaveRequestDAO.findAll(null, null, employeeId);
    }

    public List<LeaveRequest> getAllRequests(LeaveRequest.Status status, Integer departmentId, Integer employeeId) {
        return leaveRequestDAO.findAll(status, departmentId, employeeId);
    }

    public Optional<LeaveRequest> getRequestById(int id) {
        return leaveRequestDAO.findById(id);
    }

    /**
     * Submits a leave application with balance reservation and date validation.
     */
    public LeaveRequest applyForLeave(int employeeId, int leaveTypeId, LocalDate startDate,
                                      LocalDate endDate, String reason, String emergencyContact) {
        ValidationUtil.requireNonEmpty(reason, "Reason for leave");
        DateUtil.validateDateRange(startDate, endDate, false);

        double totalDays = DateUtil.calculateBusinessDays(startDate, endDate);
        if (totalDays <= 0) {
            throw new ValidationException("Selected date range contains no working business days (weekend days only).");
        }

        Employee employee = employeeDAO.findById(employeeId)
                .orElseThrow(() -> new ValidationException("Employee record not found."));

        LeaveType leaveType = leaveTypeDAO.findById(leaveTypeId)
                .orElseThrow(() -> new ValidationException("Invalid leave category selected."));

        int year = startDate.getYear();

        Connection conn = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Check balance
            Optional<LeaveBalance> balanceOpt = leaveBalanceDAO.findByEmployeeAndType(employeeId, leaveTypeId, year, conn);
            if (balanceOpt.isEmpty()) {
                // Initialize if not present
                LeaveBalance newBalance = new LeaveBalance();
                newBalance.setEmployeeId(employeeId);
                newBalance.setLeaveTypeId(leaveTypeId);
                newBalance.setYear(year);
                newBalance.setTotalAllocated(leaveType.getDefaultDaysPerYear());
                newBalance.setUsedDays(0.0);
                newBalance.setPendingDays(0.0);
                leaveBalanceDAO.insert(newBalance, conn);
                balanceOpt = Optional.of(newBalance);
            }

            LeaveBalance balance = balanceOpt.get();
            double remaining = balance.getRemainingDays();

            // LOP (Loss of Pay) allows flexibility, but normal paid leaves require available quota
            if (!"LOP".equalsIgnoreCase(leaveType.getTypeCode()) && totalDays > remaining) {
                throw new ValidationException(String.format(
                        "Insufficient leave quota for %s. Requested: %.1f days, Available unreserved: %.1f days.",
                        leaveType.getTypeName(), totalDays, remaining));
            }

            // 1. Insert Leave Request
            LeaveRequest req = new LeaveRequest();
            req.setEmployeeId(employeeId);
            req.setLeaveTypeId(leaveTypeId);
            req.setStartDate(startDate);
            req.setEndDate(endDate);
            req.setTotalDays(totalDays);
            req.setReason(reason.trim());
            req.setEmergencyContact(emergencyContact != null ? emergencyContact.trim() : null);
            req.setStatus(LeaveRequest.Status.PENDING);

            int requestId = leaveRequestDAO.insert(req, conn);
            req.setId(requestId);

            // 2. Reserve pending days on balance
            leaveBalanceDAO.addPendingDays(employeeId, leaveTypeId, year, totalDays, conn);

            // 3. Audit trail
            String auditMsg = String.format("Applied for %.1f days of %s (%s to %s)",
                    totalDays, leaveType.getTypeName(), startDate, endDate);
            AuditLog log = new AuditLog("LEAVE_REQUEST", requestId, "APPLY", employee.getUserId(),
                    employee.getFullName(), auditMsg);
            auditLogDAO.insert(log, conn);

            conn.commit();
            return req;

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            if (e instanceof ValidationException) {
                throw (ValidationException) e;
            }
            throw new DatabaseException("Failed to submit leave application: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Cancels a pending leave application and releases reserved days.
     */
    public void cancelLeave(int requestId, int employeeId, String employeeName, int userId) {
        LeaveRequest req = leaveRequestDAO.findById(requestId)
                .orElseThrow(() -> new ValidationException("Leave request not found."));

        if (req.getEmployeeId() != employeeId) {
            throw new ValidationException("You are not authorized to cancel this leave application.");
        }

        if (req.getStatus() != LeaveRequest.Status.PENDING) {
            throw new ValidationException("Only pending leave requests can be cancelled.");
        }

        Connection conn = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            leaveRequestDAO.cancel(requestId, employeeId, conn);
            int year = req.getStartDate().getYear();
            leaveBalanceDAO.releasePendingDays(employeeId, req.getLeaveTypeId(), year, req.getTotalDays(), conn);

            AuditLog log = new AuditLog("LEAVE_REQUEST", requestId, "CANCEL", userId, employeeName,
                    "Leave request cancelled by employee.");
            auditLogDAO.insert(log, conn);

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new DatabaseException("Failed to cancel leave request", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Approves a pending leave request, moving pending days to used days.
     */
    public void approveLeave(int requestId, String remarks, int adminUserId, String adminUsername) {
        LeaveRequest req = leaveRequestDAO.findById(requestId)
                .orElseThrow(() -> new ValidationException("Leave request not found."));

        if (req.getStatus() != LeaveRequest.Status.PENDING) {
            throw new ValidationException("This leave request is already " + req.getStatus());
        }

        Connection conn = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            leaveRequestDAO.updateStatus(requestId, LeaveRequest.Status.APPROVED, remarks, adminUserId, conn);
            int year = req.getStartDate().getYear();
            leaveBalanceDAO.commitPendingToUsed(req.getEmployeeId(), req.getLeaveTypeId(), year, req.getTotalDays(), conn);

            String details = "Approved by " + adminUsername + ". Remarks: " + (remarks != null ? remarks : "None");
            AuditLog log = new AuditLog("LEAVE_REQUEST", requestId, "APPROVE", adminUserId, adminUsername, details);
            auditLogDAO.insert(log, conn);

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new DatabaseException("Failed to approve leave request", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Rejects a pending leave request, releasing the reserved pending days.
     */
    public void rejectLeave(int requestId, String remarks, int adminUserId, String adminUsername) {
        ValidationUtil.requireNonEmpty(remarks, "Rejection reason / remarks");

        LeaveRequest req = leaveRequestDAO.findById(requestId)
                .orElseThrow(() -> new ValidationException("Leave request not found."));

        if (req.getStatus() != LeaveRequest.Status.PENDING) {
            throw new ValidationException("This leave request is already " + req.getStatus());
        }

        Connection conn = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            leaveRequestDAO.updateStatus(requestId, LeaveRequest.Status.REJECTED, remarks, adminUserId, conn);
            int year = req.getStartDate().getYear();
            leaveBalanceDAO.releasePendingDays(req.getEmployeeId(), req.getLeaveTypeId(), year, req.getTotalDays(), conn);

            String details = "Rejected by " + adminUsername + ". Reason: " + remarks;
            AuditLog log = new AuditLog("LEAVE_REQUEST", requestId, "REJECT", adminUserId, adminUsername, details);
            auditLogDAO.insert(log, conn);

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new DatabaseException("Failed to reject leave request", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Checks if any colleagues in the same department are on approved leave on the same dates.
     */
    public List<LeaveRequest> checkDepartmentConflicts(LeaveRequest req) {
        Employee emp = employeeDAO.findById(req.getEmployeeId()).orElse(null);
        if (emp == null || emp.getDepartmentId() == null) {
            return List.of();
        }
        return leaveRequestDAO.findOverlappingApprovedLeaves(emp.getDepartmentId(), req.getStartDate(), req.getEndDate(), req.getId());
    }

    public List<AuditLog> getRecentAuditLogs(int limit) {
        return auditLogDAO.findRecent(limit);
    }
}
