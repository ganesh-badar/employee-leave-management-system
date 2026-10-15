package com.elms.filter;

import com.elms.dao.EmployeeDAO;
import com.elms.dao.impl.EmployeeDAOImpl;
import com.elms.model.Employee;
import com.elms.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

/**
 * Enterprise Authentication & Authorization Security Filter.
 * Guards internal routes, enforces role-based access control, and attaches context objects.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = "/*")
public class AuthenticationFilter implements Filter {

    private final EmployeeDAO employeeDAO = new EmployeeDAOImpl();

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI().substring(request.getContextPath().length());

        // Allow static resources, auth endpoints, public policy pages, and favicon
        if (path.startsWith("/static/") ||
                path.equals("/favicon.ico") ||
                path.equals("/favicon.svg") ||
                path.equals("/login") ||
                path.equals("/register") ||
                path.equals("/logout") ||
                path.equals("/privacy-policy") ||
                path.equals("/terms-and-conditions") ||
                path.startsWith("/error/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("SESSION_USER") : null;

        if (user == null) {
            // Unauthenticated user requesting protected resource
            String target = path;
            if (request.getQueryString() != null) {
                target += "?" + request.getQueryString();
            }
            response.sendRedirect(request.getContextPath() + "/login?redirect=" + java.net.URLEncoder.encode(target, "UTF-8"));
            return;
        }

        // Attach user and associated employee to request scope
        request.setAttribute("currentUser", user);
        Optional<Employee> empOpt = employeeDAO.findByUserId(user.getId());
        empOpt.ifPresent(employee -> request.setAttribute("currentEmployee", employee));

        // Enforce admin authorization on /admin/*
        if (path.startsWith("/admin")) {
            if (!user.isAdmin()) {
                request.setAttribute("errorMessage", "Access Denied: Administrative privileges required.");
                request.getRequestDispatcher("/WEB-INF/views/error/403.jsp").forward(request, response);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
