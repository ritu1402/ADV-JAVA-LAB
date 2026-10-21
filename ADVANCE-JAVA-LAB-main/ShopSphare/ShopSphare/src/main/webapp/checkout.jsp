<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Secure Checkout - ShopSphere" scope="request" />
<jsp:include page="/WEB-INF/fragments/header.jsp" />
<jsp:include page="/WEB-INF/fragments/navbar.jsp" />

<main class="py-5">
    <div class="container">
        <!-- Breadcrumb / Header -->
        <div class="mb-4">
            <h2 class="fw-bold"><i class="bi bi-shield-check me-2 text-success"></i>Secure Checkout</h2>
            <p class="text-muted">Finalize your delivery and payment details. All totals are calculated securely on the server.</p>
        </div>

        <!-- Error Alert if any -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                <c:out value="${errorMessage}" />
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <form method="POST" action="<c:url value='/checkout' />" id="checkoutForm">
            <div class="row g-4">
                <!-- Left Column: Delivery & Payment Details -->
                <div class="col-lg-8">
                    <!-- 1. Customer Verification Card -->
                    <div class="card border-0 shadow-sm rounded-3 mb-4 p-4">
                        <h5 class="fw-bold mb-3 d-flex align-items-center gap-2">
                            <i class="bi bi-person-check text-primary"></i> 1. Contact Information
                        </h5>
                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label small text-muted">Customer Name</label>
                                <input type="text" class="form-control" 
                                       value="<c:out value='${sessionScope.user.firstName} ${sessionScope.user.lastName}' />" readonly>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small text-muted">Account Email</label>
                                <input type="email" class="form-control" 
                                       value="<c:out value='${sessionScope.user.email}' />" readonly>
                            </div>
                        </div>
                    </div>

                    <!-- 2. Shipping Address Card -->
                    <div class="card border-0 shadow-sm rounded-3 mb-4 p-4">
                        <h5 class="fw-bold mb-3 d-flex align-items-center gap-2">
                            <i class="bi bi-geo-alt text-primary"></i> 2. Delivery Address
                        </h5>

                        <c:choose>
                            <c:when test="${not empty userAddresses}">
                                <div class="mb-3">
                                    <label class="form-label small fw-semibold">Choose Saved Address</label>
                                    <c:forEach items="${userAddresses}" var="addr">
                                        <div class="form-check border rounded p-3 mb-2">
                                            <input class="form-check-input" type="radio" name="shippingAddressId" 
                                                   id="addr_${addr.addressId}" value="${addr.addressId}" 
                                                   ${addr.isDefault ? 'checked' : ''} required>
                                            <label class="form-check-label w-100" for="addr_${addr.addressId}">
                                                <div class="fw-semibold"><c:out value="${addr.recipientName}" /> (${addr.addressType})</div>
                                                <small class="text-muted">
                                                    <c:out value="${addr.streetAddressLine1}" />, <c:out value="${addr.city}" />, 
                                                    <c:out value="${addr.state}" /> <c:out value="${addr.postalCode}" />
                                                </small>
                                            </label>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <!-- Direct Address ID / Manual Address Option -->
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label class="form-label small fw-semibold">Shipping Address Reference ID</label>
                                        <input type="number" name="shippingAddressId" class="form-control" 
                                               placeholder="e.g. 1" value="1" required>
                                        <small class="text-muted">Using default account address reference.</small>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label small fw-semibold">Billing Address ID (Optional)</label>
                                        <input type="number" name="billingAddressId" class="form-control" 
                                               placeholder="Same as shipping if blank">
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- 3. Payment Method Card -->
                    <div class="card border-0 shadow-sm rounded-3 mb-4 p-4">
                        <h5 class="fw-bold mb-3 d-flex align-items-center gap-2">
                            <i class="bi bi-credit-card text-primary"></i> 3. Payment Method
                        </h5>

                        <div class="row g-3">
                            <div class="col-md-4">
                                <div class="border rounded p-3 text-center h-100">
                                    <input class="form-check-input mb-2" type="radio" name="paymentMethod" 
                                           id="payCard" value="credit_card" checked>
                                    <label class="form-check-label d-block fw-semibold" for="payCard">
                                        <i class="bi bi-credit-card-2-front fs-4 d-block text-primary mb-1"></i>
                                        Credit Card
                                    </label>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="border rounded p-3 text-center h-100">
                                    <input class="form-check-input mb-2" type="radio" name="paymentMethod" 
                                           id="payPaypal" value="paypal">
                                    <label class="form-check-label d-block fw-semibold" for="payPaypal">
                                        <i class="bi bi-paypal fs-4 d-block text-primary mb-1"></i>
                                        PayPal
                                    </label>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="border rounded p-3 text-center h-100">
                                    <input class="form-check-input mb-2" type="radio" name="paymentMethod" 
                                           id="payCod" value="cod">
                                    <label class="form-check-label d-block fw-semibold" for="payCod">
                                        <i class="bi bi-cash-stack fs-4 d-block text-success mb-1"></i>
                                        Cash on Delivery
                                    </label>
                                </div>
                            </div>
                        </div>

                        <!-- Special Instructions -->
                        <div class="mt-4">
                            <label class="form-label small fw-semibold text-secondary">Delivery Notes / Special Instructions</label>
                            <textarea name="notes" class="form-control" rows="2" 
                                      placeholder="Leave package at the door, gate code, etc."></textarea>
                        </div>
                    </div>
                </div>

                <!-- Right Column: Sticky Server-Side Calculation Summary -->
                <div class="col-lg-4">
                    <div class="cart-summary-card shadow-sm sticky-top" style="top: 90px;">
                        <h5 class="fw-bold mb-3 border-bottom pb-2">Order Review</h5>

                        <!-- Coupon Input Voucher -->
                        <div class="mb-4">
                            <label class="form-label small fw-semibold text-secondary">Have a Coupon?</label>
                            <div class="input-group">
                                <input type="text" name="couponCode" class="form-control form-control-sm text-uppercase font-monospace" 
                                       placeholder="e.g. SAVE10" value="<c:out value='${param.couponCode}' />">
                                <button type="button" class="btn btn-outline-secondary btn-sm" onclick="document.getElementById('checkoutForm').submit();">
                                    Apply
                                </button>
                            </div>
                            <small class="text-muted">Discounts validated strictly on server-side.</small>
                        </div>

                        <!-- Price Breakdown -->
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-secondary">Subtotal</span>
                            <span class="fw-semibold">Calculated from Catalog</span>
                        </div>

                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-secondary">Discount</span>
                            <span class="text-success fw-semibold">Auto-computed</span>
                        </div>

                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-secondary">Shipping Fee</span>
                            <span class="text-muted small">$0.00 if over $100</span>
                        </div>

                        <div class="d-flex justify-content-between mb-3">
                            <span class="text-secondary">Estimated Tax</span>
                            <span class="text-muted small">8% Standard Rate</span>
                        </div>

                        <hr class="my-3">

                        <div class="alert alert-light border small text-muted mb-4">
                            <i class="bi bi-lock-fill text-success me-1"></i>
                            <strong>Server-Verified Integrity:</strong> Order totals and discounts are calculated strictly on the backend.
                        </div>

                        <!-- Submit Order Button -->
                        <div class="d-grid">
                            <button type="submit" class="btn btn-success btn-lg fw-bold d-flex align-items-center justify-content-center gap-2">
                                <i class="bi bi-check2-circle fs-5"></i>
                                <span>Place Order Now</span>
                            </button>
                        </div>

                        <div class="text-center mt-3 small text-muted">
                            By placing an order, you agree to ShopSphere's Terms & Conditions.
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</main>

<jsp:include page="/WEB-INF/fragments/footer.jsp" />

