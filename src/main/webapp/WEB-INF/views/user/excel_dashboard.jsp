<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Giám sát tiến trình Excel">
<jsp:attribute name="head">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/user.css?v=${appVersion}">
    <script src="${pageContext.request.contextPath}/resources/js/pages/excel-dashboard.js?v=${appVersion}" defer></script>
</jsp:attribute>
<jsp:body>
<div class="container">
    <div class="mb-3">
        <a href="${pageContext.request.contextPath}/users" class="action-link-edit">
            &larr; Quay lại danh sách người dùng
        </a>
    </div>
    <h1>Giám sát tiến trình Excel</h1>
    <p class="text-muted mb-4">Theo dõi trực quan tiến độ nhập/xuất dữ liệu người dùng thời gian thực.</p>

    <div class="grid-3-col mb-4">
        <div class="stat-card">
            <span class="stat-card-title">Tổng tác vụ</span>
            <span class="stat-card-value" id="stat-total-tasks">0</span>
        </div>
        <div class="stat-card">
            <span class="stat-card-title">Đang hoạt động</span>
            <span class="stat-card-value text-primary" id="stat-active-tasks">0</span>
        </div>
        <div class="stat-card">
            <span class="stat-card-title">Hoàn thành</span>
            <span class="stat-card-value text-success" id="stat-completed-tasks">0</span>
        </div>
    </div>

    <h2 class="mb-3" style="font-size: 1.2rem; font-weight: 600; color: var(--text-main); margin-top: 10px;">
        Giám sát hiệu năng hệ thống
    </h2>
    <div class="pool-grid mb-4">
        <!-- Reader Thread Pool -->
        <div class="pool-card">
            <div class="pool-card-header">
                <span class="pool-card-title">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="vertical-align: middle;"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                    Xử lý Tệp (Reader/Exporter Pool)
                </span>
                <span class="badge-task" id="reader-pool-status" style="background: var(--bg-body); color: var(--text-muted); display: inline-flex; align-items: center; gap: 6px;">
                    <span class="pulse-indicator" id="reader-pulse"></span> <span id="reader-status-text">Đang rảnh</span>
                </span>
            </div>
            <div class="pool-metrics-grid">
                <div class="pool-metric-item">
                    <span class="pool-metric-label">Luồng đang chạy</span>
                    <span class="pool-metric-value" id="reader-active-threads">0</span>
                </div>
                <div class="pool-metric-item">
                    <span class="pool-metric-label">Hàng đợi (Files)</span>
                    <span class="pool-metric-value text-primary" id="reader-queue-size">0</span>
                </div>
                <div class="pool-metric-item">
                    <span class="pool-metric-label">Tổng luồng tối đa</span>
                    <span class="pool-metric-value" id="reader-max-threads">0</span>
                </div>
                <div class="pool-metric-item">
                    <span class="pool-metric-label">Đã xử lý xong</span>
                    <span class="pool-metric-value text-success" id="reader-completed-tasks">0</span>
                </div>
            </div>
        </div>

        <!-- DB Writer Thread Pool -->
        <div class="pool-card">
            <div class="pool-card-header">
                <span class="pool-card-title">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="vertical-align: middle;"><ellipse cx="12" cy="5" rx="9" ry="3"/><path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"/><path d="M3 12c0 1.66 4 3 9 3s9-1.34 9-3"/></svg>
                    Ghi cơ sở dữ liệu (DB Writer Pool)
                </span>
                <span class="badge-task" id="writer-pool-status" style="background: var(--bg-body); color: var(--text-muted); display: inline-flex; align-items: center; gap: 6px;">
                    <span class="pulse-indicator" id="writer-pulse"></span> <span id="writer-status-text">Đang rảnh</span>
                </span>
            </div>
            <div class="pool-metrics-grid">
                <div class="pool-metric-item">
                    <span class="pool-metric-label">Luồng đang chạy</span>
                    <span class="pool-metric-value" id="writer-active-threads">0</span>
                </div>
                <div class="pool-metric-item">
                    <span class="pool-metric-label">Hàng đợi (Batches)</span>
                    <span class="pool-metric-value text-primary" id="writer-queue-size">0</span>
                </div>
                <div class="pool-metric-item">
                    <span class="pool-metric-label">Tổng luồng tối đa</span>
                    <span class="pool-metric-value" id="writer-max-threads">0</span>
                </div>
                <div class="pool-metric-item">
                    <span class="pool-metric-label">Đã ghi xong</span>
                    <span class="pool-metric-value text-success" id="writer-completed-tasks">0</span>
                </div>
            </div>
        </div>
    </div>

    <div class="excel-dashboard" id="excel-dashboard">
        <div class="excel-dashboard-header">
            <div class="excel-dashboard-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                Danh sách Tiến trình Gần đây
            </div>
            <button type="button" class="btn-secondary btn-sm" id="btn-refresh-tasks" style="padding: 6px 12px; font-size: 0.85rem;">
                Làm mới
            </button>
        </div>
        <div class="task-list" id="task-list-container">
            <div class="text-center text-muted py-4">Đang tải danh sách tiến trình...</div>
        </div>
    </div>
</div>

<!-- Modal hiển thị báo cáo lỗi chi tiết -->
<div class="excel-modal" id="modal-error-report">
    <div class="excel-modal-content large">
        <div class="excel-modal-header">
            <h3>Báo cáo lỗi chi tiết</h3>
            <button type="button" class="excel-modal-close" id="btn-close-errors-modal">&times;</button>
        </div>
        <div class="excel-form-group">
            <pre class="excel-error-log" id="excel-error-log-content">Đang tải...</pre>
        </div>
    </div>
</div>
</jsp:body>
</t:layout>
