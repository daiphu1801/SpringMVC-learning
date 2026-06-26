<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Quản lý Danh mục">
    <div class="container" style="max-width: 900px; margin: 30px auto; padding: 30px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px;">
            <h2 style="margin: 0; color: var(--primary);">Danh sách Danh mục</h2>
            <a href="${pageContext.request.contextPath}/admin/categories/create" class="btn" style="padding: 10px 20px;">+ Thêm Danh mục</a>
        </div>

        <table style="width: 100%; border-collapse: collapse; margin-top: 15px;">
            <thead>
                <tr style="border-bottom: 2px solid var(--border-color); text-align: left;">
                    <th style="padding: 12px; color: var(--text-muted);">ID</th>
                    <th style="padding: 12px; color: var(--text-muted);">Tên Danh mục</th>
                    <th style="padding: 12px; color: var(--text-muted);">Mã Code</th>
                    <th style="padding: 12px; color: var(--text-muted);">Mô tả</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty categories}">
                        <tr>
                            <td colspan="4" style="text-align: center; padding: 30px; color: var(--text-muted);">
                                Chưa có danh mục nào. Hãy tạo danh mục đầu tiên!
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="cat" items="${categories}">
                            <tr style="border-bottom: 1px solid var(--border-color); transition: background 0.2s;" onmouseover="this.style.background='rgba(159, 161, 255, 0.03)'" onmouseout="this.style.background='transparent'">
                                <td style="padding: 15px 12px; font-weight: 600;">${cat.id}</td>
                                <td style="padding: 15px 12px; color: var(--text-main); font-weight: 500;">${cat.name}</td>
                                <td style="padding: 15px 12px;"><code style="background: var(--bg-main); padding: 4px 8px; border-radius: 6px; color: var(--primary); font-family: monospace;">${cat.code}</code></td>
                                <td style="padding: 15px 12px; color: var(--text-muted);">${cat.description}</td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</t:layout>
