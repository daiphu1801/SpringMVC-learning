<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Quản lý Danh mục">
    <div class="container container-md">
        <div class="flex-row-between mb-3">
            <h2 class="text-primary">Danh sách Danh mục</h2>
            <a href="${pageContext.request.contextPath}/admin/categories/create" class="btn">+ Thêm Danh mục</a>
        </div>

        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên Danh mục</th>
                        <th>Mã Code</th>
                        <th>Mô tả</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty categories}">
                            <tr>
                                <td colspan="4" class="text-center text-muted">
                                    Chưa có danh mục nào. Hãy tạo danh mục đầu tiên!
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="cat" items="${categories}">
                                <tr>
                                    <td class="font-bold">${cat.id}</td>
                                    <td class="text-main font-bold"><c:out value="${cat.name}"/></td>
                                    <td><code class="sku-code"><c:out value="${cat.code}"/></code></td>
                                    <td class="text-muted"><c:out value="${cat.description}"/></td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</t:layout>
