<%@tag description="Layout" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@attribute name="title" required="false" %>
<%@attribute name="showHeader" type="java.lang.Boolean" required="false" %>
<%@attribute name="isAdminLayout" type="java.lang.Boolean" required="false" %>
<%@attribute name="head" fragment="true" required="false" %>
<c:set var="showNav" value="${empty showHeader ? true : showHeader}" />

<c:set var="isAdmin" value="${empty isAdminLayout ? false : isAdminLayout}" />
<c:if test="${empty isAdminLayout}">
    <%-- Auto-detection of Admin Page based on request URI --%>
    <c:set var="reqUri" value="${requestScope['jakarta.servlet.forward.request_uri']}" />
    <c:if test="${empty reqUri}">
        <c:set var="reqUri" value="${pageContext.request.requestURI}" />
    </c:if>
    <c:if test="${not empty reqUri}">
        <c:if test="${(reqUri.contains('/admin/') || reqUri.contains('/users')) && !reqUri.contains('/users/addresses')}">
            <c:set var="isAdmin" value="true" />
        </c:if>
    </c:if>
</c:if>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty title ? 'Spring MVC Demo' : title}</title>
    <%-- Preconnect to external asset servers to speed up initial network requests --%>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="preconnect" href="https://img.vietqr.io">
    <%-- Static resources with cache busting version string --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/global.css?v=${appVersion}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/layout.css?v=${appVersion}">
    <script src="${pageContext.request.contextPath}/resources/js/layout.js?v=${appVersion}" defer></script>
    <jsp:invoke fragment="head"/>
</head>
<body>
    <div class="app-wrapper">
        <%-- Header Navigation --%>
        <c:if test="${not empty sessionScope.currentUser && showNav}">
            <c:choose>
                <c:when test="${isAdmin}">
                    <%-- ADMIN HEADER --%>
                    <div class="nav-container admin-nav">
                        <div class="nav-content">
                            <%-- Brand Logo / Name --%>
                            <div class="brand-wrapper">
                                <div class="brand-icon admin-icon">
                                    A
                                </div>
                                <div class="brand-text">
                                    <span class="brand-title">SpringMVC Admin</span>
                                    <span class="brand-subtitle admin-subtitle">Hệ thống quản trị</span>
                                </div>
                            </div>

                            <%-- Navigation Links --%>
                            <div class="nav-menu">
                                <a href="${pageContext.request.contextPath}/admin/products" class="nav-link">Quản lý Sản phẩm</a>
                                <a href="${pageContext.request.contextPath}/admin/categories" class="nav-link">Quản lý Danh mục</a>
                                <a href="${pageContext.request.contextPath}/admin/orders" class="nav-link">Quản lý Đơn hàng</a>
                                <a href="${pageContext.request.contextPath}/users" class="nav-link">Quản lý User</a>
                                
                                <span class="nav-separator"></span>
                                
                                <a href="${pageContext.request.contextPath}/products" class="nav-link nav-link-outline">Về Cửa hàng &rarr;</a>
                            </div>

                            <%-- User Account / Logout --%>
                            <div class="user-profile-wrapper">
                                <div class="user-info">
                                    <span class="user-fullname"><c:out value="${sessionScope.currentUser.fullName}"/></span>
                                    <span class="user-role-badge admin-role"><c:out value="${sessionScope.currentUser.role}"/></span>
                                </div>
                                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-logout">
                                    Đăng xuất
                                </a>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <%-- USER HEADER --%>
                    <div class="nav-container user-nav">
                        <div class="nav-content">
                            <%-- Brand Logo / Name --%>
                            <div class="brand-wrapper">
                                <div class="brand-icon user-icon">
                                    S
                                </div>
                                <span class="brand-title">SpringMVC Shop</span>
                            </div>

                            <%-- Navigation Links --%>
                            <div class="nav-menu">
                                <a href="${pageContext.request.contextPath}/products" class="nav-link">Cửa hàng</a>
                                <a href="${pageContext.request.contextPath}/cart" class="nav-link">🛒 Giỏ hàng</a>
                                <a href="${pageContext.request.contextPath}/orders" class="nav-link">Đơn hàng</a>
                                <a href="${pageContext.request.contextPath}/users/addresses" class="nav-link">Sổ địa chỉ</a>
                                <a href="${pageContext.request.contextPath}/architecture" class="nav-link">Kiến trúc</a>
                                
                                <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                                    <span class="nav-separator"></span>
                                    <a href="${pageContext.request.contextPath}/admin/products" class="nav-link nav-link-outline">Trang Quản trị &rarr;</a>
                                </c:if>
                            </div>

                            <%-- User Account / Logout --%>
                            <div class="user-profile-wrapper">
                                <div class="user-info">
                                    <span class="user-fullname"><c:out value="${sessionScope.currentUser.fullName}"/></span>
                                    <span class="user-role-badge user-role"><c:out value="${sessionScope.currentUser.role}"/></span>
                                </div>
                                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-logout">
                                    Đăng xuất
                                </a>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>

        <%-- Main Content Body --%>
        <jsp:doBody/>

        <%-- Footer --%>
        <div class="footer">
            &copy; 2026 SpringMVC-Demo &bull; Phát triển theo mô hình Clean Architecture & DDD &bull; 
            <a href="${pageContext.request.contextPath}/architecture" class="footer-link">Tìm hiểu kiến trúc</a>
        </div>
    </div>
</body>
</html>
