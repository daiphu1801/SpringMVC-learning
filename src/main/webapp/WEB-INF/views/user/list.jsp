<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Danh sách người dùng">
<jsp:attribute name="head">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/user.css?v=${appVersion}">
    <script src="${pageContext.request.contextPath}/resources/js/pages/user-list.js?v=${appVersion}" defer></script>
</jsp:attribute>
<jsp:body>
<div class="container">
    <h1>Danh sách người dùng</h1>
    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
        <div class="btn-container mb-3 d-flex align-center gap-3">
            <a href="${pageContext.request.contextPath}/users/create" class="btn">
                Thêm người dùng
            </a>
            <button type="button" class="btn-secondary" id="btn-open-import">
                Import Excel
            </button>
            <button type="button" class="btn-secondary" id="btn-trigger-export">
                Export Excel
            </button>
            <a href="${pageContext.request.contextPath}/users/excel/dashboard" class="btn-secondary" id="btn-view-dashboard" style="text-decoration: none;">
                Lịch sử &amp; Tiến trình Excel
            </a>
        </div>
    </c:if>

    <div class="table-wrapper">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Họ tên</th>
                <th>Email</th>
                <th>Số điện thoại</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
            </tr>
            </thead>

            <tbody>

            <c:forEach var="user" items="${pagedResult.items}">
                <tr>
                    <td>
                        <c:out value="${user.id}"/>
                    </td>

                    <td>
                        <c:out value="${user.username}"/>
                    </td>

                    <td>
                        <c:out value="${user.fullName}"/>
                    </td>

                    <td>
                        <c:out value="${user.email}"/>
                    </td>

                    <td>
                        <c:out value="${user.phone}"/>
                    </td>

                    <td>
                        <span class="badge ${user.status == 'ACTIVE' ? 'badge-active' : 'badge-inactive'}">
                            <c:choose>
                                <c:when test="${user.status == 'ACTIVE'}">Hoạt động</c:when>
                                <c:otherwise>Không hoạt động</c:otherwise>
                            </c:choose>
                        </span>
                    </td>

                    <td>
                        <div class="action-links">
                            <c:choose>
                                <c:when test="${sessionScope.currentUser.role == 'ADMIN'}">
                                    <a href="${pageContext.request.contextPath}/users/edit/${user.id}" class="action-link-edit">
                                        Sửa
                                    </a>

                                    <form action="${pageContext.request.contextPath}/users/delete/${user.id}" method="post" class="d-inline-flex">
                                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                                        <button type="submit" class="btn-delete">
                                            Xóa
                                        </button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-sm text-muted">Không có quyền</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty pagedResult.items}">
                <tr>
                    <td colspan="7" class="text-center text-muted">
                        Chưa có dữ liệu người dùng
                    </td>
                </tr>
            </c:if>

            </tbody>
        </table>
    </div>

    <!-- Phân trang (Pagination) -->
    <c:if test="${not empty pagedResult.items}">
        <div class="pagination-wrapper">
            <div class="pagination-info">
                Hiển thị từ <strong><c:out value="${(pagedResult.page - 1) * pagedResult.size + 1}"/></strong> 
                đến <strong><c:out value="${(pagedResult.page - 1) * pagedResult.size + pagedResult.items.size()}"/></strong> 
                trên tổng số <strong><c:out value="${pagedResult.totalItems}"/></strong> người dùng
            </div>
            <div class="pagination-controls">
                <!-- Nút Về Đầu -->
                <c:choose>
                    <c:when test="${pagedResult.hasPrevious}">
                        <a href="?page=1&amp;size=<c:out value='${pagedResult.size}'/>" class="page-link" title="Trang đầu">&laquo;</a>
                        <a href="?page=<c:out value='${pagedResult.page - 1}'/>&amp;size=<c:out value='${pagedResult.size}'/>" class="page-link" title="Trang trước">&lsaquo;</a>
                    </c:when>
                    <c:otherwise>
                        <span class="page-link disabled">&laquo;</span>
                        <span class="page-link disabled">&lsaquo;</span>
                    </c:otherwise>
                </c:choose>

                <!-- Hiển thị các số trang xung quanh trang hiện tại -->
                <c:set var="beginPage" value="${pagedResult.page - 2 < 1 ? 1 : pagedResult.page - 2}"/>
                <c:set var="endPage" value="${pagedResult.page + 2 > pagedResult.totalPages ? pagedResult.totalPages : pagedResult.page + 2}"/>
                <c:forEach var="i" begin="${beginPage}" end="${endPage}">
                    <c:choose>
                        <c:when test="${i == pagedResult.page}">
                            <span class="page-link active"><c:out value="${i}"/></span>
                        </c:when>
                        <c:otherwise>
                            <a href="?page=<c:out value='${i}'/>&amp;size=<c:out value='${pagedResult.size}'/>" class="page-link"><c:out value="${i}"/></a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <!-- Nút Về Cuối -->
                <c:choose>
                    <c:when test="${pagedResult.hasNext}">
                        <a href="?page=<c:out value='${pagedResult.page + 1}'/>&amp;size=<c:out value='${pagedResult.size}'/>" class="page-link" title="Trang sau">&rsaquo;</a>
                        <a href="?page=<c:out value='${pagedResult.totalPages}'/>&amp;size=<c:out value='${pagedResult.size}'/>" class="page-link" title="Trang cuối">&raquo;</a>
                    </c:when>
                    <c:otherwise>
                        <span class="page-link disabled">&rsaquo;</span>
                        <span class="page-link disabled">&raquo;</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:if>

    <!-- Modal Import Excel -->
    <div class="excel-modal" id="modal-import-excel">
        <div class="excel-modal-content">
            <div class="excel-modal-header">
                <h3>Nhập Dữ Liệu Người Dùng Từ Excel</h3>
                <button type="button" class="excel-modal-close" id="btn-close-import-modal">&times;</button>
            </div>
            <form id="form-import-excel" enctype="multipart/form-data">
                <input type="hidden" name="csrfToken" id="csrfToken-field" value="${csrfToken}">
                <div class="excel-form-group">
                    <label for="excel-file">Chọn tệp Excel (.xlsx)</label>
                    <input type="file" id="excel-file" name="file" accept=".xlsx" class="excel-file-input" required>
                </div>
                <div class="btn-container justify-content-end">
                    <button type="submit" class="btn">Bắt Đầu Import</button>
                </div>
            </form>
        </div>
    </div>
</div>
</jsp:body>
</t:layout>
