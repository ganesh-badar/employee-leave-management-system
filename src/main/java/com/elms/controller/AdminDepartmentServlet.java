package com.elms.controller;

import com.elms.model.Department;
import com.elms.model.User;
import com.elms.service.DepartmentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller for managing corporate departments.
 */
@WebServlet(name = "AdminDepartmentServlet", urlPatterns = {"/admin/departments"})
public class AdminDepartmentServlet extends HttpServlet {

    private final DepartmentService departmentService = new DepartmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Department> departments = departmentService.getAllDepartments();
        req.setAttribute("departments", departments);
        req.getRequestDispatcher("/WEB-INF/views/admin/departments.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User admin = (session != null) ? (User) session.getAttribute("SESSION_USER") : null;

        if (admin == null || !admin.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = req.getParameter("action");
        try {
            if ("create".equalsIgnoreCase(action)) {
                String code = req.getParameter("departmentCode");
                String name = req.getParameter("departmentName");
                String desc = req.getParameter("description");
                departmentService.createDepartment(code, name, desc);
                session.setAttribute("flashSuccess", "Department created successfully.");
            } else if ("update".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                String name = req.getParameter("departmentName");
                String desc = req.getParameter("description");
                departmentService.updateDepartment(id, name, desc);
                session.setAttribute("flashSuccess", "Department updated successfully.");
            } else if ("delete".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                departmentService.deleteDepartment(id);
                session.setAttribute("flashSuccess", "Department deleted.");
            }
        } catch (Exception e) {
            session.setAttribute("flashError", "Action failed: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/departments");
    }
}
