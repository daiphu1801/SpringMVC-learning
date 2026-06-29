<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Cửa hàng Trực tuyến">
    <jsp:attribute name="head">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/store-front.css?v=${appVersion}">
    </jsp:attribute>
    <jsp:body>
    <div class="store-layout">
        
        <!-- Sidebar Categories -->
        <div class="container category-sidebar">
            <h3 class="category-title">
                Danh mục
            </h3>
            <ul class="category-list">
                <li>
                    <c:choose>
                        <c:when test="${empty selectedCategoryId}">
                            <a href="${pageContext.request.contextPath}/products" class="category-link active">
                                Tất cả sản phẩm
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/products" class="category-link">
                                Tất cả sản phẩm
                            </a>
                        </c:otherwise>
                    </c:choose>
                </li>
                <c:forEach var="cat" items="${categories}">
                    <li>
                        <c:choose>
                            <c:when test="${selectedCategoryId == cat.id}">
                                <a href="${pageContext.request.contextPath}/products?category=${cat.id}" class="category-link active">
                                    <c:out value="${cat.name}"/>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/products?category=${cat.id}" class="category-link">
                                    <c:out value="${cat.name}"/>
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </li>
                </c:forEach>
            </ul>
        </div>

        <!-- Product Grid -->
        <div>
            <div class="product-grid-header">
                <h2 class="product-grid-title">Sản phẩm Nổi bật</h2>
                <span class="text-sm text-muted">Tìm thấy <strong>${products.size()}</strong> sản phẩm</span>
            </div>

            <c:choose>
                <c:when test="${empty products}">
                    <div class="container text-center mb-4">
                        <h3 class="text-muted mb-2">Không tìm thấy sản phẩm nào</h3>
                        <p class="text-muted mb-1">Hiện chưa có sản phẩm nào thuộc danh mục này.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="product-grid">
                        <c:forEach var="prod" items="${products}">
                            <div class="container product-card">
                                
                                <!-- Product Image -->
                                <div class="product-image-wrapper">
                                    <c:choose>
                                        <c:when test="${not empty prod.imageUrl}">
                                            <img src="<c:out value='${prod.imageUrl}'/>" alt="<c:out value='${prod.name}'/>" class="product-img" loading="lazy" />
                                        </c:when>
                                        <c:otherwise>
                                            <div class="product-image-placeholder">
                                                <span class="product-sku-badge"><c:out value="${prod.sku}"/></span>
                                            </div>
                                        </c:otherwise>
                                     </c:choose>
                                     <span class="product-cat-tag">
                                         <c:out value="${prod.categoryName}"/>
                                     </span>
                                </div>

                                <!-- Product Info -->
                                <div class="product-details">
                                    <div>
                                        <h4 class="product-name">
                                            <c:out value="${prod.name}"/>
                                        </h4>
                                        <p class="product-desc">
                                            <c:out value="${prod.description}"/>
                                        </p>
                                    </div>
                                    
                                    <div class="product-action-section">
                                        <span class="product-price">
                                            <fmt:formatNumber value="${prod.price}" pattern="#,##0"/> đ
                                        </span>
                                        <form action="${pageContext.request.contextPath}/cart/add" method="post">
                                            <input type="hidden" name="csrfToken" value="${csrfToken}">
                                            <input type="hidden" name="productId" value="${prod.id}">
                                            <input type="hidden" name="quantity" value="1">
                                            <button type="submit" class="btn btn-full">
                                                🛒 Thêm vào giỏ
                                            </button>
                                        </form>
                                        <a href="${pageContext.request.contextPath}/products/${prod.id}" class="product-detail-link">
                                            Xem chi tiết →
                                        </a>
                                    </div>
                                </div>

                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

    </div>
    </jsp:body>
</t:layout>
