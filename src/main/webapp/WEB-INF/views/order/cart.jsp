<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Giỏ hàng của tôi">
<jsp:attribute name="head">
    <script src="${pageContext.request.contextPath}/resources/js/cart.js" defer></script>
</jsp:attribute>
<jsp:body>
<div class="container" style="max-width: 900px;">
    <h1>🛒 Giỏ hàng</h1>

    <c:if test="${empty cartItems}">
        <div style="text-align: center; padding: 60px 0; color: var(--text-muted);">
            <p style="font-size: 3rem; margin-bottom: 15px;">🛒</p>
            <h3 style="margin-bottom: 10px; color: var(--text-muted);">Giỏ hàng trống</h3>
            <p style="margin-bottom: 25px;">Hãy thêm sản phẩm vào giỏ hàng để tiếp tục.</p>
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
                        <tr>
                            <td>
                                <div style="display: flex; align-items: center; gap: 14px;">
                                    <c:choose>
                                        <c:when test="${not empty item.imageUrl}">
                                            <img src="${item.imageUrl}" alt="${item.productName}"
                                                 style="width: 56px; height: 56px; object-fit: cover; border-radius: 10px; border: 1px solid var(--border-color);"/>
                                        </c:when>
                                        <c:otherwise>
                                            <div style="width: 56px; height: 56px; background: linear-gradient(135deg, #7F84FF, #9FA1FF); border-radius: 10px; display: flex; align-items: center; justify-content: center;">
                                                <span style="color: white; font-weight: 700; font-size: 0.7rem; opacity: 0.6;">${item.productSku}</span>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div>
                                        <div style="font-weight: 600; color: var(--text-main);">${item.productName}</div>
                                        <div style="font-size: 0.8rem; color: var(--text-muted);">SKU: ${item.productSku}</div>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <span style="color: var(--text-main); font-weight: 500;">
                                    <fmt:formatNumber value="${item.unitPrice}" pattern="#,##0"/> đ
                                </span>
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/cart/update" method="post" style="display: flex; align-items: center; justify-content: center; gap: 4px;">
                                    <input type="hidden" name="productId" value="${item.productId}">
                                    
                                    <button type="button" class="qty-btn" onclick="decrementQty(this)" 
                                            style="width: 28px; height: 28px; border-radius: 6px; border: 1px solid var(--border-color); background: var(--bg-main); color: var(--text-main); font-weight: bold; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.2s;"
                                            onmouseover="this.style.background='var(--primary)'; this.style.color='white'; this.style.borderColor='var(--primary)';"
                                            onmouseout="this.style.background='var(--bg-main)'; this.style.color='var(--text-main)'; this.style.borderColor='var(--border-color)';"
                                            title="Giảm số lượng">-</button>
                                    
                                    <input type="number" name="quantity" value="${item.quantity}" min="1" max="99" 
                                           onchange="this.form.submit()" 
                                           style="width: 45px; height: 28px; border-radius: 6px; border: 1px solid var(--border-color); text-align: center; font-size: 0.9rem; font-weight: 600; color: var(--text-main); -moz-appearance: textfield; padding: 0; margin: 0;" />
                                    
                                    <button type="button" class="qty-btn" onclick="incrementQty(this)" 
                                            style="width: 28px; height: 28px; border-radius: 6px; border: 1px solid var(--border-color); background: var(--bg-main); color: var(--text-main); font-weight: bold; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.2s;"
                                            onmouseover="this.style.background='var(--primary)'; this.style.color='white'; this.style.borderColor='var(--primary)';"
                                            onmouseout="this.style.background='var(--bg-main)'; this.style.color='var(--text-main)'; this.style.borderColor='var(--border-color)';"
                                            title="Tăng số lượng">+</button>
                                </form>
                            </td>
                            <td>
                                <span style="font-weight: 700; color: #2ecc71;">
                                    <fmt:formatNumber value="${item.subtotal}" pattern="#,##0"/> đ
                                </span>
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/cart/remove" method="post">
                                    <input type="hidden" name="productId" value="${item.productId}">
                                    <button type="submit" class="btn-delete" onclick="return confirm('Xóa sản phẩm này khỏi giỏ?')">Xóa</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <div style="display: flex; justify-content: flex-end; align-items: center; gap: 20px; margin-top: 30px; padding-top: 25px; border-top: 2px solid var(--border-color);">
            <div style="text-align: right;">
                <div style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 4px;">Tổng cộng</div>
                <div style="font-size: 1.8rem; font-weight: 800; color: #2ecc71;">
                    <fmt:formatNumber value="${cartTotal}" pattern="#,##0"/> đ
                </div>
            </div>
            <div style="display: flex; flex-direction: column; gap: 10px;">
                <a href="${pageContext.request.contextPath}/orders/checkout" class="btn">
                    Tiến hành đặt hàng →
                </a>
                <form action="${pageContext.request.contextPath}/cart/clear" method="post" style="text-align: center;">
                    <button type="submit" class="btn-delete" style="font-size: 0.85rem;">Xóa tất cả</button>
                </form>
            </div>
        </div>
    </c:if>
</jsp:body>
</t:layout>
