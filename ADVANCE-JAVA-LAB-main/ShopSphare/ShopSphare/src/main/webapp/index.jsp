<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:if test="${products == null}">
    <c:redirect url="/products" />
</c:if>

<c:set var="pageTitle" value="ShopSphere - Home & Products" scope="request" />
<jsp:include page="/WEB-INF/fragments/header.jsp" />
<jsp:include page="/WEB-INF/fragments/navbar.jsp" />

<main class="py-4">
    <div class="container">
        <!-- Hero Banner -->
        <div class="p-4 p-md-5 mb-4 rounded-3 text-white bg-dark shadow-sm position-relative overflow-hidden" 
             style="background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);">
            <div class="col-md-8 position-relative z-1">
                <span class="badge bg-warning text-dark px-3 py-2 fw-semibold mb-3">Enterprise Grade Catalog</span>
                <h1 class="display-5 fw-bold">Explore Premium Quality Products</h1>
                <p class="lead my-3 text-white-50">
                    Discover hand-picked collections across top electronics, apparel, and lifestyle accessories.
                </p>
                <a href="#catalog" class="btn btn-primary btn-lg px-4 me-md-2 fw-semibold">Browse Collection</a>
            </div>
            <i class="bi bi-box-seam position-absolute end-0 bottom-0 text-white opacity-10" style="font-size: 15rem; transform: translate(15%, 15%);"></i>
        </div>

        <!-- Main Layout: Sidebar & Products Grid -->
        <div class="row g-4" id="catalog">
            <!-- Sidebar -->
            <div class="col-lg-3">
                <jsp:include page="/WEB-INF/fragments/sidebar.jsp" />
            </div>

            <!-- Product Grid -->
            <div class="col-lg-9">
                <!-- Sorting & Stats Bar -->
                <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
                    <div>
                        <h4 class="fw-bold mb-0">Products</h4>
                        <small class="text-muted">
                            <c:choose>
                                <c:when test="${not empty products}">Showing <c:out value="${products.size()}" /> items</c:when>
                                <c:otherwise>0 products found</c:otherwise>
                            </c:choose>
                        </small>
                    </div>
                </div>

                <!-- Product Grid Rendering via JSTL -->
                <c:choose>
                    <c:when test="${not empty products}">
                        <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-4">
                            <c:forEach items="${products}" var="product">
                                <div class="col">
                                    <div class="card product-card">
                                        <!-- Image Placeholder & Badges -->
                                        <div class="product-image-wrapper">
                                            <i class="bi bi-image fs-1"></i>
                                            <c:if test="${not empty product.salePrice && product.salePrice > 0}">
                                                <span class="badge bg-danger product-badge">SALE</span>
                                            </c:if>
                                        </div>

                                        <!-- Card Body -->
                                        <div class="card-body product-body">
                                            <div class="d-flex justify-content-between align-items-center mb-1">
                                                <small class="text-uppercase text-muted fw-semibold" style="font-size: 0.75rem;">
                                                    SKU: <c:out value="${product.sku}" />
                                                </small>
                                                <c:choose>
                                                    <c:when test="${product.stockQuantity > 0}">
                                                        <span class="badge bg-success-subtle text-success small">In Stock</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-danger-subtle text-danger small">Sold Out</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>

                                            <h5 class="card-title product-title">
                                                <a href="<c:url value='/products/${product.productId}' />" class="text-decoration-none text-dark">
                                                    <c:out value="${product.name}" />
                                                </a>
                                            </h5>

                                            <p class="card-text text-muted small flex-grow-1" style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
                                                <c:out value="${product.description != null ? product.description : 'Premium quality item tailored to your daily needs.'}" />
                                            </p>

                                            <!-- Price & Action Section -->
                                            <div class="product-price-section">
                                                <div class="d-flex align-items-baseline mb-3">
                                                    <c:choose>
                                                        <c:when test="${not empty product.salePrice && product.salePrice > 0}">
                                                            <span class="current-price text-danger">$<c:out value="${product.salePrice}" /></span>
                                                            <span class="original-price">$<c:out value="${product.price}" /></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="current-price">$<c:out value="${product.price}" /></span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>

                                                <!-- Add To Cart Form -->
                                                <form method="POST" action="<c:url value='/cart' />" class="d-grid">
                                                    <input type="hidden" name="action" value="add"/>
                                                    <input type="hidden" name="productId" value="<c:out value='${product.productId}' />"/>
                                                    <input type="hidden" name="quantity" value="1"/>

                                                    <button type="submit" class="btn btn-primary btn-sm d-flex align-items-center justify-content-center gap-2"
                                                            ${product.stockQuantity <= 0 ? 'disabled' : ''}>
                                                        <i class="bi bi-cart-plus"></i>
                                                        <c:choose>
                                                            <c:when test="${product.stockQuantity > 0}">Add to Cart</c:when>
                                                            <c:otherwise>Out of Stock</c:otherwise>
                                                        </c:choose>
                                                    </button>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>

                        <!-- Pagination -->
                        <nav aria-label="Page navigation" class="mt-5">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${param.page <= 1 || empty param.page ? 'disabled' : ''}">
                                    <a class="page-link" href="<c:url value='/products?page=${(param.page != null ? param.page : 1) - 1}' />">Previous</a>
                                </li>
                                <li class="page-item active"><a class="page-link" href="#"><c:out value="${param.page != null ? param.page : 1}" /></a></li>
                                <li class="page-item">
                                    <a class="page-link" href="<c:url value='/products?page=${(param.page != null ? param.page : 1) + 1}' />">Next</a>
                                </li>
                            </ul>
                        </nav>
                    </c:when>
                    <c:otherwise>
                        <!-- Empty State -->
                        <div class="text-center py-5 bg-white rounded-3 shadow-sm p-4">
                            <i class="bi bi-inbox fs-1 text-muted"></i>
                            <h5 class="mt-3 fw-bold">No Products Found</h5>
                            <p class="text-muted">No items matched your current filter criteria.</p>
                            <a href="<c:url value='/products' />" class="btn btn-outline-primary btn-sm mt-2">Clear Filters</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/fragments/footer.jsp" />

