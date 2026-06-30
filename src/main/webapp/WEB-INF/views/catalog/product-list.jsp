<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Quản lý Sản phẩm">
    <jsp:attribute name="head">
        <script src="${pageContext.request.contextPath}/resources/js/pages/product-list.js?v=${appVersion}" defer></script>
    </jsp:attribute>
    <jsp:body>
    <div class="container">
        <div class="flex-row-between mb-3">
            <h2 class="text-primary">Danh sách Sản phẩm</h2>
            <a href="${pageContext.request.contextPath}/admin/products/create" class="btn">+ Thêm Sản phẩm</a>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                <c:out value="${error}"/>
            </div>
        </c:if>

        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>SKU</th>
                        <th class="w-80">Ảnh</th>
                        <th>Tên Sản phẩm</th>
                        <th>Danh mục</th>
                        <th>Giá</th>
                        <th>Trạng thái</th>
                        <th class="text-center">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty products}">
                            <tr>
                                <td colspan="7" class="text-center text-muted">
                                    Chưa có sản phẩm nào. Hãy tạo sản phẩm đầu tiên!
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="prod" items="${products}">
                                <tr>
                                    <td><code class="sku-code"><c:out value="${prod.sku}"/></code></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty prod.imageUrl}">
                                                <img src="<c:out value='${prod.imageUrl}'/>" alt="<c:out value='${prod.name}'/>" class="admin-prod-thumb" />
                                            </c:when>
                                            <c:otherwise>
                                                <div class="admin-prod-thumb-placeholder">
                                                    NO IMG
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-main font-bold"><c:out value="${prod.name}"/></td>
                                    <td class="text-muted"><c:out value="${prod.categoryName}"/></td>
                                    <td class="text-success font-bold">
                                        <fmt:formatNumber value="${prod.price}" pattern="#,##0"/> đ
                                    </td>
                                    <td>
                                        <span class="badge ${prod.status == 'ACTIVE' ? 'badge-success' : 'badge-danger'}">
                                            ${prod.statusDesc}
                                        </span>
                                    </td>
                                    <td class="text-center">
                                        <div class="d-inline-flex gap-2">
                                            <a href="${pageContext.request.contextPath}/admin/products/edit/${prod.id}" class="btn btn-secondary btn-sm">Sửa</a>
                                            <form action="${pageContext.request.contextPath}/admin/products/delete/${prod.id}" method="POST" class="m-0 delete-product-form">
                                                <input type="hidden" name="csrfToken" value="${csrfToken}">
                                                <button type="submit" class="btn btn-danger btn-sm">Xóa</button>
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
    </div>
    </jsp:body>
</t:layout>
