<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Đăng ký tài khoản" showHeader="false">
<div class="container login-container">
    <div class="login-header">
        <h1>Đăng ký</h1>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">
            ${error}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/register" method="post">
        <div class="form-group">
            <label for="username">Tên đăng nhập</label>
            <input type="text" id="username" name="username" class="form-control" 
                   value="${user.username}" placeholder="Nhập tên đăng nhập" required autofocus>
        </div>

        <div class="form-group">
            <label for="fullName">Họ và tên</label>
            <input type="text" id="fullName" name="fullName" class="form-control" 
                   value="${user.fullName}" placeholder="Nhập họ và tên" required>
        </div>

        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" class="form-control" 
                   value="${user.email}" placeholder="Nhập địa chỉ email" required>
        </div>

        <div class="form-group">
            <label for="phone">Số điện thoại</label>
            <input type="text" id="phone" name="phone" class="form-control" 
                   value="${user.phone}" placeholder="Nhập số điện thoại" required>
        </div>

        <div class="form-group">
            <label for="password">Mật khẩu</label>
            <input type="password" id="password" name="password" class="form-control" 
                   placeholder="Nhập mật khẩu" required>
        </div>

        <div style="margin-top: 20px;">
            <button type="submit" class="btn btn-full">Đăng ký</button>
        </div>
        
        <div style="margin-top: 15px; text-align: center;">
            <a href="${pageContext.request.contextPath}/login" style="color: var(--primary); text-decoration: none; font-weight: 500;">
                Đã có tài khoản? Đăng nhập ngay
            </a>
        </div>
    </form>
</div>
</t:layout>
