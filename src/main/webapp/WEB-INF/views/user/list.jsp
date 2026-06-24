<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ taglib prefix="c"
           uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách người dùng</title>
</head>

<body>

<h1>Danh sách người dùng</h1>

<p>
    <a href="${pageContext.request.contextPath}/users/create">
        Thêm người dùng
    </a>
</p>

<table border="1" cellpadding="8" cellspacing="0">
    <thead>
    <tr>
        <th>ID</th>
        <th>Username</th>
        <th>Họ tên</th>
        <th>Email</th>
        <th>Số điện thoại</th>
        <th>Trạng thái</th>
        <th>Ngày tạo</th>
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
                <c:out value="${user.status}"/>
            </td>

            <td>
                <c:out value="${user.createdAt}"/>
            </td>

            <td>
                <a href="${pageContext.request.contextPath}/users/edit/${user.id}">
                    Sửa
                </a>

                <form
                        action="${pageContext.request.contextPath}/users/${user.id}/delete"
                        method="post"
                        style="display:inline">

                    <button type="submit">
                        Xóa
                    </button>
                </form>
            </td>
        </tr>
    </c:forEach>

    <c:if test="${empty users}">
        <tr>
            <td colspan="8">
                Chưa có dữ liệu
            </td>
        </tr>
    </c:if>

    </tbody>
</table>

</body>
</html>