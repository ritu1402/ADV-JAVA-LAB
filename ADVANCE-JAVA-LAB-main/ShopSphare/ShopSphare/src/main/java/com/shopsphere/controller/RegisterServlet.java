package com.shopsphere.controller;

import com.shopsphere.dao.UserDAO;
import com.shopsphere.model.User;
import com.shopsphere.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO;

    public RegisterServlet() {
        this.userDAO = new UserDAO();
    }

    public RegisterServlet(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (getServletContext().getResource("/WEB-INF/views/register.jsp") != null) {
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        } else {
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("<!DOCTYPE html><html><head><title>ShopSphere - Register</title></head><body>" +
                "<h2>Create a New Account</h2>" +
                "<form method='POST' action='" + req.getContextPath() + "/register'>" +
                "<p><label>First Name: <input type='text' name='firstName' required/></label></p>" +
                "<p><label>Last Name: <input type='text' name='lastName' required/></label></p>" +
                "<p><label>Email: <input type='email' name='email' required/></label></p>" +
                "<p><label>Password: <input type='password' name='password' minlength='8' required/></label></p>" +
                "<p><label>Phone: <input type='tel' name='phoneNumber'/></label></p>" +
                "<p><button type='submit'>Register</button></p>" +
                "</form>" +
                "<p>Already registered? <a href='" + req.getContextPath() + "/login'>Login here</a></p>" +
                "</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String phoneNumber = req.getParameter("phoneNumber");

        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.length() < 8) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "All fields are required. Password must be at least 8 characters.");
            return;
        }

        try {
            Optional<User> existingUser = userDAO.findByEmail(email.trim());
            if (existingUser.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                req.setAttribute("error", "Email is already registered.");
                doGet(req, resp);
                return;
            }

            // Securely hash password using PBKDF2
            String passwordHash = PasswordUtil.hashPassword(password);

            User newUser = new User();
            newUser.setFirstName(firstName.trim());
            newUser.setLastName(lastName.trim());
            newUser.setEmail(email.trim().toLowerCase());
            newUser.setPasswordHash(passwordHash);
            newUser.setPhoneNumber(phoneNumber != null ? phoneNumber.trim() : null);
            newUser.setRole("customer");
            newUser.setStatus("active");

            userDAO.create(newUser);

            // Redirect to login with confirmation
            resp.sendRedirect(req.getContextPath() + "/login?registered=true");
        } catch (SQLException e) {
            throw new ServletException("Database error during registration", e);
        }
    }
}

