<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Thanh toán VietQR - Đơn hàng #${order.id}">
<jsp:attribute name="head">
    <style>
        .payment-container {
            max-width: 800px;
            margin: 40px auto;
            display: grid;
            grid-template-columns: 1.2fr 1fr;
            gap: 30px;
        }
        @media (max-width: 768px) {
            .payment-container {
                grid-template-columns: 1fr;
                margin: 20px auto;
            }
        }
        .info-card, .qr-card {
            background: var(--bg-surface);
            border-radius: var(--radius-lg);
            padding: 30px;
            border: 1px solid var(--border);
            box-shadow: var(--shadow-sm);
        }
        .qr-image-wrapper {
            background: #fff;
            padding: 15px;
            border-radius: var(--radius-md);
            border: 1px solid var(--border);
            text-align: center;
            margin-bottom: 20px;
        }
        .qr-image {
            max-width: 100%;
            height: auto;
            display: block;
            margin: 0 auto;
        }
        .bank-info-table {
            width: 100%;
            margin-top: 15px;
            border-collapse: collapse;
        }
        .bank-info-table td {
            padding: 12px 0;
            border-bottom: 1px dashed var(--border);
            font-size: 0.95rem;
        }
        .bank-info-table td:first-child {
            color: var(--text-muted);
            width: 40%;
        }
        .bank-info-table td:last-child {
            font-weight: 700;
            text-align: right;
            color: var(--text-main);
        }
        .amount-highlight {
            font-size: 1.4rem;
            color: #2ecc71;
            font-weight: 800;
        }
        .copy-badge {
            background: var(--primary-light);
            color: var(--primary);
            font-size: 0.75rem;
            padding: 2px 8px;
            border-radius: 4px;
            margin-left: 8px;
            cursor: pointer;
            user-select: none;
            transition: all var(--transition-speed);
        }
        .copy-badge:hover {
            opacity: 0.8;
        }
        .alert-instruction {
            background: rgba(79, 70, 229, 0.05);
            border-left: 4px solid var(--primary);
            color: var(--text-main);
            padding: 15px;
            border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
            font-size: 0.85rem;
            line-height: 1.5;
            margin-bottom: 20px;
        }
    </style>
    <script>
        function copyText(text, btn) {
            navigator.clipboard.writeText(text).then(() => {
                const originalText = btn.textContent;
                btn.textContent = 'Đã chép!';
                btn.style.background = '#2ecc71';
                btn.style.color = '#fff';
                setTimeout(() => {
                    btn.textContent = originalText;
                    btn.style.background = '';
                    btn.style.color = '';
                }, 1500);
            });
        }
    </script>
</jsp:attribute>
<jsp:body>
<div class="payment-container">
    <!-- Cột trái: Thông tin chuyển khoản -->
    <div class="info-card">
        <h2 style="margin-top: 0; font-size: 1.5rem; color: var(--text-main); display: flex; align-items: center; gap: 10px;">
            <span style="font-size: 1.8rem;">💵</span> Thông tin chuyển khoản
        </h2>
        <p style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 25px;">
            Vui lòng thực hiện chuyển khoản chính xác thông tin bên dưới để đơn hàng được tự động xác nhận nhanh nhất.
        </p>

        <c:if test="${not empty error}">
            <div class="alert alert-danger" style="margin-bottom: 20px;">${error}</div>
        </c:if>

        <table class="bank-info-table">
            <tr>
                <td>Ngân hàng</td>
                <td>${bankCode}</td>
            </tr>
            <tr>
                <td>Số tài khoản</td>
                <td>
                    <span>${accountNumber}</span>
                    <span class="copy-badge" onclick="copyText('${accountNumber}', this)">Copy</span>
                </td>
            </tr>
            <tr>
                <td>Chủ tài khoản</td>
                <td>${accountName}</td>
            </tr>
            <tr>
                <td>Số tiền</td>
                <td class="amount-highlight">
                    <fmt:formatNumber value="${order.totalAmount}" type="number" maxFractionDigits="0"/> đ
                    <span class="copy-badge" onclick="copyText('${order.totalAmount}', this)">Copy</span>
                </td>
            </tr>
            <tr>
                <td>Nội dung chuyển khoản</td>
                <td style="color: var(--primary);">
                    <span>DH${order.id}</span>
                    <span class="copy-badge" onclick="copyText('DH${order.id}', this)">Copy</span>
                </td>
            </tr>
        </table>

        <div style="margin-top: 30px;">
            <div class="alert-instruction">
                <strong>💡 Lưu ý:</strong> Hệ thống tự động xác nhận đơn hàng sau khi nhận đủ tiền. Trong môi trường giả lập này, bạn vui lòng nhấn nút <strong>"Xác nhận đã thanh toán"</strong> ở bên phải sau khi quét mã QR để chuyển trạng thái.
            </div>
            <a href="${pageContext.request.contextPath}/orders/${order.id}" style="color: var(--text-muted); text-decoration: none; font-size: 0.9rem; display: inline-flex; align-items: center; gap: 6px;">
                ← Quay lại chi tiết đơn hàng
            </a>
        </div>
    </div>

    <!-- Cột phải: Mã QR và Nút giả lập -->
    <div class="qr-card" style="display: flex; flex-direction: column; align-items: center; justify-content: center;">
        <h3 style="margin-top: 0; font-size: 1.1rem; text-align: center; color: var(--text-main); margin-bottom: 15px;">
            Quét mã QR để thanh toán
        </h3>

        <div class="qr-image-wrapper">
            <img class="qr-image"
                 src="https://img.vietqr.io/image/${bankCode}-${accountNumber}-qr_only.jpg?amount=${order.totalAmount}&addInfo=DH${order.id}&accountName=${accountName}"
                 alt="Mã QR thanh toán đơn hàng DH${order.id}" />
        </div>

        <div style="font-size: 0.8rem; color: var(--text-muted); text-align: center; margin-bottom: 25px; line-height: 1.4;">
            Sử dụng ứng dụng Camera hoặc App Ngân hàng hỗ trợ <strong>VietQR / QRPay</strong> để quét.
        </div>

        <form action="${pageContext.request.contextPath}/orders/${order.id}/payment/confirm" method="post" style="width: 100%;">
            <button type="submit" class="btn" style="width: 100%; padding: 14px; font-weight: 700; border-radius: var(--radius-md); box-shadow: var(--shadow-md);">
                ✅ Xác nhận đã thanh toán (Giả lập)
            </button>
        </form>
    </div>
</div>
</jsp:body>
</t:layout>
