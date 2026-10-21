package com.shopsphere.controller;

import com.shopsphere.model.User;
import com.shopsphere.service.CheckoutRequest;
import com.shopsphere.service.CheckoutResult;
import com.shopsphere.service.CheckoutService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

    private final CheckoutService checkoutService;

    public CheckoutServlet() {
        this.checkoutService = new CheckoutService();
    }

    public CheckoutServlet(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" + req.getRequestURI());
            return;
        }

        if (getServletContext().getResource("/WEB-INF/views/checkout.jsp") != null) {
            req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
        } else {
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("<!DOCTYPE html><html><head><title>ShopSphere - Checkout</title></head><body>" +
                "<h2>Checkout</h2>" +
                "<p>Logged in as: " + user.getEmail() + "</p>" +
                "<form method='POST' action='" + req.getContextPath() + "/checkout'>" +
                "<p><label>Shipping Address ID: <input type='number' name='shippingAddressId' required/></label></p>" +
                "<p><label>Billing Address ID: <input type='number' name='billingAddressId'/></label></p>" +
                "<p><label>Coupon Code (Optional): <input type='text' name='couponCode'/></label></p>" +
                "<p><label>Payment Method: " +
                "<select name='paymentMethod'>" +
                "<option value='credit_card'>Credit Card</option>" +
                "<option value='paypal'>PayPal</option>" +
                "<option value='cod'>Cash on Delivery</option>" +
                "</select></label></p>" +
                "<p><label>Order Notes: <textarea name='notes'></textarea></label></p>" +
                "<button type='submit'>Place Order (Server-Verified Calculation)</button>" +
                "</form></body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User session expired.");
            return;
        }

        String shippingAddressStr = req.getParameter("shippingAddressId");
        String billingAddressStr = req.getParameter("billingAddressId");
        String couponCode = req.getParameter("couponCode");
        String paymentMethod = req.getParameter("paymentMethod");
        String notes = req.getParameter("notes");

        Long shippingAddressId = (shippingAddressStr != null && !shippingAddressStr.isBlank()) 
            ? Long.parseLong(shippingAddressStr) : null;
        Long billingAddressId = (billingAddressStr != null && !billingAddressStr.isBlank()) 
            ? Long.parseLong(billingAddressStr) : shippingAddressId;

        CheckoutRequest checkoutReq = new CheckoutRequest();
        checkoutReq.setUserId(user.getUserId());
        checkoutReq.setShippingAddressId(shippingAddressId);
        checkoutReq.setBillingAddressId(billingAddressId);
        checkoutReq.setCouponCode(couponCode);
        checkoutReq.setPaymentMethod(paymentMethod != null ? paymentMethod : "credit_card");
        checkoutReq.setNotes(notes);

        try {
            // Strictly calculate all pricing, tax, discounts and order totals server-side
            CheckoutResult result = checkoutService.processCheckout(checkoutReq);

            req.setAttribute("checkoutResult", result);
            req.setAttribute("order", result.getOrder());

            if (getServletContext().getResource("/WEB-INF/views/order-confirmation.jsp") != null) {
                req.getRequestDispatcher("/WEB-INF/views/order-confirmation.jsp").forward(req, resp);
            } else {
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().println("<!DOCTYPE html><html><head><title>Order Confirmed</title></head><body>" +
                    "<h2>Thank You! Your Order has been Placed.</h2>" +
                    "<p>Order Number: <strong>" + result.getOrder().getOrderNumber() + "</strong></p>" +
                    "<p>Subtotal: $" + result.getSubtotal() + "</p>" +
                    "<p>Discount: -$" + result.getDiscountAmount() + "</p>" +
                    "<p>Shipping: $" + result.getShippingFee() + "</p>" +
                    "<p>Tax: $" + result.getTaxAmount() + "</p>" +
                    "<p><strong>Total Charged: $" + result.getTotalAmount() + "</strong></p>" +
                    "<p>Transaction ID: " + result.getPayment().getTransactionId() + "</p>" +
                    "<a href='" + req.getContextPath() + "/products'>Continue Shopping</a>" +
                    "</body></html>");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            req.setAttribute("errorMessage", e.getMessage());
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("<h3>Checkout Error: " + e.getMessage() + "</h3><a href='checkout'>Try again</a>");
        } catch (SQLException e) {
            throw new ServletException("Database error during order checkout", e);
        }
    }
}

