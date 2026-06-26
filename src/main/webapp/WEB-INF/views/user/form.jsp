<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<c:choose>
  <c:when test="${empty user.id}">
    <c:set var="pageTitle" value="Thêm người dùng" />
  </c:when>
  <c:otherwise>
    <c:set var="pageTitle" value="Cập nhật người dùng" />
  </c:otherwise>
</c:choose>

<t:layout title="${pageTitle}">
<div class="container">
  <c:choose>
    <c:when test="${empty user.id}">
      <h1>Thêm người dùng</h1>

      <c:set
              var="formAction"
              value="${pageContext.request.contextPath}/users"/>
    </c:when>

    <c:otherwise>
      <h1>Cập nhật người dùng</h1>

      <c:set
              var="formAction"
              value="${pageContext.request.contextPath}/users/${user.id}"/>
    </c:otherwise>
  </c:choose>

  <form:form
          modelAttribute="user"
          action="${formAction}"
          method="post"
          style="margin-top: 30px;">

    <div class="form-group">
      <label for="username">Tên đăng nhập</label>

      <form:input
              id="username"
              path="username"
              class="form-control"
              placeholder="Nhập tên đăng nhập..."/>

      <form:errors
              path="username"
              element="span"
              cssClass="form-error"/>
    </div>

    <div class="form-group">
      <label for="fullName">Họ và tên</label>

      <form:input
              id="fullName"
              path="fullName"
              class="form-control"
              placeholder="Nhập họ và tên..."/>

      <form:errors
              path="fullName"
              element="span"
              cssClass="form-error"/>
    </div>

    <div class="form-group">
      <label for="email">Email</label>

      <form:input
              id="email"
              path="email"
              type="email"
              class="form-control"
              placeholder="Nhập địa chỉ email..."/>

      <form:errors
              path="email"
              element="span"
              cssClass="form-error"/>
    </div>

    <div class="form-group">
      <label for="phone">Số điện thoại</label>

      <form:input
              id="phone"
              path="phone"
              class="form-control"
              placeholder="Nhập số điện thoại..."/>

      <form:errors
              path="phone"
              element="span"
              cssClass="form-error"/>
    </div>

    <div class="form-group">
      <label for="status">Trạng thái</label>

      <form:select
              id="status"
              path="status"
              class="form-control">

        <form:option
                value="ACTIVE"
                label="Hoạt động"/>

        <form:option
                value="INACTIVE"
                label="Không hoạt động"/>

      </form:select>
    </div>

    <div class="form-group">
      <label for="role">Vai trò</label>
      <form:select id="role" path="role" class="form-control">
        <form:option value="USER" label="User (Người dùng thường)"/>
        <form:option value="ADMIN" label="Admin (Quản trị viên)"/>
      </form:select>
      <form:errors path="role" element="span" cssClass="form-error"/>
    </div>

    <div class="form-group">
      <label for="password">
        Mật khẩu
        <c:if test="${not empty user.id}">
          <span style="font-size: 0.85rem; color: var(--text-muted); font-weight: normal;">(Để trống nếu không thay đổi)</span>
        </c:if>
      </label>
      <form:password id="password" path="password" class="form-control" placeholder="Nhập mật khẩu..." showPassword="false" required="${empty user.id ? 'true' : 'false'}"/>
      <form:errors path="password" element="span" cssClass="form-error"/>
    </div>

    <div class="form-actions">
      <button type="submit" class="btn">
        Lưu người dùng
      </button>

      <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary">
        Quay lại
      </a>
    </div>

  </form:form>
</div>
</t:layout>