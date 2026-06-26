<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Chi tiết đơn hàng #${order.id}">
<div class="container" style="max-width: 850px;">
    <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 15px; margin-bottom: 30px;">
        <div>
            <h1>Chi tiết đơn hàng <span style="color: var(--primary);">#${order.id}</span></h1>
            <p style="color: var(--text-muted); margin-top: 5px; font-size: 0.9rem;">
                Đặt lúc: ${order.formattedCreatedAt}
            </p>
        </div>
        <c:choose>
            <c:when test="${order.status == 'PENDING'}">
                <span class="badge" style="background: #FEF3C7; color: #B45309; font-size: 1rem; padding: 10px 18px;">⏳ Chờ xử lý</span>
            </c:when>
            <c:when test="${order.status == 'CONFIRMED'}">
                <span class="badge" style="background: #DBEAFE; color: #1D4ED8; font-size: 1rem; padding: 10px 18px;">✅ Đã xác nhận</span>
            </c:when>
            <c:when test="${order.status == 'SHIPPING'}">
                <span class="badge" style="background: #EDE9FE; color: #6D28D9; font-size: 1rem; padding: 10px 18px;">🚚 Đang vận chuyển</span>
            </c:when>
            <c:when test="${order.status == 'DELIVERED'}">
                <span class="badge badge-active" style="font-size: 1rem; padding: 10px 18px;">🎉 Đã giao thành công</span>
            </c:when>
            <c:when test="${order.status == 'CANCELLED'}">
                <span class="badge badge-inactive" style="font-size: 1rem; padding: 10px 18px;">❌ Đã huỷ</span>
            </c:when>
        </c:choose>
    </div>

    <c:if test="${param.paymentSuccess == 'true'}">
        <div class="alert alert-success" style="margin-bottom: 20px;">🎉 Thanh toán đơn hàng thành công! Đơn hàng của bạn đã được xác nhận.</div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <%-- Thông tin đơn hàng (Địa chỉ, Thanh toán, Ghi chú) --%>
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 20px; margin-bottom: 25px;">
        <div style="background: var(--bg-main); border-radius: var(--radius-md); padding: 20px; border: 1px solid var(--border-color);">
            <h3 style="font-size: 0.9rem; text-transform: uppercase; letter-spacing: 1px; color: var(--text-muted); margin-bottom: 12px;">📦 Địa chỉ giao hàng</h3>
            <p style="font-weight: 600; margin-bottom: 4px;">${order.receiverName}</p>
            <p style="color: var(--text-muted); font-size: 0.9rem;">${order.receiverPhone}</p>
            <p style="color: var(--text-muted); font-size: 0.9rem; margin-top: 6px;">${order.shippingAddress}</p>
        </div>
        <div style="background: var(--bg-main); border-radius: var(--radius-md); padding: 20px; border: 1px solid var(--border-color);">
            <h3 style="font-size: 0.9rem; text-transform: uppercase; letter-spacing: 1px; color: var(--text-muted); margin-bottom: 12px;">💳 Thanh toán</h3>
            <p style="font-weight: 600; margin-bottom: 4px;">Phương thức: ${order.formattedPaymentMethod}</p>
            <p style="margin-top: 6px;">
                <c:choose>
                    <c:when test="${order.paymentStatus == 'PAID'}">
                        <span class="badge" style="background: #D1FAE5; color: #065F46; padding: 4px 10px; font-size: 0.8rem; font-weight: 600;">✅ Đã thanh toán</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge" style="background: #FEE2E2; color: #991B1B; padding: 4px 10px; font-size: 0.8rem; font-weight: 600;">⏳ Chờ thanh toán</span>
                        <c:if test="${order.paymentMethod == 'VIETQR' && order.status == 'PENDING'}">
                            <a href="${pageContext.request.contextPath}/orders/${order.id}/payment" style="display: block; margin-top: 8px; font-size: 0.85rem; color: var(--primary); font-weight: bold; text-decoration: none;">👉 Đi đến trang QR thanh toán</a>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </p>
        </div>
        <div style="background: var(--bg-main); border-radius: var(--radius-md); padding: 20px; border: 1px solid var(--border-color);">
            <h3 style="font-size: 0.9rem; text-transform: uppercase; letter-spacing: 1px; color: var(--text-muted); margin-bottom: 12px;">💬 Ghi chú</h3>
            <p style="color: var(--text-muted); font-size: 0.9rem;">${empty order.note ? 'Không có ghi chú' : order.note}</p>
        </div>
    </div>

    <%-- Danh sách sản phẩm --%>
    <div class="table-wrapper" style="margin-bottom: 25px;">
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
                            <div style="font-weight: 600;">${item.productName}</div>
                            <div style="font-size: 0.8rem; color: var(--text-muted);">SKU: ${item.productSku}</div>
                        </td>
                        <td><fmt:formatNumber value="${item.unitPrice}" pattern="#,##0"/> đ</td>
                        <td>${item.quantity}</td>
                        <td><strong style="color: #2ecc71;"><fmt:formatNumber value="${item.subtotal}" pattern="#,##0"/> đ</strong></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <%-- Tổng cộng --%>
    <div style="display: flex; justify-content: flex-end; margin-bottom: 30px;">
        <div style="text-align: right; background: var(--primary-light); padding: 20px 30px; border-radius: var(--radius-md); border: 1px solid rgba(79, 70, 229, 0.1);">
            <div style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 4px;">Tổng giá trị đơn hàng</div>
            <div style="font-size: 2rem; font-weight: 800; color: var(--primary);">
                <fmt:formatNumber value="${order.totalAmount}" pattern="#,##0"/> đ
            </div>
        </div>
    </div>

    <%-- Actions --%>
    <div class="form-actions">
        <a href="${pageContext.request.contextPath}/orders" class="btn btn-secondary">← Lịch sử đơn hàng</a>
        <c:if test="${order.status == 'PENDING' || order.status == 'CONFIRMED'}">
            <form action="${pageContext.request.contextPath}/orders/${order.id}/cancel" method="post">
                <button type="submit" class="btn" style="background: linear-gradient(135deg, #EF4444, #DC2626);" onclick="return confirm('Bạn chắc chắn muốn huỷ đơn hàng này?')">
                    ❌ Huỷ đơn hàng
                </button>
            </form>
        </c:if>
    </div>
</div>
</t:layout>
