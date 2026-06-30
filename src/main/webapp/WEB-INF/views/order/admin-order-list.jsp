<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="h" uri="com.examp.springmvc.helpers" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Quản lý Đơn hàng">
<jsp:attribute name="head">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/order.css?v=${appVersion}">
</jsp:attribute>
<jsp:body>
<div class="container">
    <h1>Quản lý Đơn hàng</h1>
    <p class="text-muted mb-3">Tất cả đơn hàng trong hệ thống — <strong>${orders.size()}</strong> đơn.</p>

    <c:if test="${empty orders}">
        <div class="text-center mt-4 mb-4 text-muted">
            <p class="empty-state-icon">📋</p>
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
                                <div><c:out value="${order.receiverName}"/></div>
                                <div class="text-sm text-muted"><c:out value="${order.receiverPhone}"/></div>
                            </td>
                            <td class="text-sm text-muted max-w-200">
                                <c:out value="${order.shippingAddress}"/>
                            </td>
                            <td>
                                <strong class="text-success">
                                    <fmt:formatNumber value="${order.totalAmount}" pattern="#,##0"/> đ
                                </strong>
                            </td>
                            <td class="text-sm text-muted">
                                <c:out value="${h:formatDateTime(order.createdAt)}"/>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${order.status == 'PENDING'}">
                                        <span class="badge badge-warning">Chờ xử lý</span>
                                    </c:when>
                                    <c:when test="${order.status == 'CONFIRMED'}">
                                        <span class="badge badge-info">Đã xác nhận</span>
                                    </c:when>
                                    <c:when test="${order.status == 'SHIPPING'}">
                                        <span class="badge badge-secondary">Đang vận chuyển</span>
                                    </c:when>
                                    <c:when test="${order.status == 'DELIVERED'}">
                                        <span class="badge badge-success">Đã giao</span>
                                    </c:when>
                                    <c:when test="${order.status == 'CANCELLED'}">
                                        <span class="badge badge-danger">Đã huỷ</span>
                                    </c:when>
                                </c:choose>
                            </td>
                            <td class="ws-nowrap">
                                <a href="${pageContext.request.contextPath}/admin/orders/${order.id}" class="action-link-edit">Xem & Xử lý</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>
</div>
</jsp:body>
</t:layout>
