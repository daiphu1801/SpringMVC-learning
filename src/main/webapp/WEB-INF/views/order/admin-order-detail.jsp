<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Đơn hàng #${order.id} - Admin">
<div class="container" style="max-width: 900px;">

    <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 15px; margin-bottom: 30px;">
        <div>
            <h1>Đơn hàng <span style="color: var(--primary);">#${order.id}</span></h1>
            <p style="color: var(--text-muted); font-size: 0.9rem; margin-top: 5px;">
                User ID: ${order.userId} &bull;
                Đặt lúc: ${order.formattedCreatedAt}
            </p>
        </div>
        <c:choose>
            <c:when test="${order.status == 'PENDING'}"><span class="badge" style="background: #FEF3C7; color: #B45309; font-size: 1rem; padding: 10px 18px;">⏳ Chờ xử lý</span></c:when>
            <c:when test="${order.status == 'CONFIRMED'}"><span class="badge" style="background: #DBEAFE; color: #1D4ED8; font-size: 1rem; padding: 10px 18px;">✅ Đã xác nhận</span></c:when>
            <c:when test="${order.status == 'SHIPPING'}"><span class="badge" style="background: #EDE9FE; color: #6D28D9; font-size: 1rem; padding: 10px 18px;">🚚 Đang vận chuyển</span></c:when>
            <c:when test="${order.status == 'DELIVERED'}"><span class="badge badge-active" style="font-size: 1rem; padding: 10px 18px;">🎉 Đã giao</span></c:when>
            <c:when test="${order.status == 'CANCELLED'}"><span class="badge badge-inactive" style="font-size: 1rem; padding: 10px 18px;">❌ Đã huỷ</span></c:when>
        </c:choose>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <%-- Cập nhật trạng thái --%>
    <c:if test="${order.status != 'DELIVERED' && order.status != 'CANCELLED'}">
        <div style="background: linear-gradient(135deg, #FFF7ED, #FFFBEB); border-radius: var(--radius-md); padding: 20px 25px; margin-bottom: 25px; border: 1px solid #FDE68A;">
            <h3 style="font-size: 0.9rem; text-transform: uppercase; letter-spacing: 1px; color: #B45309; margin-bottom: 16px; font-weight: 700;">⚙️ Cập nhật trạng thái</h3>
            <div style="display: flex; gap: 12px; flex-wrap: wrap;">
                <c:if test="${order.status == 'PENDING'}">
                    <form action="${pageContext.request.contextPath}/admin/orders/${order.id}/status" method="post">
                        <input type="hidden" name="action" value="confirm">
                        <button type="submit" class="btn" style="background: linear-gradient(135deg, #1D4ED8, #2563EB);">✅ Xác nhận đơn</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/admin/orders/${order.id}/status" method="post">
                        <input type="hidden" name="action" value="cancel">
                        <button type="submit" class="btn" style="background: linear-gradient(135deg, #EF4444, #DC2626);" onclick="return confirm('Huỷ đơn hàng này?')">❌ Huỷ đơn</button>
                    </form>
                </c:if>
                <c:if test="${order.status == 'CONFIRMED'}">
                    <form action="${pageContext.request.contextPath}/admin/orders/${order.id}/status" method="post">
                        <input type="hidden" name="action" value="ship">
                        <button type="submit" class="btn" style="background: linear-gradient(135deg, #6D28D9, #7C3AED);">🚚 Bắt đầu vận chuyển</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/admin/orders/${order.id}/status" method="post">
                        <input type="hidden" name="action" value="cancel">
                        <button type="submit" class="btn" style="background: linear-gradient(135deg, #EF4444, #DC2626);" onclick="return confirm('Huỷ đơn hàng này?')">❌ Huỷ đơn</button>
                    </form>
                </c:if>
                <c:if test="${order.status == 'SHIPPING'}">
                    <form action="${pageContext.request.contextPath}/admin/orders/${order.id}/status" method="post">
                        <input type="hidden" name="action" value="deliver">
                        <button type="submit" class="btn" style="background: linear-gradient(135deg, #059669, #10B981);">🎉 Xác nhận đã giao</button>
                    </form>
                </c:if>
            </div>
        </div>
    </c:if>

    <%-- Thông tin giao hàng --%>
    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 25px;">
        <div style="background: var(--bg-main); border-radius: var(--radius-md); padding: 20px; border: 1px solid var(--border-color);">
            <h3 style="font-size: 0.85rem; text-transform: uppercase; color: var(--text-muted); margin-bottom: 12px; letter-spacing: 1px;">📦 Thông tin giao hàng</h3>
            <p style="font-weight: 600; margin-bottom: 4px;">${order.receiverName}</p>
            <p style="color: var(--text-muted); font-size: 0.9rem;">${order.receiverPhone}</p>
            <p style="color: var(--text-muted); font-size: 0.9rem; margin-top: 6px;">${order.shippingAddress}</p>
        </div>
        <div style="background: var(--bg-main); border-radius: var(--radius-md); padding: 20px; border: 1px solid var(--border-color);">
            <h3 style="font-size: 0.85rem; text-transform: uppercase; color: var(--text-muted); margin-bottom: 12px; letter-spacing: 1px;">💬 Ghi chú</h3>
            <p style="color: var(--text-muted); font-size: 0.9rem;">${empty order.note ? 'Không có ghi chú' : order.note}</p>
        </div>
    </div>

    <%-- Danh sách sản phẩm --%>
    <div class="table-wrapper" style="margin-bottom: 25px;">
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
                        <td><strong>${item.productName}</strong></td>
                        <td style="color: var(--text-muted); font-size: 0.85rem;">${item.productSku}</td>
                        <td><fmt:formatNumber value="${item.unitPrice}" pattern="#,##0"/> đ</td>
                        <td>${item.quantity}</td>
                        <td><strong style="color: #2ecc71;"><fmt:formatNumber value="${item.subtotal}" pattern="#,##0"/> đ</strong></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 15px;">
        <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-secondary">← Danh sách đơn hàng</a>
        <div style="background: var(--primary-light); padding: 15px 25px; border-radius: var(--radius-md); text-align: right;">
            <div style="color: var(--text-muted); font-size: 0.85rem;">Tổng cộng</div>
            <div style="font-size: 1.8rem; font-weight: 800; color: var(--primary);">
                <fmt:formatNumber value="${order.totalAmount}" pattern="#,##0"/> đ
            </div>
        </div>
    </div>
</div>
</t:layout>
