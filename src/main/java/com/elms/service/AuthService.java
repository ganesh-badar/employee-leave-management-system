package com.elms.service;

import com.elms.config.DBConnectionManager;
import com.elms.dao.AuditLogDAO;
import com.elms.dao.DepartmentDAO;
import com.elms.dao.EmployeeDAO;
import com.elms.dao.LeaveBalanceDAO;
import com.elms.dao.LeaveTypeDAO;
import com.elms.dao.UserDAO;
import com.elms.dao.impl.AuditLogDAOImpl;
import com.elms.dao.impl.DepartmentDAOImpl;
import com.elms.dao.impl.EmployeeDAOImpl;
import com.elms.dao.impl.LeaveBalanceDAOImpl;
import com.elms.dao.impl.LeaveTypeDAOImpl;
import com.elms.dao.impl.UserDAOImpl;
import com.elms.exception.AuthenticationException;
import com.elms.exception.DatabaseException;
import com.elms.exception.ValidationException;
import com.elms.model.AuditLog;
import com.elms.model.Employee;
import com.elms.model.LeaveBalance;
import com.elms.model.LeaveType;
import com.elms.model.User;
import com.elms.util.PasswordUtil;
import com.elms.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Business service handling authentication, registration, session checks, and credentials.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAOImpl();
    private final EmployeeDAO employeeDAO = new EmployeeDAOImpl();
    private final LeaveTypeDAO leaveTypeDAO = new LeaveTypeDAOImpl();
    private final LeaveBalanceDAO leaveBalanceDAO = new LeaveBalanceDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();

    /**
     * Authenticates an employee or administrator by username or email.
     */
    public User authenticate(String usernameOrEmail, String plainPassword) {
        ValidationUtil.requireNonEmpty(usernameOrEmail, "Username or corporate email");
        ValidationUtil.requireNonEmpty(plainPassword, "Password");

        Optional<User> userOpt = userDAO.findByUsername(usernameOrEmail.trim());
        if (userOpt.isEmpty()) {
            userOpt = userDAO.findByEmail(usernameOrEmail.trim());
        }

        if (userOpt.isEmpty()) {
            throw new AuthenticationException("Invalid username/email or password credentials.");
        }

        User user = userOpt.get();

        if (!user.isActive()) {
            throw new AuthenticationException("This account has been deactivated. Please contact HR Operations.");
        }

        if (!PasswordUtil.checkPassword(plainPassword, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username/email or password credentials.");
        }

        return user;
    }

    /**
     * Registers a new employee with an associated user account, initial leave quotas, and audit trail.
     */
    public Employee registerEmployee(String username, String email, String password,
                                     String firstName, String lastName, String phone,
                                     Integer departmentId, String designation,
                                     LocalDate joiningDate, BigDecimal salary) {
        // Enforce validations
        ValidationUtil.validateUsername(username);
        ValidationUtil.validateEmail(email);
        ValidationUtil.validatePassword(password);
        ValidationUtil.requireNonEmpty(firstName, "First name");
        ValidationUtil.requireNonEmpty(lastName, "Last name");
        ValidationUtil.requireNonEmpty(designation, "Designation");
        ValidationUtil.validatePhone(phone);

        if (userDAO.findByUsername(username).isPresent()) {
            throw new ValidationException("username", "Username '" + username + "' is already registered.");
        }

        if (userDAO.findByEmail(email).isPresent()) {
            throw new ValidationException("email", "Corporate email '" + email + "' is already in use.");
        }

        if (joiningDate == null) {
            joiningDate = LocalDate.now();
        }

        if (salary == null || salary.compareTo(BigDecimal.ZERO) < 0) {
            salary = BigDecimal.ZERO;
        }

        Connection conn = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false); // Transaction boundary

            // 1. Create User
            User newUser = new User();
            newUser.setUsername(username.trim());
            newUser.setEmail(email.trim().toLowerCase());
            newUser.setPasswordHash(PasswordUtil.hashPassword(password));
            newUser.setRole(User.ROLE_EMPLOYEE);
            newUser.setActive(true);
            int userId = userDAO.insert(newUser, conn);

            // 2. Generate Employee Code
            String empCode = "EMP-" + (1000 + userId);

            // 3. Create Employee Profile
            Employee emp = new Employee();
            emp.setUserId(userId);
            emp.setEmployeeCode(empCode);
            emp.setFirstName(firstName.trim());
            emp.setLastName(lastName.trim());
            emp.setEmail(email.trim().toLowerCase());
            emp.setPhone(phone != null ? phone.trim() : null);
            emp.setDepartmentId(departmentId);
            emp.setDesignation(designation.trim());
            emp.setJoiningDate(joiningDate);
            emp.setSalary(salary);
            emp.setStatus(Employee.Status.ACTIVE);
            int employeeId = employeeDAO.insert(emp, conn);

            // 4. Initialize Leave Balances for all active leave types for current year
            int currentYear = LocalDate.now().getYear();
            List<LeaveType> types = leaveTypeDAO.findAll();
            for (LeaveType lt : types) {
                LeaveBalance lb = new LeaveBalance();
                lb.setEmployeeId(employeeId);
                lb.setLeaveTypeId(lt.getId());
                lb.setYear(currentYear);
                lb.setTotalAllocated(lt.getDefaultDaysPerYear());
                lb.setUsedDays(0.0);
                lb.setPendingDays(0.0);
                leaveBalanceDAO.insert(lb, conn);
            }

            // 5. Audit Log
            AuditLog log = new AuditLog("EMPLOYEE", employeeId, "REGISTER", userId, emp.getFullName(),
                    "New employee registered with code " + empCode);
            auditLogDAO.insert(log, conn);

            conn.commit();
            emp.setId(employeeId);
            return emp;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            throw new DatabaseException("Failed to register employee: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /**
     * Changes user password verifying the existing password.
     */
    public void changePassword(int userId, String oldPassword, String newPassword) {
        ValidationUtil.requireNonEmpty(oldPassword, "Current password");
        ValidationUtil.validatePassword(newPassword);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new AuthenticationException("User account not found."));

        if (!PasswordUtil.checkPassword(oldPassword, user.getPasswordHash())) {
            throw new ValidationException("oldPassword", "Current password does not match.");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        userDAO.updatePassword(userId, newHash);

        AuditLog log = new AuditLog("USER", userId, "CHANGE_PASSWORD", userId, user.getUsername(), "Password changed successfully.");
        auditLogDAO.insert(log, null);
    }
}
