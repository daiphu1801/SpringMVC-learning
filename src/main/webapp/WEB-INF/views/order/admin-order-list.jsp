<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Quản lý Đơn hàng">
<div class="container">
    <h1>Quản lý Đơn hàng</h1>
    <p style="color: var(--text-muted); margin-bottom: 25px;">Tất cả đơn hàng trong hệ thống — <strong>${orders.size()}</strong> đơn.</p>

    <c:if test="${empty orders}">
        <div style="text-align: center; padding: 60px 0; color: var(--text-muted);">
            <p style="font-size: 3rem; margin-bottom: 15px;">📋</p>
            <h3>Chưa có đơn hàng nào</h3>
        </div>
    </c:if>

    <c:if test="${not empty orders}">
        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>Mã đơn</th>
                        <th>Khách hàng</th>
                        <th>Địa chỉ</th>
                        <th>Tổng tiền</th>
                        <th>Ngày đặt</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <td><strong>#${order.id}</strong></td>
                            <td>
                                <div>${order.receiverName}</div>
                                <div style="font-size: 0.8rem; color: var(--text-muted);">${order.receiverPhone}</div>
                            </td>
                            <td style="font-size: 0.85rem; color: var(--text-muted); max-width: 200px;">
                                ${order.shippingAddress}
                            </td>
                            <td>
                                <strong style="color: #2ecc71;">
                                    <fmt:formatNumber value="${order.totalAmount}" pattern="#,##0"/> đ
                                </strong>
                            </td>
                            <td style="color: var(--text-muted); font-size: 0.85rem;">
                                ${order.formattedCreatedAt}
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${order.status == 'PENDING'}">
                                        <span class="badge" style="background: #FEF3C7; color: #B45309;">Chờ xử lý</span>
                                    </c:when>
                                    <c:when test="${order.status == 'CONFIRMED'}">
                                        <span class="badge" style="background: #DBEAFE; color: #1D4ED8;">Đã xác nhận</span>
                                    </c:when>
                                    <c:when test="${order.status == 'SHIPPING'}">
                                        <span class="badge" style="background: #EDE9FE; color: #6D28D9;">Đang vận chuyển</span>
                                    </c:when>
                                    <c:when test="${order.status == 'DELIVERED'}">
                                        <span class="badge badge-active">Đã giao</span>
                                    </c:when>
                                    <c:when test="${order.status == 'CANCELLED'}">
                                        <span class="badge badge-inactive">Đã huỷ</span>
                                    </c:when>
                                </c:choose>
                            </td>
                            <td style="white-space: nowrap;">
                                <a href="${pageContext.request.contextPath}/admin/orders/${order.id}" class="action-link-edit">Xem & Xử lý</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>
</div>
</t:layout>
