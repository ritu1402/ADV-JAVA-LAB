<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Shopping Cart - ShopSphere" scope="request" />
<jsp:include page="/WEB-INF/fragments/header.jsp" />
<jsp:include page="/WEB-INF/fragments/navbar.jsp" />

<main class="py-5">
    <div class="container">
        <!-- Page Title -->
        <div class="d-flex align-items-center justify-content-between mb-4">
            <h2 class="fw-bold mb-0"><i class="bi bi-cart3 me-2 text-primary"></i>Your Shopping Cart</h2>
            <a href="<c:url value='/products' />" class="btn btn-outline-secondary btn-sm">
                <i class="bi bi-arrow-left me-1"></i> Continue Shopping
            </a>
        </div>

        <c:choose>
            <c:when test="${not empty cartItems}">
                <div class="row g-4">
                    <!-- Cart Items Table Column -->
                    <div class="col-lg-8">
                        <div class="card border-0 shadow-sm rounded-3 overflow-hidden">
                            <div class="table-responsive">
                                <table class="table cart-table align-middle mb-0">
                                    <thead class="table-light text-secondary small text-uppercase">
                                        <tr>
                                            <th scope="col" style="min-width: 250px;">Product</th>
                                            <th scope="col" class="text-center">Price</th>
                                            <th scope="col" class="text-center" style="width: 150px;">Quantity</th>
                                            <th scope="col" class="text-end">Total</th>
                                            <th scope="col" class="text-center" style="width: 50px;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:set var="cartSubtotal" value="0" />
                                        <c:forEach items="${cartItems}" var="item">
                                            <tr>
                                                <!-- Product Info -->
                                                <td>
                                                    <div class="d-flex align-items-center gap-3">
                                                        <div class="bg-light rounded p-2 text-center text-muted" style="width: 56px; height: 56px;">
                                                            <i class="bi bi-box-seam fs-3"></i>
                                                        </div>
                                                        <div>
                                                            <h6 class="mb-0 fw-semibold">
                                                                <a href="<c:url value='/products/${item.productId}' />" class="text-decoration-none text-dark">
                                                                    <c:out value="${itemProductMap[item.productId].name != null ? itemProductMap[item.productId].name : 'Product #' += item.productId}" />
                                                                </a>
                                                            </h6>
                                                            <small class="text-muted">Item ID: <c:out value="${item.productId}" /></small>
                                                        </div>
                                                    </div>
                                                </td>

                                                <!-- Unit Price -->
                                                <td class="text-center fw-medium">
                                                    <c:set var="currentPrice" value="${itemProductMap[item.productId].price != null ? itemProductMap[item.productId].price : 0.00}" />
                                                    $<c:out value="${currentPrice}" />
                                                </td>

                                                <!-- Quantity Update Form -->
                                                <td class="text-center">
                                                    <form method="POST" action="<c:url value='/cart' />" class="d-inline-flex align-items-center gap-1">
                                                        <input type="hidden" name="action" value="update"/>
                                                        <input type="hidden" name="cartId" value="<c:out value='${item.cartId}' />"/>
                                                        <input type="number" name="quantity" class="form-control form-control-sm text-center"
                                                               value="<c:out value='${item.quantity}' />" min="1" max="99" style="width: 65px;" required>
                                                        <button type="submit" class="btn btn-outline-primary btn-sm px-2" title="Update Quantity">
                                                            <i class="bi bi-check-lg"></i>
                                                        </button>
                                                    </form>
                                                </td>

                                                <!-- Line Total -->
                                                <td class="text-end fw-bold">
                                                    $<c:out value="${currentPrice * item.quantity}" />
                                                    <c:set var="cartSubtotal" value="${cartSubtotal + (currentPrice * item.quantity)}" />
                                                </td>

                                                <!-- Remove Action -->
                                                <td class="text-center">
                                                    <form method="POST" action="<c:url value='/cart' />" class="d-inline">
                                                        <input type="hidden" name="action" value="delete"/>
                                                        <input type="hidden" name="cartId" value="<c:out value='${item.cartId}' />"/>
                                                        <button type="submit" class="btn btn-link text-danger p-0 border-0" title="Remove item">
                                                            <i class="bi bi-trash3"></i>
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>

                            <!-- Clear Cart Action -->
                            <div class="card-footer bg-white border-top d-flex justify-content-between align-items-center py-3">
                                <form method="POST" action="<c:url value='/cart' />" onsubmit="return confirm('Clear all items from your cart?');">
                                    <input type="hidden" name="action" value="clear"/>
                                    <button type="submit" class="btn btn-outline-danger btn-sm">
                                        <i class="bi bi-trash me-1"></i> Empty Cart
                                    </button>
                                </form>
                                <span class="text-muted small">Prices are confirmed at checkout.</span>
                            </div>
                        </div>
                    </div>

                    <!-- Order Summary Sidebar Column -->
                    <div class="col-lg-4">
                        <div class="cart-summary-card shadow-sm">
                            <h5 class="fw-bold mb-3 border-bottom pb-2">Order Summary</h5>

                            <div class="d-flex justify-content-between mb-2">
                                <span class="text-secondary">Estimated Subtotal</span>
                                <span class="fw-semibold">$<c:out value="${cartSubtotal}" /></span>
                            </div>

                            <div class="d-flex justify-content-between mb-2">
                                <span class="text-secondary">Shipping</span>
                                <span class="text-muted small">Calculated at checkout</span>
                            </div>

                            <div class="d-flex justify-content-between mb-3">
                                <span class="text-secondary">Sales Tax (8%)</span>
                                <span class="text-muted small">Calculated at checkout</span>
                            </div>

                            <div class="alert alert-info py-2 small mb-3">
                                <i class="bi bi-truck me-1"></i> Free standard shipping on orders over $100!
                            </div>

                            <hr class="my-3">

                            <div class="d-flex justify-content-between mb-4 fs-5 fw-bold">
                                <span>Estimated Total</span>
                                <span class="text-primary">$<c:out value="${cartSubtotal}" /></span>
                            </div>

                            <!-- Proceed to Checkout Button -->
                            <div class="d-grid gap-2">
                                <a href="<c:url value='/checkout' />" class="btn btn-primary btn-lg fw-semibold d-flex align-items-center justify-content-center gap-2">
                                    <span>Proceed to Checkout</span>
                                    <i class="bi bi-arrow-right"></i>
                                </a>
                            </div>

                            <div class="text-center mt-3 small text-muted">
                                <i class="bi bi-shield-lock me-1"></i> Guaranteed safe and secure checkout
                            </div>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Empty Cart State -->
                <div class="card border-0 shadow-sm rounded-3 text-center py-5 px-3">
                    <div class="card-body">
                        <i class="bi bi-cart-x fs-1 text-muted" style="font-size: 4rem;"></i>
                        <h4 class="fw-bold mt-4 mb-2">Your Cart is Currently Empty</h4>
                        <p class="text-muted mb-4">
                            Looks like you haven't added anything to your cart yet. Explore our featured products!
                        </p>
                        <a href="<c:url value='/products' />" class="btn btn-primary px-4 py-2 fw-semibold">
                            Start Shopping
                        </a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<jsp:include page="/WEB-INF/fragments/footer.jsp" />

