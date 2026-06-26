<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="isEdit" value="${not empty product}" />
<t:layout title="${isEdit ? 'Cập nhật Sản phẩm' : 'Thêm Sản phẩm'}">
    <div class="container" style="max-width: 700px; margin: 30px auto; padding: 40px;">
        <h2 style="margin-top: 0; color: var(--primary); margin-bottom: 25px; text-align: center;">
            ${isEdit ? 'Cập nhật Sản phẩm' : 'Thêm Sản phẩm Mới'}
        </h2>

        <c:if test="${not empty error}">
            <div style="background: rgba(255, 114, 114, 0.1); border-left: 4px solid #FF7272; color: #FF7272; padding: 15px; border-radius: 8px; margin-bottom: 20px; font-size: 0.9rem;">
                ${error}
            </div>
        </c:if>

        <form action="${isEdit ? pageContext.request.contextPath.concat('/admin/products/edit/').concat(product.id) : pageContext.request.contextPath.concat('/admin/products/create')}" method="POST" enctype="multipart/form-data">
            
            <div class="form-group">
                <label for="categoryId">Danh mục sản phẩm</label>
                <select id="categoryId" name="categoryId" class="form-control" required style="width: 100%; padding: 12px; border-radius: 10px; border: 1px solid var(--border-color); background: var(--bg-card); color: var(--text-main);">
                    <option value="">-- Chọn danh mục --</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.id}" ${((isEdit && product.categoryId == cat.id) || (not isEdit && categoryId == cat.id)) ? 'selected' : ''}>
                            ${cat.name}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
                <div class="form-group">
                    <label for="sku">Mã SKU</label>
                    <input type="text" id="sku" name="sku" class="form-control" value="<c:out value="${isEdit ? product.sku : sku}"/>" required placeholder="Ví dụ: IPHONE15PM">
                </div>

                <div class="form-group">
                    <label for="price">Giá bán (VNĐ)</label>
                    <input type="number" id="price" name="price" class="form-control" value="${isEdit ? product.price : price}" required min="0" placeholder="Ví dụ: 30000000">
                </div>
            </div>

            <div class="form-group">
                <label for="name">Tên Sản phẩm</label>
                <input type="text" id="name" name="name" class="form-control" value="<c:out value="${isEdit ? product.name : name}"/>" required placeholder="Nhập tên sản phẩm đầy đủ">
            </div>

            <div class="form-group">
                <label for="description">Mô tả chi tiết</label>
                <textarea id="description" name="description" class="form-control" style="min-height: 120px; resize: vertical;" placeholder="Nhập mô tả sản phẩm, cấu hình kỹ thuật..."><c:out value="${isEdit ? product.description : description}"/></textarea>
            </div>

            <div class="form-group">
                <label for="imageFile">Ảnh sản phẩm</label>
                <c:if test="${isEdit && not empty product.imageUrl}">
                    <div style="margin-bottom: 12px;">
                        <img src="<c:out value='${product.imageUrl}'/>" alt="Ảnh hiện tại" style="max-width: 150px; border-radius: 8px; border: 1px solid var(--border-color); display: block;" />
                        <span style="font-size: 0.8rem; color: var(--text-muted);">Ảnh hiện tại</span>
                    </div>
                </c:if>
                <input type="file" id="imageFile" name="imageFile" class="form-control" accept="image/*" style="padding: 10px;" />
            </div>

            <div class="form-group">
                <label for="status">Trạng thái kinh doanh</label>
                <select id="status" name="status" class="form-control" required style="width: 100%; padding: 12px; border-radius: 10px; border: 1px solid var(--border-color); background: var(--bg-card); color: var(--text-main);">
                    <option value="ACTIVE" ${((isEdit && product.status == 'ACTIVE') || (not isEdit && status == 'ACTIVE')) ? 'selected' : ''}>Hoạt động</option>
                    <option value="INACTIVE" ${((isEdit && product.status == 'INACTIVE') || (not isEdit && status == 'INACTIVE')) ? 'selected' : ''}>Ngừng kinh doanh</option>
                </select>
            </div>

            <div style="display: flex; gap: 15px; margin-top: 35px;">
                <button type="submit" class="btn" style="flex: 1;">Lưu Sản phẩm</button>
                <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-secondary" style="flex: 1; text-align: center; line-height: 25px;">Hủy bỏ</a>
            </div>
        </form>
    </div>
</t:layout>
