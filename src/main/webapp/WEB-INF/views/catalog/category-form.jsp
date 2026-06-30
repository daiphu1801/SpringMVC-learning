<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Thêm Danh mục">
    <div class="container container-sm">
        <h2 class="text-center text-primary mb-3">Tạo Danh mục Mới</h2>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                <c:out value="${error}"/>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/admin/categories/create" method="POST">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <div class="form-group">
                <label for="name">Tên Danh mục</label>
                <input type="text" id="name" name="name" class="form-control" value="<c:out value="${name}"/>" required placeholder="Nhập tên danh mục (Ví dụ: Điện thoại, Laptop...)">
            </div>

            <div class="form-group">
                <label for="code">Mã Code (Slug)</label>
                <input type="text" id="code" name="code" class="form-control" value="<c:out value="${code}"/>" required placeholder="Ví dụ: dien-thoai, laptop (viết liền, không dấu)">
                <small class="form-help mt-1">
                    Mã code viết thường không dấu, dùng để tạo đường dẫn thân thiện.
                </small>
            </div>

            <div class="form-group">
                <label for="description">Mô tả</label>
                <textarea id="description" name="description" class="form-control textarea-field" placeholder="Mô tả sơ lược về danh mục này..."><c:out value="${description}"/></textarea>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn flex-1">Lưu Danh mục</button>
                <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary flex-1 text-center">Hủy bỏ</a>
            </div>
        </form>
    </div>
</t:layout>
