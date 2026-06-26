<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Lịch sử đơn hàng">
<div class="container" style="max-width: 1000px;">
    <h1>📋 Đơn hàng của tôi</h1>
    <p style="color: var(--text-muted); margin-bottom: 25px;">Theo dõi trạng thái tất cả đơn hàng của bạn.</p>

    <c:if test="${empty orders}">
        <div style="text-align: center; padding: 60px 0; color: var(--text-muted);">
            <p style="font-size: 3rem; margin-bottom: 15px;">📦</p>
            <h3 style="margin-bottom: 10px; color: var(--text-muted);">Bạn chưa có đơn hàng nào</h3>
            <a href="${pageContext.request.contextPath}/products" class="btn" style="margin-top: 15px;">Mua sắm ngay</a>
        </div>
    </c:if>

    <c:if test="${not empty orders}">
        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>Mã đơn</th>
                        <th>Ngày đặt</th>
                        <th>Địa chỉ giao hàng</th>
                        <th>Tổng tiền</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <td><strong>#${order.id}</strong></td>
                            <td style="color: var(--text-muted); font-size: 0.9rem;">
                                ${order.formattedCreatedAt}
                            </td>
                            <td>
                                <div style="font-size: 0.9rem;">
                                    <strong>${order.receiverName}</strong><br>
                                    <span style="color: var(--text-muted);">${order.shippingAddress}</span>
                                </div>
                            </td>
                            <td>
                                <strong style="color: #2ecc71;">
                                    <fmt:formatNumber value="${order.totalAmount}" pattern="#,##0"/> đ
                                </strong>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${order.status == 'PENDING'}">
                                        <span class="badge" style="background: #FEF3C7; color: #B45309;">⏳ Chờ xử lý</span>
                                    </c:when>
                                    <c:when test="${order.status == 'CONFIRMED'}">
                                        <span class="badge" style="background: #DBEAFE; color: #1D4ED8;">✅ Đã xác nhận</span>
                                    </c:when>
                                    <c:when test="${order.status == 'SHIPPING'}">
                                        <span class="badge" style="background: #EDE9FE; color: #6D28D9;">🚚 Đang vận chuyển</span>
                                    </c:when>
                                    <c:when test="${order.status == 'DELIVERED'}">
                                        <span class="badge badge-active">🎉 Đã giao</span>
                                    </c:when>
                                    <c:when test="${order.status == 'CANCELLED'}">
                                        <span class="badge badge-inactive">❌ Đã huỷ</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge">${order.status}</span>
                                    </c:otherwise>
                                </c:choose>
                                <div style="font-size: 0.75rem; margin-top: 6px; line-height: 1.4;">
                                    <span style="color: var(--text-muted);">${order.formattedPaymentMethod}</span><br>
                                    <c:choose>
                                        <c:when test="${order.paymentStatus == 'PAID'}">
                                            <span style="color: #2ecc71; font-weight: 600;">Đã thanh toán</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: #e74c3c; font-weight: 600;">Chờ thanh toán</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </td>
                            <td style="white-space: nowrap;">
                                <div class="action-links">
                                    <a href="${pageContext.request.contextPath}/orders/${order.id}" class="action-link-edit">Xem chi tiết</a>
                                    <c:if test="${order.status == 'PENDING' || order.status == 'CONFIRMED'}">
                                        <form action="${pageContext.request.contextPath}/orders/${order.id}/cancel" method="post">
                                            <button type="submit" class="btn-delete" onclick="return confirm('Bạn chắc chắn muốn huỷ đơn hàng này?')">Huỷ đơn</button>
                                        </form>
                                    </c:if>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>
</div>
</t:layout>
