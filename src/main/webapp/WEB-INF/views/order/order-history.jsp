<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="h" uri="com.examp.springmvc.helpers" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Lịch sử đơn hàng">
<jsp:attribute name="head">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/order.css?v=${appVersion}">
    <script src="${pageContext.request.contextPath}/resources/js/pages/order-history.js?v=${appVersion}" defer></script>
</jsp:attribute>
<jsp:body>
<div class="container container-lg">
    <h1>📋 Đơn hàng của tôi</h1>
    <p class="text-muted mb-3">Theo dõi trạng thái tất cả đơn hàng của bạn.</p>

    <c:if test="${empty orders}">
        <div class="text-center mt-4 mb-4 text-muted">
            <p class="empty-state-icon">📦</p>
            <h3 class="mb-2">Bạn chưa có đơn hàng nào</h3>
            <a href="${pageContext.request.contextPath}/products" class="btn mt-2">Mua sắm ngay</a>
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
                            <td class="text-muted text-sm">
                                <c:out value="${h:formatDateTime(order.createdAt)}"/>
                            </td>
                            <td>
                                <div class="text-sm">
                                    <strong><c:out value="${order.receiverName}"/></strong><br>
                                    <span class="text-muted"><c:out value="${order.shippingAddress}"/></span>
                                </div>
                            </td>
                            <td>
                                <strong class="text-success">
                                    <fmt:formatNumber value="${order.totalAmount}" pattern="#,##0"/> đ
                                </strong>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${order.status == 'PENDING'}">
                                        <span class="badge badge-warning">⏳ Chờ xử lý</span>
                                    </c:when>
                                    <c:when test="${order.status == 'CONFIRMED'}">
                                        <span class="badge badge-info">✅ Đã xác nhận</span>
                                    </c:when>
                                    <c:when test="${order.status == 'SHIPPING'}">
                                        <span class="badge badge-secondary">🚚 Đang vận chuyển</span>
                                    </c:when>
                                    <c:when test="${order.status == 'DELIVERED'}">
                                        <span class="badge badge-success">🎉 Đã giao</span>
                                    </c:when>
                                    <c:when test="${order.status == 'CANCELLED'}">
                                        <span class="badge badge-danger">❌ Đã huỷ</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge">${order.status}</span>
                                    </c:otherwise>
                                </c:choose>
                                <div class="order-meta-info">
                                     <span class="text-muted"><c:out value="${h:formatPaymentMethod(order.paymentMethod)}"/></span><br>
                                     <c:choose>
                                         <c:when test="${order.paymentStatus == 'PAID'}">
                                             <span class="text-success font-bold">Đã thanh toán</span>
                                         </c:when>
                                         <c:otherwise>
                                             <span class="text-danger font-bold">Chờ thanh toán</span>
                                         </c:otherwise>
                                     </c:choose>
                                 </div>
                            </td>
                            <td>
                                <div class="action-links">
                                    <a href="${pageContext.request.contextPath}/orders/${order.id}" class="action-link-edit">Xem chi tiết</a>
                                    <c:if test="${order.status == 'PENDING' || order.status == 'CONFIRMED'}">
                                        <form action="${pageContext.request.contextPath}/orders/${order.id}/cancel" method="post" class="m-0">
                                            <input type="hidden" name="csrfToken" value="${csrfToken}">
                                            <button type="submit" class="btn-delete text-sm">Huỷ đơn</button>
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
</jsp:body>
</t:layout>
