<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Đăng nhập hệ thống" showHeader="false">
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

        <div style="margin-top: 10px; margin-bottom: 20px; padding: 12px 16px; background: rgba(127,132,255,0.08); border: 1px dashed var(--primary); border-radius: 10px; font-size: 0.85rem; color: var(--text-muted);">
            <div style="font-weight: 600; color: var(--primary); margin-bottom: 6px;">🔑 Tài khoản mẫu</div>
            <div>Quản trị viên: <strong>adminnn</strong> / <strong>123456</strong></div>
            <button type="button" id="btn-demo-fill"
                    onclick="document.getElementById('username').value='adminnn';document.getElementById('password').value='123456';"
                    style="margin-top: 8px; padding: 4px 12px; font-size: 0.8rem; border: 1px solid var(--primary); background: transparent; color: var(--primary); border-radius: 6px; cursor: pointer;">
                ⚡ Điền nhanh
            </button>
        </div>

        <button type="submit" class="btn btn-full">Đăng nhập</button>
        
        <div style="margin-top: 15px; text-align: center;">
            <a href="${pageContext.request.contextPath}/register" style="color: var(--primary); text-decoration: none; font-weight: 500;">
                Chưa có tài khoản? Đăng ký ngay
            </a>
        </div>
    </form>
</div>
</t:layout>
