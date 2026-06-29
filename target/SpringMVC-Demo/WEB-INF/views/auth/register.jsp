<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Đăng ký tài khoản" showHeader="false">
<jsp:attribute name="head">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/auth.css?v=${appVersion}">
</jsp:attribute>
<jsp:body>
<div class="container login-container">
    <div class="login-header">
        <h1>Đăng ký</h1>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/register" method="post">
        <input type="hidden" name="csrfToken" value="${csrfToken}">
        <div class="form-group">
            <label for="username">Tên đăng nhập</label>
            <input type="text" id="username" name="username" class="form-control" 
                   value="<c:out value='${user.username}'/>" placeholder="Nhập tên đăng nhập" required autofocus>
        </div>

        <div class="form-group">
            <label for="fullName">Họ và tên</label>
            <input type="text" id="fullName" name="fullName" class="form-control" 
                   value="<c:out value='${user.fullName}'/>" placeholder="Nhập họ và tên" required>
        </div>

        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" class="form-control" 
                   value="<c:out value='${user.email}'/>" placeholder="Nhập địa chỉ email" required>
        </div>

        <div class="form-group">
            <label for="phone">Số điện thoại</label>
            <input type="text" id="phone" name="phone" class="form-control" 
                   value="<c:out value='${user.phone}'/>" placeholder="Nhập số điện thoại" required>
        </div>

        <div class="form-group">
            <label for="password">Mật khẩu</label>
            <input type="password" id="password" name="password" class="form-control" 
                   placeholder="Nhập mật khẩu" required>
        </div>

        <button type="submit" class="btn btn-full">Đăng ký</button>
        
        <div class="auth-redirect-wrapper">
            <a href="${pageContext.request.contextPath}/login" class="auth-redirect-link">
                Đã có tài khoản? Đăng nhập ngay
            </a>
        </div>
    </form>
</div>
</jsp:body>
</t:layout>
