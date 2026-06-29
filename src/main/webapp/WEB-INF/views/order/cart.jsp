<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Giỏ hàng của tôi">
<jsp:attribute name="head">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/cart.css?v=${appVersion}">
    <script src="${pageContext.request.contextPath}/resources/js/cart.js?v=${appVersion}" defer></script>
</jsp:attribute>
<jsp:body>
<div class="container container-md">
    <h1>🛒 Giỏ hàng</h1>

    <c:if test="${empty cartItems}">
        <div class="empty-cart-state">
            <span class="empty-cart-icon">🛒</span>
            <h3 class="mb-2 text-muted">Giỏ hàng trống</h3>
            <p class="mb-3">Hãy thêm sản phẩm vào giỏ hàng để tiếp tục.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn">Khám phá cửa hàng</a>
        </div>
    </c:if>

    <c:if test="${not empty cartItems}">
        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>Sản phẩm</th>
                        <th>Đơn giá</th>
                        <th>Số lượng</th>
                        <th>Thành tiền</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${cartItems}">
                        <tr class="cart-item-row">
                            <td>
                                <div class="cart-item-info">
                                    <c:choose>
                                        <c:when test="${not empty item.imageUrl}">
                                            <img src="<c:out value='${item.imageUrl}'/>" alt="<c:out value='${item.productName}'/>" class="cart-item-img"/>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="cart-item-placeholder">
                                                <span class="product-sku-badge text-sm"><c:out value="${item.productSku}"/></span>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div>
                                        <div class="cart-item-name"><c:out value="${item.productName}"/></div>
                                        <div class="cart-item-sku">SKU: <c:out value="${item.productSku}"/></div>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <span class="cart-item-price">
                                    <fmt:formatNumber value="${item.unitPrice}" pattern="#,##0"/> đ
                                </span>
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/cart/update" method="post" class="qty-control-form">
                                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                                    <input type="hidden" name="productId" value="${item.productId}">
                                    
                                    <button type="button" class="qty-control-btn qty-btn-minus" title="Giảm số lượng">-</button>
                                    
                                    <input type="number" name="quantity" value="${item.quantity}" min="1" max="99" 
                                           class="qty-control-input" />
                                    
                                    <button type="button" class="qty-control-btn qty-btn-plus" title="Tăng số lượng">+</button>
                                </form>
                            </td>
                            <td>
                                <span class="cart-item-subtotal">
                                    <fmt:formatNumber value="${item.subtotal}" pattern="#,##0"/> đ
                                </span>
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/cart/remove" method="post" class="remove-item-form">
                                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                                    <input type="hidden" name="productId" value="${item.productId}">
                                    <button type="submit" class="btn-delete">Xóa</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="cart-summary-section">
            <div class="cart-summary-info">
                <div class="cart-summary-label">Tổng cộng</div>
                <div class="cart-summary-total">
                    <fmt:formatNumber value="${cartTotal}" pattern="#,##0"/> đ
                </div>
            </div>
            <div class="cart-actions-column">
                <a href="${pageContext.request.contextPath}/orders/checkout" class="btn">
                    Tiến hành đặt hàng →
                </a>
                <form action="${pageContext.request.contextPath}/cart/clear" method="post" class="text-center">
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <button type="submit" class="btn-delete text-sm">Xóa tất cả</button>
                </form>
            </div>
        </div>
    </c:if>
</div>
</jsp:body>
</t:layout>
