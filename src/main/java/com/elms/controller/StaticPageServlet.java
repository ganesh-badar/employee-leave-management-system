package com.elms.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller serving formal enterprise compliance pages (Privacy Policy, Terms of Service).
 */
@WebServlet(name = "StaticPageServlet", urlPatterns = {"/privacy-policy", "/terms-and-conditions"})
public class StaticPageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/privacy-policy".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/pages/privacy-policy.jsp").forward(req, resp);
        } else if ("/terms-and-conditions".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/pages/terms-and-conditions.jsp").forward(req, resp);
        }
    }
}
