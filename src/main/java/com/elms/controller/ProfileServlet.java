package com.elms.controller;

import com.elms.exception.ValidationException;
import com.elms.model.Employee;
import com.elms.model.User;
import com.elms.service.AuthService;
import com.elms.service.EmployeeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller for user profile self-service and security credential updates.
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private final EmployeeService employeeService = new EmployeeService();
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("SESSION_USER") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Optional<Employee> empOpt = employeeService.getEmployeeByUserId(user.getId());
        empOpt.ifPresent(employee -> req.setAttribute("employee", employee));
        req.setAttribute("user", user);

        req.getRequestDispatcher("/WEB-INF/views/employee/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("SESSION_USER") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        try {
            if ("changePassword".equalsIgnoreCase(action)) {
                String oldPass = req.getParameter("oldPassword");
                String newPass = req.getParameter("newPassword");
                String confirmPass = req.getParameter("confirmPassword");

                if (newPass == null || !newPass.equals(confirmPass)) {
                    throw new ValidationException("confirmPassword", "New passwords do not match.");
                }

                authService.changePassword(user.getId(), oldPass, newPass);
                session.setAttribute("flashSuccess", "Password updated successfully.");
            } else if ("updateContact".equalsIgnoreCase(action)) {
                Optional<Employee> empOpt = employeeService.getEmployeeByUserId(user.getId());
                if (empOpt.isPresent()) {
                    Employee emp = empOpt.get();
                    String phone = req.getParameter("phone");
                    emp.setPhone(phone);
                    employeeService.updateEmployee(emp, user.getId(), user.getUsername());
                    session.setAttribute("flashSuccess", "Contact details updated.");
                }
            }
        } catch (Exception e) {
            session.setAttribute("flashError", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/profile");
    }
}
