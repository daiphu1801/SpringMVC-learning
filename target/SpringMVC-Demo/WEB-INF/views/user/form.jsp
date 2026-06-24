<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8" %>

<%--@elvariable id="user" type="com.examp.springmvc.model.User"--%>

<%@ taglib prefix="c"
           uri="jakarta.tags.core" %>

<%@ taglib prefix="form"
           uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">

  <title>
    <c:choose>
      <c:when test="${empty user.id}">
        Thêm người dùng
      </c:when>

      <c:otherwise>
        Cập nhật người dùng
      </c:otherwise>
    </c:choose>
  </title>
</head>

<body>

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
        method="post">

  <div>
    <label for="username">Tên đăng nhập</label>

    <form:input
            id="username"
            path="username"/>

    <form:errors
            path="username"/>
  </div>

  <br>

  <div>
    <label for="fullName">Họ và tên</label>

    <form:input
            id="fullName"
            path="fullName"/>

    <form:errors
            path="fullName"/>
  </div>

  <br>

  <div>
    <label for="email">Email</label>

    <form:input
            id="email"
            path="email"
            type="email"/>

    <form:errors
            path="email"/>
  </div>

  <br>

  <div>
    <label for="phone">Số điện thoại</label>

    <form:input
            id="phone"
            path="phone"/>

    <form:errors
            path="phone"/>
  </div>

  <br>

  <div>
    <label for="status">Trạng thái</label>

    <form:select
            id="status"
            path="status">

      <form:option
              value="ACTIVE"
              label="Hoạt động"/>

      <form:option
              value="INACTIVE"
              label="Không hoạt động"/>

    </form:select>
  </div>

  <br>

  <button type="submit">
    Lưu
  </button>

  <a href="${pageContext.request.contextPath}/users">
    Quay lại
  </a>

</form:form>

</body>
</html>