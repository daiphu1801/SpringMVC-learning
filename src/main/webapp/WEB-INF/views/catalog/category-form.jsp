<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Thêm Danh mục">
    <div class="container" style="max-width: 600px; margin: 30px auto; padding: 40px;">
        <h2 style="margin-top: 0; color: var(--primary); margin-bottom: 25px; text-align: center;">Tạo Danh mục Mới</h2>

        <c:if test="${not empty error}">
            <div style="background: rgba(255, 114, 114, 0.1); border-left: 4px solid #FF7272; color: #FF7272; padding: 15px; border-radius: 8px; margin-bottom: 20px; font-size: 0.9rem;">
                ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/admin/categories/create" method="POST">
            <div class="form-group">
                <label for="name">Tên Danh mục</label>
                <input type="text" id="name" name="name" class="form-control" value="<c:out value="${name}"/>" required placeholder="Nhập tên danh mục (Ví dụ: Điện thoại, Laptop...)">
            </div>

            <div class="form-group">
                <label for="code">Mã Code (Slug)</label>
                <input type="text" id="code" name="code" class="form-control" value="<c:out value="${code}"/>" required placeholder="Ví dụ: dien-thoai, laptop (viết liền, không dấu)">
                <small style="color: var(--text-muted); font-size: 0.8rem; display: block; margin-top: 5px;">
                    Mã code viết thường không dấu, dùng để tạo đường dẫn thân thiện.
                </small>
            </div>

            <div class="form-group">
                <label for="description">Mô tả</label>
                <textarea id="description" name="description" class="form-control" style="min-height: 100px; resize: vertical;" placeholder="Mô tả sơ lược về danh mục này..."><c:out value="${description}"/></textarea>
            </div>

            <div style="display: flex; gap: 15px; margin-top: 30px;">
                <button type="submit" class="btn" style="flex: 1;">Lưu Danh mục</button>
                <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary" style="flex: 1; text-align: center; line-height: 25px;">Hủy bỏ</a>
            </div>
        </form>
    </div>
</t:layout>
