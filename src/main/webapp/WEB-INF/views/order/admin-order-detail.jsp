<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Đơn hàng #${order.id} - Admin">
<jsp:attribute name="head">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/order.css?v=${appVersion}">
    <script src="${pageContext.request.contextPath}/resources/js/pages/admin-order-detail.js?v=${appVersion}" defer></script>
</jsp:attribute>
<jsp:body>
<div class="container container-md">

    <div class="order-detail-header">
        <div>
            <h1>Đơn hàng <span class="text-primary">#${order.id}</span></h1>
            <p class="order-detail-subtitle">
                User ID: ${order.userId} &bull;
                Đặt lúc: ${order.formattedCreatedAt}
            </p>
        </div>
        <c:choose>
            <c:when test="${order.status == 'PENDING'}">
                <span class="badge badge-warning order-status-badge">⏳ Chờ xử lý</span>
            </c:when>
            <c:when test="${order.status == 'CONFIRMED'}">
                <span class="badge badge-info order-status-badge">✅ Đã xác nhận</span>
            </c:when>
            <c:when test="${order.status == 'SHIPPING'}">
                <span class="badge badge-secondary order-status-badge">🚚 Đang vận chuyển</span>
            </c:when>
            <c:when test="${order.status == 'DELIVERED'}">
                <span class="badge badge-success order-status-badge">🎉 Đã giao</span>
            </c:when>
            <c:when test="${order.status == 'CANCELLED'}">
                <span class="badge badge-danger order-status-badge">❌ Đã huỷ</span>
            </c:when>
        </c:choose>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <%-- Cập nhật trạng thái --%>
    <c:if test="${order.status != 'DELIVERED' && order.status != 'CANCELLED'}">
        <div class="admin-status-banner">
            <h3 class="admin-status-banner-title">⚙️ Cập nhật trạng thái</h3>
            <div class="admin-status-banner-actions">
                <c:if test="${order.status == 'PENDING'}">
                    <form action="${pageContext.request.contextPath}/admin/orders/${order.id}/status" method="post">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="action" value="confirm">
                        <button type="submit" class="btn">✅ Xác nhận đơn</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/admin/orders/${order.id}/status" method="post" class="cancel-order-form">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="action" value="cancel">
                        <button type="submit" class="btn btn-danger">❌ Huỷ đơn</button>
                    </form>
                </c:if>
                <c:if test="${order.status == 'CONFIRMED'}">
                    <form action="${pageContext.request.contextPath}/admin/orders/${order.id}/status" method="post">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="action" value="ship">
                        <button type="submit" class="btn">🚚 Bắt đầu vận chuyển</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/admin/orders/${order.id}/status" method="post" class="cancel-order-form">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="action" value="cancel">
                        <button type="submit" class="btn btn-danger">❌ Huỷ đơn</button>
                    </form>
                </c:if>
                <c:if test="${order.status == 'SHIPPING'}">
                    <form action="${pageContext.request.contextPath}/admin/orders/${order.id}/status" method="post">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="action" value="deliver">
                        <button type="submit" class="btn">🎉 Xác nhận đã giao</button>
                    </form>
                </c:if>
            </div>
        </div>
    </c:if>

    <%-- Thông tin giao hàng --%>
    <div class="order-info-grid">
        <div class="order-info-card">
            <h3 class="order-info-title">📦 Thông tin giao hàng</h3>
            <p class="order-info-name"><c:out value="${order.receiverName}"/></p>
            <p class="order-info-phone"><c:out value="${order.receiverPhone}"/></p>
            <p class="order-info-address mt-1"><c:out value="${order.shippingAddress}"/></p>
        </div>
        <div class="order-info-card">
            <h3 class="order-info-title">💬 Ghi chú</h3>
            <p class="order-info-address">
                <c:choose>
                    <c:when test="${empty order.note}">Không có ghi chú</c:when>
                    <c:otherwise><c:out value="${order.note}"/></c:otherwise>
                </c:choose>
            </p>
        </div>
    </div>

    <%-- Danh sách sản phẩm --%>
    <div class="table-wrapper mb-3">
        <table>
            <thead>
                <tr>
                    <th>Sản phẩm</th>
                    <th>SKU</th>
                    <th>Đơn giá</th>
                    <th>Số lượng</th>
                    <th>Thành tiền</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${order.items}">
                    <tr>
                        <td><strong><c:out value="${item.productName}"/></strong></td>
                        <td class="text-sm text-muted"><c:out value="${item.productSku}"/></td>
                        <td><fmt:formatNumber value="${item.unitPrice}" pattern="#,##0"/> đ</td>
                        <td>${item.quantity}</td>
                        <td><strong class="text-success"><fmt:formatNumber value="${item.subtotal}" pattern="#,##0"/> đ</strong></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="flex-row-between">
        <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-secondary">← Danh sách đơn hàng</a>
        <div class="order-total-bill-box">
            <div class="order-total-bill-label">Tổng cộng</div>
            <div class="order-total-bill-val">
                <fmt:formatNumber value="${order.totalAmount}" pattern="#,##0"/> đ
            </div>
        </div>
    </div>
</div>
</jsp:body>
</t:layout>
