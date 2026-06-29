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
        <div class="btn-container mb-3">
            <a href="${pageContext.request.contextPath}/users/create" class="btn">
                Thêm người dùng
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

            <c:forEach var="user" items="${users}">
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

            <c:if test="${empty users}">
                <tr>
                    <td colspan="7" class="text-center text-muted">
                        Chưa có dữ liệu người dùng
                    </td>
                </tr>
            </c:if>

            </tbody>
        </table>
    </div>
</div>
</jsp:body>
</t:layout>
