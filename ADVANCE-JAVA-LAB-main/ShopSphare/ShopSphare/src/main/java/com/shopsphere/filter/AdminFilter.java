package com.shopsphere.filter;

import com.shopsphere.model.User;

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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebFilter(filterName = "AdminFilter", urlPatterns = "/admin/*")
public class AdminFilter implements Filter {

    private static final String ADMIN_ROLE = "ADMIN";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // 1. If not authenticated at all, redirect to login
        if (user == null) {
            String requestUri = httpRequest.getRequestURI();
            String queryString = httpRequest.getQueryString();
            String redirectTarget = (queryString != null) ? (requestUri + "?" + queryString) : requestUri;
            String encodedRedirect = URLEncoder.encode(redirectTarget, StandardCharsets.UTF_8);

            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?redirect=" + encodedRedirect);
            return;
        }

        // 2. If authenticated, verify the user possesses the 'ADMIN' role
        if (!ADMIN_ROLE.equalsIgnoreCase(user.getRole())) {
            httpResponse.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "Access Denied: You do not possess administrator permissions for this resource."
            );
            return;
        }

        // 3. Authorized admin user: proceed with request
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}

