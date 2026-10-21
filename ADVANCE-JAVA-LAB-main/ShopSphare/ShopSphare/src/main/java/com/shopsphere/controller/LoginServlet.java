package com.shopsphere.controller;

import com.shopsphere.dao.UserDAO;
import com.shopsphere.model.User;
import com.shopsphere.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO;

    public LoginServlet() {
        this.userDAO = new UserDAO();
    }

    public LoginServlet(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        String redirect = req.getParameter("redirect");
        req.setAttribute("redirectUrl", redirect);
        
        // Forward to JSP or render fallback form if view not present
        if (getServletContext().getResource("/WEB-INF/views/login.jsp") != null) {
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        } else {
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("<!DOCTYPE html><html><head><title>ShopSphere - Login</title></head><body>" +
                "<h2>Login to ShopSphere</h2>" +
                "<form method='POST' action='" + req.getContextPath() + "/login'>" +
                "<input type='hidden' name='redirect' value='" + (redirect != null ? redirect : "") + "'/>" +
                "<p><label>Email: <input type='email' name='email' required/></label></p>" +
                "<p><label>Password: <input type='password' name='password' required/></label></p>" +
                "<p><button type='submit'>Sign In</button></p>" +
                "</form>" +
                "<p>Don't have an account? <a href='" + req.getContextPath() + "/register'>Register here</a></p>" +
                "</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String redirect = req.getParameter("redirect");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            req.setAttribute("error", "Email and password are required.");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email and password are required.");
            return;
        }

        try {
            Optional<User> userOpt = userDAO.findByEmail(email.trim());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if ("active".equalsIgnoreCase(user.getStatus()) && 
                    PasswordUtil.verifyPassword(password, user.getPasswordHash())) {

                    // Successful authentication: create session & prevent session fixation
                    HttpSession session = req.getSession(true);
                    req.changeSessionId();
                    session.setAttribute("user", user);

                    if (redirect != null && !redirect.trim().isEmpty() && !redirect.startsWith("http")) {
                        resp.sendRedirect(redirect);
                    } else if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                        resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/products");
                    }
                    return;
                }
            }

            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            req.setAttribute("error", "Invalid email or password.");
            doGet(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Database error during authentication", e);
        }
    }
}

