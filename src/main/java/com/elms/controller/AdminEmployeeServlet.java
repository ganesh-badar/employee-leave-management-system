package com.elms.controller;

import com.elms.exception.ValidationException;
import com.elms.model.Department;
import com.elms.model.Employee;
import com.elms.model.LeaveBalance;
import com.elms.model.LeaveType;
import com.elms.model.User;
import com.elms.service.AuthService;
import com.elms.service.DepartmentService;
import com.elms.service.EmployeeService;
import com.elms.service.LeaveService;
import com.elms.util.DateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Administrative controller for comprehensive Employee CRUD operations and leave quota allocation.
 */
@WebServlet(name = "AdminEmployeeServlet", urlPatterns = {
        "/admin/employees",
        "/admin/employees/new",
        "/admin/employees/edit",
        "/admin/employees/status",
        "/admin/employees/quota"
})
public class AdminEmployeeServlet extends HttpServlet {

    private final EmployeeService employeeService = new EmployeeService();
    private final AuthService authService = new AuthService();
    private final DepartmentService departmentService = new DepartmentService();
    private final LeaveService leaveService = new LeaveService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/admin/employees/new".equals(path)) {
            req.setAttribute("isNew", true);
            req.setAttribute("departments", departmentService.getAllDepartments());
            req.getRequestDispatcher("/WEB-INF/views/admin/employee-form.jsp").forward(req, resp);
            return;
        }

        if ("/admin/employees/edit".equals(path)) {
            String idStr = req.getParameter("id");
            try {
                int empId = Integer.parseInt(idStr.trim());
                Employee emp = employeeService.getEmployeeById(empId)
                        .orElseThrow(() -> new ValidationException("Employee not found."));
                List<LeaveBalance> balances = leaveService.getEmployeeBalances(empId, LocalDate.now().getYear());
                List<LeaveType> leaveTypes = leaveService.getAllLeaveTypes();

                req.setAttribute("employee", emp);
                req.setAttribute("balances", balances);
                req.setAttribute("leaveTypes", leaveTypes);
                req.setAttribute("departments", departmentService.getAllDepartments());
                req.setAttribute("isNew", false);
                req.getRequestDispatcher("/WEB-INF/views/admin/employee-form.jsp").forward(req, resp);
                return;
            } catch (Exception e) {
                req.getSession().setAttribute("flashError", "Invalid employee record: " + e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/admin/employees");
                return;
            }
        }

        // Default: /admin/employees directory view
        String search = req.getParameter("search");
        String deptStr = req.getParameter("departmentId");
        String status = req.getParameter("status");

        Integer departmentId = null;
        if (deptStr != null && !deptStr.trim().isEmpty()) {
            try {
                departmentId = Integer.parseInt(deptStr.trim());
            } catch (NumberFormatException ignored) {}
        }

        List<Employee> employees = employeeService.getAllEmployees(search, departmentId, status);
        List<Department> departments = departmentService.getAllDepartments();

        req.setAttribute("employees", employees);
        req.setAttribute("departments", departments);
        req.setAttribute("currentSearch", search != null ? search : "");
        req.setAttribute("currentDept", departmentId);
        req.setAttribute("currentStatus", status != null ? status : "ALL");

        req.getRequestDispatcher("/WEB-INF/views/admin/employees.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession(false);
        User admin = (session != null) ? (User) session.getAttribute("SESSION_USER") : null;

        if (admin == null || !admin.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if ("/admin/employees/new".equals(path)) {
            handleCreateEmployee(req, resp, session);
        } else if ("/admin/employees/edit".equals(path)) {
            handleUpdateEmployee(req, resp, session, admin);
        } else if ("/admin/employees/status".equals(path)) {
            handleToggleStatus(req, resp, session, admin);
        } else if ("/admin/employees/quota".equals(path)) {
            handleUpdateQuota(req, resp, session, admin);
        }
    }

    private void handleCreateEmployee(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String phone = req.getParameter("phone");
        String departmentIdStr = req.getParameter("departmentId");
        String designation = req.getParameter("designation");
        String joiningDateStr = req.getParameter("joiningDate");
        String salaryStr = req.getParameter("salary");

        try {
            Integer departmentId = (departmentIdStr != null && !departmentIdStr.trim().isEmpty())
                    ? Integer.parseInt(departmentIdStr.trim()) : null;
            LocalDate joiningDate = DateUtil.parseDate(joiningDateStr, "Joining date");
            BigDecimal salary = (salaryStr != null && !salaryStr.trim().isEmpty())
                    ? new BigDecimal(salaryStr.trim()) : BigDecimal.ZERO;

            authService.registerEmployee(username, email, password, firstName, lastName, phone,
                    departmentId, designation, joiningDate, salary);

            session.setAttribute("flashSuccess", "New employee record created successfully.");
            resp.sendRedirect(req.getContextPath() + "/admin/employees");
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("isNew", true);
            req.setAttribute("departments", departmentService.getAllDepartments());
            req.setAttribute("form_username", username);
            req.setAttribute("form_email", email);
            req.setAttribute("form_firstName", firstName);
            req.setAttribute("form_lastName", lastName);
            req.setAttribute("form_phone", phone);
            req.setAttribute("form_departmentId", departmentIdStr);
            req.setAttribute("form_designation", designation);
            req.setAttribute("form_joiningDate", joiningDateStr);
            req.setAttribute("form_salary", salaryStr);
            req.getRequestDispatcher("/WEB-INF/views/admin/employee-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Failed to add employee: " + e.getMessage());
            req.setAttribute("isNew", true);
            req.setAttribute("departments", departmentService.getAllDepartments());
            req.getRequestDispatcher("/WEB-INF/views/admin/employee-form.jsp").forward(req, resp);
        }
    }

    private void handleUpdateEmployee(HttpServletRequest req, HttpServletResponse resp, HttpSession session, User admin)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String phone = req.getParameter("phone");
        String departmentIdStr = req.getParameter("departmentId");
        String designation = req.getParameter("designation");
        String joiningDateStr = req.getParameter("joiningDate");
        String salaryStr = req.getParameter("salary");
        String statusStr = req.getParameter("status");

        try {
            int empId = Integer.parseInt(idStr.trim());
            Employee emp = employeeService.getEmployeeById(empId)
                    .orElseThrow(() -> new ValidationException("Employee not found."));

            emp.setFirstName(firstName);
            emp.setLastName(lastName);
            emp.setPhone(phone);
            emp.setDepartmentId((departmentIdStr != null && !departmentIdStr.trim().isEmpty())
                    ? Integer.parseInt(departmentIdStr.trim()) : null);
            emp.setDesignation(designation);
            if (joiningDateStr != null && !joiningDateStr.trim().isEmpty()) {
                emp.setJoiningDate(DateUtil.parseDate(joiningDateStr, "Joining Date"));
            }
            if (salaryStr != null && !salaryStr.trim().isEmpty()) {
                emp.setSalary(new BigDecimal(salaryStr.trim()));
            }
            if (statusStr != null) {
                emp.setStatus(Employee.Status.valueOf(statusStr));
            }

            employeeService.updateEmployee(emp, admin.getId(), admin.getUsername());

            session.setAttribute("flashSuccess", "Employee " + emp.getEmployeeCode() + " updated successfully.");
            resp.sendRedirect(req.getContextPath() + "/admin/employees");
        } catch (Exception e) {
            session.setAttribute("flashError", "Failed to update employee: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/employees/edit?id=" + idStr);
        }
    }

    private void handleToggleStatus(HttpServletRequest req, HttpServletResponse resp, HttpSession session, User admin)
            throws IOException {
        String empIdStr = req.getParameter("id");
        String newStatusStr = req.getParameter("status");
        try {
            int empId = Integer.parseInt(empIdStr.trim());
            Employee.Status newStatus = Employee.Status.valueOf(newStatusStr.trim());
            employeeService.toggleEmployeeStatus(empId, newStatus, admin.getId(), admin.getUsername());
            session.setAttribute("flashSuccess", "Employee status transitioned to " + newStatus);
        } catch (Exception e) {
            session.setAttribute("flashError", "Failed to update status: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin/employees");
    }

    private void handleUpdateQuota(HttpServletRequest req, HttpServletResponse resp, HttpSession session, User admin)
            throws IOException {
        String empIdStr = req.getParameter("employeeId");
        String typeIdStr = req.getParameter("leaveTypeId");
        String yearStr = req.getParameter("year");
        String quotaStr = req.getParameter("quota");

        try {
            int empId = Integer.parseInt(empIdStr.trim());
            int typeId = Integer.parseInt(typeIdStr.trim());
            int year = Integer.parseInt(yearStr.trim());
            double quota = Double.parseDouble(quotaStr.trim());

            employeeService.updateLeaveAllocation(empId, typeId, year, quota, admin.getId(), admin.getUsername());
            session.setAttribute("flashSuccess", "Leave quota updated successfully.");
            resp.sendRedirect(req.getContextPath() + "/admin/employees/edit?id=" + empId);
        } catch (Exception e) {
            session.setAttribute("flashError", "Failed to adjust leave quota: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/employees/edit?id=" + empIdStr);
        }
    }
}
