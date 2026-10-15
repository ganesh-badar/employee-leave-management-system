package com.elms.controller;

import com.elms.model.Department;
import com.elms.model.LeaveRequest;
import com.elms.model.User;
import com.elms.service.DepartmentService;
import com.elms.service.LeaveService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Administrative Leave Approval, Rejection, and Department Conflict Inspection.
 */
@WebServlet(name = "AdminLeaveServlet", urlPatterns = {"/admin/leaves", "/admin/leaves/approve", "/admin/leaves/reject"})
public class AdminLeaveServlet extends HttpServlet {

    private final LeaveService leaveService = new LeaveService();
    private final DepartmentService departmentService = new DepartmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String statusStr = req.getParameter("status");
        String departmentIdStr = req.getParameter("departmentId");

        LeaveRequest.Status status = null;
        if (statusStr != null && !statusStr.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusStr)) {
            try {
                status = LeaveRequest.Status.valueOf(statusStr.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        Integer departmentId = null;
        if (departmentIdStr != null && !departmentIdStr.trim().isEmpty()) {
            try {
                departmentId = Integer.parseInt(departmentIdStr.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        List<LeaveRequest> requests = leaveService.getAllRequests(status, departmentId, null);
        List<Department> departments = departmentService.getAllDepartments();

        // Calculate potential departmental conflicts for pending leaves
        Map<Integer, List<LeaveRequest>> conflictsMap = new HashMap<>();
        for (LeaveRequest r : requests) {
            if (r.isPending()) {
                List<LeaveRequest> conflicts = leaveService.checkDepartmentConflicts(r);
                if (!conflicts.isEmpty()) {
                    conflictsMap.put(r.getId(), conflicts);
                }
            }
        }

        req.setAttribute("requests", requests);
        req.setAttribute("departments", departments);
        req.setAttribute("conflictsMap", conflictsMap);
        req.setAttribute("currentStatus", statusStr != null ? statusStr : "ALL");
        req.setAttribute("currentDept", departmentId);

        req.getRequestDispatcher("/WEB-INF/views/admin/leave-requests.jsp").forward(req, resp);
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

        String requestIdStr = req.getParameter("requestId");
        String remarks = req.getParameter("adminRemarks");

        try {
            int requestId = Integer.parseInt(requestIdStr.trim());

            if ("/admin/leaves/approve".equals(path)) {
                leaveService.approveLeave(requestId, remarks, admin.getId(), admin.getUsername());
                session.setAttribute("flashSuccess", "Leave request #" + requestId + " has been successfully approved.");
            } else if ("/admin/leaves/reject".equals(path)) {
                leaveService.rejectLeave(requestId, remarks, admin.getId(), admin.getUsername());
                session.setAttribute("flashSuccess", "Leave request #" + requestId + " has been rejected.");
            }
        } catch (Exception e) {
            session.setAttribute("flashError", "Failed to update leave status: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/leaves");
    }
}
