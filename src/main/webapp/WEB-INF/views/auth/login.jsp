<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Đăng nhập hệ thống" showHeader="false">
<jsp:attribute name="head">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/auth.css?v=${appVersion}">
    <script src="${pageContext.request.contextPath}/resources/js/pages/auth.js?v=${appVersion}" defer></script>
</jsp:attribute>
<jsp:body>
<div class="container login-container">
    <div class="login-header">
        <h1>Đăng nhập</h1>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <c:if test="${not empty success}">
        <div class="alert alert-success">
            <c:out value="${success}"/>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <input type="hidden" name="csrfToken" value="${csrfToken}">
        <div class="form-group">
            <label for="username">Tên đăng nhập</label>
            <input type="text" id="username" name="username" class="form-control" 
                   value="<c:out value='${username}'/>" placeholder="Nhập tên đăng nhập" required autofocus>
        </div>

        <div class="form-group">
            <label for="password">Mật khẩu</label>
            <input type="password" id="password" name="password" class="form-control" 
                   placeholder="Nhập mật khẩu" required>
        </div>

        <c:if test="${demoFillEnabled}">
            <div class="demo-account-box">
                <div class="demo-account-title">🔑 Tài khoản mẫu</div>
                <div>Quản trị viên: <strong>adminnn</strong> / <strong>123456</strong></div>
                <button type="button" id="btn-demo-fill" class="btn-demo-fill">
                    ⚡ Điền nhanh
                </button>
            </div>
        </c:if>

        <button type="submit" class="btn btn-full">Đăng nhập</button>
        
        <div class="auth-redirect-wrapper">
            <a href="${pageContext.request.contextPath}/register" class="auth-redirect-link">
                Chưa có tài khoản? Đăng ký ngay
            </a>
        </div>
    </form>
</div>
</jsp:body>
</t:layout>
