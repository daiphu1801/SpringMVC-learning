<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Quản lý Sản phẩm">
    <div class="container" style="max-width: 1100px; margin: 30px auto; padding: 30px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px;">
            <h2 style="margin: 0; color: var(--primary);">Danh sách Sản phẩm</h2>
            <a href="${pageContext.request.contextPath}/admin/products/create" class="btn" style="padding: 10px 20px;">+ Thêm Sản phẩm</a>
        </div>

        <c:if test="${not empty error}">
            <div style="background: rgba(255, 114, 114, 0.1); border-left: 4px solid #FF7272; color: #FF7272; padding: 15px; border-radius: 8px; margin-bottom: 20px; font-size: 0.9rem;">
                ${error}
            </div>
        </c:if>

        <table style="width: 100%; border-collapse: collapse; margin-top: 15px;">
            <thead>
                <tr style="border-bottom: 2px solid var(--border-color); text-align: left;">
                    <th style="padding: 12px; color: var(--text-muted);">SKU</th>
                    <th style="padding: 12px; color: var(--text-muted); width: 80px;">Ảnh</th>
                    <th style="padding: 12px; color: var(--text-muted);">Tên Sản phẩm</th>
                    <th style="padding: 12px; color: var(--text-muted);">Danh mục</th>
                    <th style="padding: 12px; color: var(--text-muted);">Giá</th>
                    <th style="padding: 12px; color: var(--text-muted);">Trạng thái</th>
                    <th style="padding: 12px; color: var(--text-muted); text-align: center;">Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty products}">
                        <tr>
                            <td colspan="7" style="text-align: center; padding: 30px; color: var(--text-muted);">
                                Chưa có sản phẩm nào. Hãy tạo sản phẩm đầu tiên!
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="prod" items="${products}">
                            <tr style="border-bottom: 1px solid var(--border-color); transition: background 0.2s;" onmouseover="this.style.background='rgba(159, 161, 255, 0.03)'" onmouseout="this.style.background='transparent'">
                                <td style="padding: 15px 12px;"><code style="background: var(--bg-main); padding: 4px 8px; border-radius: 6px; color: var(--primary); font-family: monospace; font-weight: 600;">${prod.sku}</code></td>
                                <td style="padding: 15px 12px;">
                                    <c:choose>
                                        <c:when test="${not empty prod.imageUrl}">
                                            <img src="<c:out value='${prod.imageUrl}'/>" alt="${prod.name}" style="width: 50px; height: 50px; object-fit: cover; border-radius: 8px; border: 1px solid var(--border-color);" />
                                        </c:when>
                                        <c:otherwise>
                                            <div style="width: 50px; height: 50px; border-radius: 8px; background: linear-gradient(135deg, #7F84FF, #9FA1FF); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; font-size: 0.7rem;">
                                                NO IMG
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="padding: 15px 12px; color: var(--text-main); font-weight: 500;">${prod.name}</td>
                                <td style="padding: 15px 12px; color: var(--text-muted);">${prod.categoryName}</td>
                                <td style="padding: 15px 12px; font-weight: 600; color: #2ecc71;">
                                    <fmt:formatNumber value="${prod.price}" pattern="#,##0"/> đ
                                </td>
                                <td style="padding: 15px 12px;">
                                    <span style="padding: 6px 12px; border-radius: 20px; font-size: 0.8rem; font-weight: 600; background: ${prod.status == 'ACTIVE' ? 'rgba(46, 204, 113, 0.1)' : 'rgba(255, 114, 114, 0.1)'}; color: ${prod.status == 'ACTIVE' ? '#2ecc71' : '#ff7272'};">
                                        ${prod.statusDesc}
                                    </span>
                                </td>
                                <td style="padding: 15px 12px; text-align: center;">
                                    <div style="display: inline-flex; gap: 8px;">
                                        <a href="${pageContext.request.contextPath}/admin/products/edit/${prod.id}" class="btn btn-secondary" style="padding: 6px 12px; font-size: 0.8rem; box-shadow: none;">Sửa</a>
                                        <form action="${pageContext.request.contextPath}/admin/products/delete/${prod.id}" method="POST" style="margin: 0;" onsubmit="return confirm('Bạn có chắc chắn muốn xóa sản phẩm này?');">
                                            <button type="submit" class="btn" style="padding: 6px 12px; font-size: 0.8rem; background: #ff7272; box-shadow: none;">Xóa</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</t:layout>
