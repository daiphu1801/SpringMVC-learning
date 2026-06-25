<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ taglib prefix="c"
           uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập hệ thống</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>

<body>

<div class="container login-container">
    <div class="login-header">
        <h1>Đăng nhập</h1>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">
            ${error}
        </div>
    </c:if>

    <c:if test="${not empty success}">
        <div class="alert alert-success">
            ${success}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <div class="form-group">
            <label for="username">Tên đăng nhập</label>
            <input type="text" id="username" name="username" class="form-control" 
                   value="${username}" placeholder="Nhập tên đăng nhập" required autofocus>
        </div>

        <div class="form-group">
            <label for="password">Mật khẩu</label>
            <input type="password" id="password" name="password" class="form-control" 
                   placeholder="Nhập mật khẩu" required>
        </div>

        <div style="margin-top: 10px; margin-bottom: 20px; font-size: 0.9rem; color: var(--text-muted);">
            Tài khoản mẫu: <br>
            - Quản trị viên: <strong>admin</strong> / <strong>password123</strong> <br>
            - Người dùng: (các tài khoản sẵn có) / <strong>password123</strong>
        </div>

        <button type="submit" class="btn btn-full">Đăng nhập</button>
        
        <div style="margin-top: 15px; text-align: center;">
            <a href="${pageContext.request.contextPath}/register" style="color: var(--primary); text-decoration: none; font-weight: 500;">
                Chưa có tài khoản? Đăng ký ngay
            </a>
        </div>
    </form>
</div>

</body>
</html>
