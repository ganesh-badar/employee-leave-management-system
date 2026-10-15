package com.elms.controller;

import com.elms.exception.ValidationException;
import com.elms.model.Employee;
import com.elms.model.LeaveBalance;
import com.elms.model.LeaveRequest;
import com.elms.model.LeaveType;
import com.elms.model.User;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller managing employee-facing leave applications, balance inquiry, and cancellation.
 */
@WebServlet(name = "LeaveServlet", urlPatterns = {"/leave/apply", "/leave/my-leaves", "/leave/cancel"})
public class LeaveServlet extends HttpServlet {

    private final LeaveService leaveService = new LeaveService();
    private final EmployeeService employeeService = new EmployeeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        Employee emp = getAuthenticatedEmployee(req, resp);
        if (emp == null) return;

        if ("/leave/apply".equals(path)) {
            List<LeaveType> types = leaveService.getAllLeaveTypes();
            List<LeaveBalance> balances = leaveService.getEmployeeBalances(emp.getId(), LocalDate.now().getYear());
            req.setAttribute("types", types);
            req.setAttribute("balances", balances);
            req.setAttribute("employee", emp);
            req.getRequestDispatcher("/WEB-INF/views/employee/apply-leave.jsp").forward(req, resp);
        } else if ("/leave/my-leaves".equals(path)) {
            List<LeaveRequest> requests = leaveService.getEmployeeLeaves(emp.getId());
            List<LeaveBalance> balances = leaveService.getEmployeeBalances(emp.getId(), LocalDate.now().getYear());
            req.setAttribute("requests", requests);
            req.setAttribute("balances", balances);
            req.setAttribute("employee", emp);
            req.getRequestDispatcher("/WEB-INF/views/employee/my-leaves.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        Employee emp = getAuthenticatedEmployee(req, resp);
        if (emp == null) return;

        if ("/leave/apply".equals(path)) {
            handleApplyLeave(req, resp, emp);
        } else if ("/leave/cancel".equals(path)) {
            handleCancelLeave(req, resp, emp);
        }
    }

    private void handleApplyLeave(HttpServletRequest req, HttpServletResponse resp, Employee emp)
            throws ServletException, IOException {
        String leaveTypeIdStr = req.getParameter("leaveTypeId");
        String startDateStr = req.getParameter("startDate");
        String endDateStr = req.getParameter("endDate");
        String reason = req.getParameter("reason");
        String emergencyContact = req.getParameter("emergencyContact");

        try {
            if (leaveTypeIdStr == null || leaveTypeIdStr.trim().isEmpty()) {
                throw new ValidationException("leaveTypeId", "Please select a leave category.");
            }
            int leaveTypeId = Integer.parseInt(leaveTypeIdStr.trim());
            LocalDate startDate = DateUtil.parseDate(startDateStr, "Start Date");
            LocalDate endDate = DateUtil.parseDate(endDateStr, "End Date");

            leaveService.applyForLeave(emp.getId(), leaveTypeId, startDate, endDate, reason, emergencyContact);

            req.getSession().setAttribute("flashSuccess", "Leave request submitted successfully for administrative review.");
            resp.sendRedirect(req.getContextPath() + "/leave/my-leaves");

        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("types", leaveService.getAllLeaveTypes());
            req.setAttribute("balances", leaveService.getEmployeeBalances(emp.getId(), LocalDate.now().getYear()));
            req.setAttribute("employee", emp);
            req.setAttribute("form_leaveTypeId", leaveTypeIdStr);
            req.setAttribute("form_startDate", startDateStr);
            req.setAttribute("form_endDate", endDateStr);
            req.setAttribute("form_reason", reason);
            req.setAttribute("form_emergencyContact", emergencyContact);
            req.getRequestDispatcher("/WEB-INF/views/employee/apply-leave.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error processing leave request: " + e.getMessage());
            req.setAttribute("types", leaveService.getAllLeaveTypes());
            req.setAttribute("balances", leaveService.getEmployeeBalances(emp.getId(), LocalDate.now().getYear()));
            req.setAttribute("employee", emp);
            req.getRequestDispatcher("/WEB-INF/views/employee/apply-leave.jsp").forward(req, resp);
        }
    }

    private void handleCancelLeave(HttpServletRequest req, HttpServletResponse resp, Employee emp)
            throws IOException {
        String requestIdStr = req.getParameter("requestId");
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("SESSION_USER") : null;

        try {
            int requestId = Integer.parseInt(requestIdStr.trim());
            leaveService.cancelLeave(requestId, emp.getId(), emp.getFullName(), user != null ? user.getId() : 0);
            session.setAttribute("flashSuccess", "Leave request has been successfully cancelled.");
        } catch (Exception e) {
            session.setAttribute("flashError", "Failed to cancel request: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/leave/my-leaves");
    }

    private Employee getAuthenticatedEmployee(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("SESSION_USER") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        Optional<Employee> empOpt = employeeService.getEmployeeByUserId(user.getId());
        if (empOpt.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        return empOpt.get();
    }
}
