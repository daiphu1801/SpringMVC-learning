<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="h" uri="com.examp.springmvc.helpers" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Chi tiết đơn hàng #${order.id}">
<jsp:attribute name="head">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/order.css?v=${appVersion}">
</jsp:attribute>
<jsp:body>
<div class="container container-md">
    <div class="order-detail-header">
        <div>
            <h1 class="order-detail-title">Chi tiết đơn hàng <span class="text-primary">#${order.id}</span></h1>
            <p class="order-detail-subtitle">
                Đặt lúc: <c:out value="${h:formatDateTime(order.createdAt)}"/>
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
                <span class="badge badge-success order-status-badge">🎉 Đã giao thành công</span>
            </c:when>
            <c:when test="${order.status == 'CANCELLED'}">
                <span class="badge badge-danger order-status-badge">❌ Đã huỷ</span>
            </c:when>
        </c:choose>
    </div>

    <c:if test="${param.paymentSuccess == 'true'}">
        <div class="alert alert-success">🎉 Thanh toán đơn hàng thành công! Đơn hàng của bạn đã được xác nhận.</div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="alert alert-danger"><c:out value="${error}"/></div>
    </c:if>

    <%-- Thông tin đơn hàng (Địa chỉ, Thanh toán, Ghi chú) --%>
    <div class="order-info-grid">
        <div class="order-info-card">
            <h3 class="order-info-title">📦 Địa chỉ giao hàng</h3>
            <p class="order-info-name"><c:out value="${order.receiverName}"/></p>
            <p class="order-info-phone"><c:out value="${order.receiverPhone}"/></p>
            <p class="order-info-address mt-1"><c:out value="${order.shippingAddress}"/></p>
        </div>
        <div class="order-info-card">
            <h3 class="order-info-title">💳 Thanh toán</h3>
            <p class="order-info-name">Phương thức: <c:out value="${h:formatPaymentMethod(order.paymentMethod)}"/></p>
            <p class="mt-1">
                <c:choose>
                    <c:when test="${order.paymentStatus == 'PAID'}">
                        <span class="badge badge-success">✅ Đã thanh toán</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-danger">⏳ Chờ thanh toán</span>
                        <c:if test="${order.paymentMethod == 'VIETQR' && order.status == 'PENDING'}">
                            <a href="${pageContext.request.contextPath}/orders/${order.id}/payment" class="order-payment-link">👉 Đi đến trang QR thanh toán</a>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </p>
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
                    <th>Đơn giá</th>
                    <th>Số lượng</th>
                    <th>Thành tiền</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${order.items}">
                    <tr>
                        <td>
                            <div class="font-bold"><c:out value="${item.productName}"/></div>
                            <div class="text-sm text-muted">SKU: <c:out value="${item.productSku}"/></div>
                        </td>
                        <td><fmt:formatNumber value="${item.unitPrice}" pattern="#,##0"/> đ</td>
                        <td>${item.quantity}</td>
                        <td><strong class="text-success"><fmt:formatNumber value="${item.subtotal}" pattern="#,##0"/> đ</strong></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <%-- Tổng cộng --%>
    <div class="order-total-bill-wrapper">
        <div class="order-total-bill-box">
            <div class="order-total-bill-label">Tổng giá trị đơn hàng</div>
            <div class="order-total-bill-val">
                <fmt:formatNumber value="${order.totalAmount}" pattern="#,##0"/> đ
            </div>
        </div>
    </div>

    <%-- Actions --%>
    <div class="form-actions">
        <a href="${pageContext.request.contextPath}/orders" class="btn btn-secondary">← Lịch sử đơn hàng</a>
        <c:if test="${order.status == 'PENDING' || order.status == 'CONFIRMED'}">
            <form action="${pageContext.request.contextPath}/orders/${order.id}/cancel" method="post" class="m-0">
                <input type="hidden" name="csrfToken" value="${csrfToken}">
                <button type="submit" class="btn btn-danger">
                    ❌ Huỷ đơn hàng
                </button>
            </form>
        </c:if>
    </div>
</div>
</jsp:body>
</t:layout>
