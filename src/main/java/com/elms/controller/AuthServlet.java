package com.elms.controller;

import com.elms.exception.AuthenticationException;
import com.elms.exception.ValidationException;
import com.elms.model.Department;
import com.elms.model.Employee;
import com.elms.model.User;
import com.elms.service.AuthService;
import com.elms.service.DepartmentService;
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
 * Controller handling user login, registration, and logout workflows.
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {

    private final AuthService authService = new AuthService();
    private final DepartmentService departmentService = new DepartmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/login?logout=true");
            return;
        }

        // Redirect already logged-in users away from login/register to dashboard
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("SESSION_USER") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        if ("/register".equals(path)) {
            List<Department> departments = departmentService.getAllDepartments();
            req.setAttribute("departments", departments);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        // Default: /login
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/login".equals(path)) {
            handleLogin(req, resp);
        } else if ("/register".equals(path)) {
            handleRegister(req, resp);
        } else if ("/logout".equals(path)) {
            doGet(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String usernameOrEmail = req.getParameter("usernameOrEmail");
        String password = req.getParameter("password");
        String redirect = req.getParameter("redirect");

        try {
            User user = authService.authenticate(usernameOrEmail, password);
            HttpSession session = req.getSession(true);
            session.setAttribute("SESSION_USER", user);
            session.setMaxInactiveInterval(1800); // 30 minutes session timeout

            if (redirect != null && !redirect.trim().isEmpty() && !redirect.contains("login") && !redirect.contains("register")) {
                resp.sendRedirect(req.getContextPath() + redirect);
            } else {
                resp.sendRedirect(req.getContextPath() + "/dashboard");
            }
        } catch (AuthenticationException | ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("usernameOrEmail", usernameOrEmail);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "An unexpected authentication error occurred. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String phone = req.getParameter("phone");
        String departmentIdStr = req.getParameter("departmentId");
        String designation = req.getParameter("designation");
        String joiningDateStr = req.getParameter("joiningDate");

        try {
            if (password == null || !password.equals(confirmPassword)) {
                throw new ValidationException("confirmPassword", "Passwords do not match.");
            }

            Integer departmentId = null;
            if (departmentIdStr != null && !departmentIdStr.trim().isEmpty()) {
                departmentId = Integer.parseInt(departmentIdStr.trim());
            }

            LocalDate joiningDate = LocalDate.now();
            if (joiningDateStr != null && !joiningDateStr.trim().isEmpty()) {
                joiningDate = LocalDate.parse(joiningDateStr.trim());
            }

            Employee employee = authService.registerEmployee(
                    username, email, password, firstName, lastName, phone,
                    departmentId, designation, joiningDate, BigDecimal.ZERO
            );

            // Automatically log in the registered user
            User user = authService.authenticate(username, password);
            HttpSession session = req.getSession(true);
            session.setAttribute("SESSION_USER", user);
            session.setAttribute("flashSuccess", "Registration successful. Welcome to the portal!");

            resp.sendRedirect(req.getContextPath() + "/dashboard");

        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("departments", departmentService.getAllDepartments());
            req.setAttribute("form_username", username);
            req.setAttribute("form_email", email);
            req.setAttribute("form_firstName", firstName);
            req.setAttribute("form_lastName", lastName);
            req.setAttribute("form_phone", phone);
            req.setAttribute("form_departmentId", departmentIdStr);
            req.setAttribute("form_designation", designation);
            req.setAttribute("form_joiningDate", joiningDateStr);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Registration failed: " + e.getMessage());
            req.setAttribute("departments", departmentService.getAllDepartments());
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        }
    }
}
