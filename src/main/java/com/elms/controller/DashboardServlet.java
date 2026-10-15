package com.elms.controller;

import com.elms.model.Employee;
import com.elms.model.LeaveBalance;
import com.elms.model.LeaveRequest;
import com.elms.model.User;
import com.elms.service.EmployeeService;
import com.elms.service.LeaveService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Main dashboard routing controller. Dispatches to Admin or Employee view based on role.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard", ""})
public class DashboardServlet extends HttpServlet {

    private final EmployeeService employeeService = new EmployeeService();
    private final LeaveService leaveService = new LeaveService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("SESSION_USER") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (user.isAdmin()) {
            renderAdminDashboard(req, resp, user);
        } else {
            renderEmployeeDashboard(req, resp, user);
        }
    }

    private void renderAdminDashboard(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        Map<String, Object> metrics = employeeService.getDashboardMetrics();
        List<LeaveRequest> pendingRequests = leaveService.getAllRequests(LeaveRequest.Status.PENDING, null, null);
        var recentLogs = leaveService.getRecentAuditLogs(10);

        req.setAttribute("metrics", metrics);
        req.setAttribute("pendingRequests", pendingRequests);
        req.setAttribute("recentLogs", recentLogs);

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }

    private void renderEmployeeDashboard(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        Optional<Employee> empOpt = employeeService.getEmployeeByUserId(user.getId());
        if (empOpt.isEmpty()) {
            req.setAttribute("errorMessage", "Employee profile record not found.");
            req.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(req, resp);
            return;
        }

        Employee emp = empOpt.get();
        int currentYear = LocalDate.now().getYear();
        List<LeaveBalance> balances = leaveService.getEmployeeBalances(emp.getId(), currentYear);
        List<LeaveRequest> leaves = leaveService.getEmployeeLeaves(emp.getId());

        req.setAttribute("employee", emp);
        req.setAttribute("balances", balances);
        req.setAttribute("leaves", leaves);
        req.setAttribute("currentYear", currentYear);

        req.getRequestDispatcher("/WEB-INF/views/employee/dashboard.jsp").forward(req, resp);
    }
}
