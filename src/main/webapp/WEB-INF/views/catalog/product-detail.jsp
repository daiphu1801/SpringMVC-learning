<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="${product.name}">
    <jsp:attribute name="head">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/store-front.css?v=${appVersion}">
        <script src="${pageContext.request.contextPath}/resources/js/pages/product-detail.js?v=${appVersion}" defer></script>
    </jsp:attribute>
    <jsp:body>
    <div class="container detail-container">
        
        <%-- Left Column: Product Image --%>
        <div class="product-detail-image-box">
            <c:choose>
                <c:when test="${not empty product.imageUrl}">
                    <img src="<c:out value='${product.imageUrl}'/>" alt="<c:out value='${product.name}'/>" class="product-detail-image" />
                </c:when>
                <c:otherwise>
                    <div class="product-image-placeholder">
                        <span class="product-sku-badge"><c:out value="${product.sku}"/></span>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- Right Column: Product Info --%>
        <div class="product-detail-info">
            <div>
                <span class="product-category-badge">
                    <c:out value="${product.categoryName}"/>
                </span>
                
                <h1 class="product-detail-title">
                    <c:out value="${product.name}"/>
                </h1>
                
                <div class="product-detail-price">
                    <fmt:formatNumber value="${product.price}" pattern="#,##0"/> đ
                </div>

                <div class="product-description-section">
                    <h3 class="product-description-title">
                        Mô tả sản phẩm
                    </h3>
                    <p class="product-description-text">
                        <c:out value="${product.description}"/>
                    </p>
                </div>
            </div>

            <%-- Action buttons --%>
            <div class="product-detail-actions">
                <div class="qty-selection">
                    <label for="quantityInput" class="qty-label">Số lượng:</label>
                    <input type="number" id="quantityInput" value="1" min="1" max="99" class="form-control qty-input-detail">
                </div>
                <div class="action-buttons-wrapper">
                    <form action="${pageContext.request.contextPath}/cart/add" method="post" class="flex-2">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="productId" value="${product.id}">
                        <input type="hidden" name="quantity" id="quantityHidden" value="1">
                        <button type="submit" class="btn btn-buy">
                            🛒 Thêm vào giỏ hàng
                        </button>
                    </form>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary btn-back-detail">
                        Quay lại
                    </a>
                </div>
            </div>
        </div>

    </div>
    </jsp:body>
</t:layout>
