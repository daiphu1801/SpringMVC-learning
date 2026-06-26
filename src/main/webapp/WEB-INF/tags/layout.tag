<%@tag description="Layout" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@attribute name="title" required="false" %>
<%@attribute name="showHeader" type="java.lang.Boolean" required="false" %>
<%@attribute name="isAdminLayout" type="java.lang.Boolean" required="false" %>
<%@attribute name="head" fragment="true" required="false" %>
<c:set var="showNav" value="${empty showHeader ? true : showHeader}" />

<c:set var="isAdmin" value="${empty isAdminLayout ? false : isAdminLayout}" />
<c:if test="${empty isAdminLayout}">
    <!-- Auto-detection of Admin Page based on request URI -->
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <jsp:invoke fragment="head"/>
</head>
<body>
    <div style="display: flex; flex-direction: column; align-items: center; width: 100%;">
        <!-- Header Navigation -->
        <c:if test="${not empty sessionScope.currentUser && showNav}">
            <c:choose>
                <c:when test="${isAdmin}">
                    <!-- ADMIN HEADER -->
                    <div class="container" style="margin-bottom: 30px; padding: 15px 30px; border-radius: 16px; width: 100%; max-width: 1100px; margin-left: auto; margin-right: auto; border: 1px solid rgba(255, 107, 107, 0.2); box-shadow: 0 4px 20px rgba(255, 107, 107, 0.05); background: #FFFDFD;">
                        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 20px;">
                            <!-- Brand Logo / Name -->
                            <div style="display: flex; align-items: center; gap: 10px;">
                                <div style="width: 38px; height: 38px; background: linear-gradient(135deg, #FF6B6B, #FF8E53); border-radius: 10px; display: flex; align-items: center; justify-content: center; color: white; font-weight: 800; font-size: 1.2rem; box-shadow: 0 4px 10px rgba(255, 107, 107, 0.3);">
                                    A
                                </div>
                                <div style="display: flex; flex-direction: column;">
                                    <span style="font-weight: 700; font-size: 1.1rem; color: var(--text-main); letter-spacing: 0.5px; line-height: 1.2;">SpringMVC Admin</span>
                                    <span style="font-size: 0.7rem; color: #FF6B6B; font-weight: 700; text-transform: uppercase; letter-spacing: 1px;">Hệ thống quản trị</span>
                                </div>
                            </div>

                            <!-- Navigation Links -->
                            <div style="display: flex; align-items: center; gap: 8px; flex-wrap: wrap;">
                                <a href="${pageContext.request.contextPath}/admin/products" style="color: var(--text-main); text-decoration: none; font-weight: 600; font-size: 0.9rem; padding: 8px 16px; border-radius: 8px; transition: all var(--transition-speed);" onmouseover="this.style.background='rgba(255, 107, 107, 0.08)'; this.style.color='#FF6B6B';" onmouseout="this.style.background='transparent'; this.style.color='var(--text-main)';">Quản lý Sản phẩm</a>
                                <a href="${pageContext.request.contextPath}/admin/categories" style="color: var(--text-main); text-decoration: none; font-weight: 600; font-size: 0.9rem; padding: 8px 16px; border-radius: 8px; transition: all var(--transition-speed);" onmouseover="this.style.background='rgba(255, 107, 107, 0.08)'; this.style.color='#FF6B6B';" onmouseout="this.style.background='transparent'; this.style.color='var(--text-main)';">Quản lý Danh mục</a>
                                <a href="${pageContext.request.contextPath}/admin/orders" style="color: var(--text-main); text-decoration: none; font-weight: 600; font-size: 0.9rem; padding: 8px 16px; border-radius: 8px; transition: all var(--transition-speed);" onmouseover="this.style.background='rgba(255, 107, 107, 0.08)'; this.style.color='#FF6B6B';" onmouseout="this.style.background='transparent'; this.style.color='var(--text-main)';">Quản lý Đơn hàng</a>
                                <a href="${pageContext.request.contextPath}/users" style="color: var(--text-main); text-decoration: none; font-weight: 600; font-size: 0.9rem; padding: 8px 16px; border-radius: 8px; transition: all var(--transition-speed);" onmouseover="this.style.background='rgba(255, 107, 107, 0.08)'; this.style.color='#FF6B6B';" onmouseout="this.style.background='transparent'; this.style.color='var(--text-main)';">Quản lý User</a>
                                
                                <span style="height: 20px; width: 1px; background-color: var(--border-color); margin: 0 8px;"></span>
                                
                                <a href="${pageContext.request.contextPath}/products" style="color: var(--primary); text-decoration: none; font-weight: 700; font-size: 0.9rem; padding: 8px 16px; border-radius: 8px; border: 1px solid var(--primary); transition: all var(--transition-speed);" onmouseover="this.style.background='var(--primary)'; this.style.color='white';" onmouseout="this.style.background='transparent'; this.style.color='var(--primary)';">Về Cửa hàng &rarr;</a>
                            </div>

                            <!-- User Account / Logout -->
                            <div style="display: flex; align-items: center; gap: 15px;">
                                <div style="display: flex; align-items: center; gap: 8px; justify-content: flex-end;">
                                    <span style="font-weight: 600; font-size: 0.9rem; color: var(--text-main);"><c:out value="${sessionScope.currentUser.fullName}"/></span>
                                    <span style="font-size: 0.75rem; color: white; font-weight: 700; text-transform: uppercase; background: #FF6B6B; padding: 2px 8px; border-radius: 20px;"><c:out value="${sessionScope.currentUser.role}"/></span>
                                </div>
                                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary" style="padding: 8px 16px; font-size: 0.85rem; border-radius: 10px; box-shadow: none;">
                                    Đăng xuất
                                </a>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- USER HEADER -->
                    <div class="container" style="margin-bottom: 30px; padding: 15px 30px; border-radius: 16px; width: 100%; max-width: 1100px; margin-left: auto; margin-right: auto; border: 1px solid var(--border-color); box-shadow: 0 4px 20px rgba(159, 161, 255, 0.05);">
                        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 20px;">
                            <!-- Brand Logo / Name -->
                            <div style="display: flex; align-items: center; gap: 10px;">
                                <div style="width: 38px; height: 38px; background: linear-gradient(135deg, var(--primary), var(--primary-light)); border-radius: 10px; display: flex; align-items: center; justify-content: center; color: white; font-weight: 800; font-size: 1.2rem; box-shadow: 0 4px 10px rgba(159, 161, 255, 0.3);">
                                    S
                                </div>
                                <span style="font-weight: 700; font-size: 1.15rem; color: var(--text-main); letter-spacing: 0.5px;">SpringMVC Shop</span>
                            </div>

                            <!-- Navigation Links -->
                            <div style="display: flex; align-items: center; gap: 8px; flex-wrap: wrap;">
                                <a href="${pageContext.request.contextPath}/products" style="color: var(--text-main); text-decoration: none; font-weight: 600; font-size: 0.9rem; padding: 8px 16px; border-radius: 8px; transition: all var(--transition-speed);" onmouseover="this.style.background='rgba(159, 161, 255, 0.08)'; this.style.color='var(--primary)';" onmouseout="this.style.background='transparent'; this.style.color='var(--text-main)';">Cửa hàng</a>
                                <a href="${pageContext.request.contextPath}/cart" style="color: var(--text-main); text-decoration: none; font-weight: 600; font-size: 0.9rem; padding: 8px 16px; border-radius: 8px; transition: all var(--transition-speed);" onmouseover="this.style.background='rgba(159, 161, 255, 0.08)'; this.style.color='var(--primary)';" onmouseout="this.style.background='transparent'; this.style.color='var(--text-main)';">🛒 Giỏ hàng</a>
                                <a href="${pageContext.request.contextPath}/orders" style="color: var(--text-main); text-decoration: none; font-weight: 600; font-size: 0.9rem; padding: 8px 16px; border-radius: 8px; transition: all var(--transition-speed);" onmouseover="this.style.background='rgba(159, 161, 255, 0.08)'; this.style.color='var(--primary)';" onmouseout="this.style.background='transparent'; this.style.color='var(--text-main)';">Đơn hàng</a>
                                <a href="${pageContext.request.contextPath}/users/addresses" style="color: var(--text-main); text-decoration: none; font-weight: 600; font-size: 0.9rem; padding: 8px 16px; border-radius: 8px; transition: all var(--transition-speed);" onmouseover="this.style.background='rgba(159, 161, 255, 0.08)'; this.style.color='var(--primary)';" onmouseout="this.style.background='transparent'; this.style.color='var(--text-main)';">Sổ địa chỉ</a>
                                <a href="${pageContext.request.contextPath}/architecture" style="color: var(--text-main); text-decoration: none; font-weight: 600; font-size: 0.9rem; padding: 8px 16px; border-radius: 8px; transition: all var(--transition-speed);" onmouseover="this.style.background='rgba(159, 161, 255, 0.08)'; this.style.color='var(--primary)';" onmouseout="this.style.background='transparent'; this.style.color='var(--text-main)';">Kiến trúc</a>
                                
                                <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                                    <span style="height: 20px; width: 1px; background-color: var(--border-color); margin: 0 8px;"></span>
                                    <a href="${pageContext.request.contextPath}/admin/products" style="color: #FF6B6B; text-decoration: none; font-weight: 700; font-size: 0.9rem; padding: 8px 16px; border-radius: 8px; border: 1px solid #FF6B6B; transition: all var(--transition-speed);" onmouseover="this.style.background='#FF6B6B'; this.style.color='white';" onmouseout="this.style.background='transparent'; this.style.color='#FF6B6B';">Trang Quản trị &rarr;</a>
                                </c:if>
                            </div>

                            <!-- User Account / Logout -->
                            <div style="display: flex; align-items: center; gap: 15px;">
                                <div style="display: flex; align-items: center; gap: 8px; justify-content: flex-end;">
                                    <span style="font-weight: 600; font-size: 0.9rem; color: var(--text-main);"><c:out value="${sessionScope.currentUser.fullName}"/></span>
                                    <span style="font-size: 0.75rem; color: var(--primary); font-weight: 700; text-transform: uppercase; background: rgba(159, 161, 255, 0.1); padding: 2px 8px; border-radius: 20px;"><c:out value="${sessionScope.currentUser.role}"/></span>
                                </div>
                                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary" style="padding: 8px 16px; font-size: 0.85rem; border-radius: 10px; box-shadow: none;">
                                    Đăng xuất
                                </a>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>

        <!-- Main Content Body -->
        <jsp:doBody/>

        <!-- Footer -->
        <div style="text-align: center; margin-top: 50px; padding: 25px 20px; color: var(--text-muted); font-size: 0.85rem; border-top: 1px solid var(--border-color); width: 100%; max-width: 1100px; margin-left: auto; margin-right: auto;">
            &copy; 2026 SpringMVC-Demo &bull; Phát triển theo mô hình Clean Architecture & DDD &bull; 
            <a href="${pageContext.request.contextPath}/architecture" style="color: var(--primary); text-decoration: none; font-weight: 500;">Tìm hiểu kiến trúc</a>
        </div>
    </div>
</body>
</html>
