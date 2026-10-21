<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm py-3">
        <div class="container">
            <!-- Brand -->
            <a class="navbar-brand d-flex align-items-center gap-2" href="<c:url value='/products' />">
                <i class="bi bi-bag-check-fill brand-icon text-warning"></i>
                <span class="fw-bold tracking-tight">ShopSphere</span>
            </a>

            <!-- Mobile Toggle -->
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent"
                    aria-controls="navbarContent" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <!-- Navbar Links & Search -->
            <div class="collapse navbar-collapse" id="navbarContent">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/products' />">
                            <i class="bi bi-grid me-1"></i> Catalog
                        </a>
                    </li>
                </ul>

                <!-- Search Bar -->
                <form class="d-flex me-lg-3 my-2 my-lg-0" role="search" method="GET" action="<c:url value='/products' />">
                    <div class="input-group">
                        <input class="form-control form-control-sm bg-light border-0" type="search" name="q" 
                               placeholder="Search products..." aria-label="Search" value="<c:out value='${param.q}' />">
                        <button class="btn btn-primary btn-sm px-3" type="submit">
                            <i class="bi bi-search"></i>
                        </button>
                    </div>
                </form>

                <!-- Right Side Actions: Cart & Auth -->
                <div class="d-flex align-items-center gap-2 mt-3 mt-lg-0">
                    <!-- Shopping Cart -->
                    <a href="<c:url value='/cart' />" class="btn btn-outline-light btn-sm position-relative me-2">
                        <i class="bi bi-cart3"></i>
                        <span class="d-none d-md-inline ms-1">Cart</span>
                        <c:if test="${not empty sessionScope.cartCount && sessionScope.cartCount > 0}">
                            <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                                <c:out value="${sessionScope.cartCount}" />
                            </span>
                        </c:if>
                    </a>

                    <!-- User Account / Auth Dropdown -->
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <div class="dropdown">
                                <button class="btn btn-secondary btn-sm dropdown-toggle d-flex align-items-center gap-1"
                                        type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                    <i class="bi bi-person-circle"></i>
                                    <span><c:out value="${sessionScope.user.firstName}" /></span>
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end shadow">
                                    <li>
                                        <h6 class="dropdown-header">
                                            Signed in as <strong><c:out value="${sessionScope.user.email}" /></strong>
                                        </h6>
                                    </li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li>
                                        <a class="dropdown-item" href="<c:url value='/profile' />">
                                            <i class="bi bi-person me-2"></i> My Profile
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item" href="<c:url value='/orders' />">
                                            <i class="bi bi-box-seam me-2"></i> My Orders
                                        </a>
                                    </li>
                                    <c:if test="${'ADMIN'.equalsIgnoreCase(sessionScope.user.role)}">
                                        <li><hr class="dropdown-divider"></li>
                                        <li>
                                            <a class="dropdown-item text-danger fw-semibold" href="<c:url value='/admin/dashboard' />">
                                                <i class="bi bi-shield-lock me-2"></i> Admin Panel
                                            </a>
                                        </li>
                                    </c:if>
                                    <li><hr class="dropdown-divider"></li>
                                    <li>
                                        <a class="dropdown-item text-danger" href="<c:url value='/logout' />">
                                            <i class="bi bi-box-arrow-right me-2"></i> Sign Out
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <a href="<c:url value='/login' />" class="btn btn-outline-light btn-sm">Sign In</a>
                            <a href="<c:url value='/register' />" class="btn btn-primary btn-sm">Register</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </nav>
</header>

