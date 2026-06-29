<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="isEdit" value="${not empty product}" />
<t:layout title="${isEdit ? 'Cập nhật Sản phẩm' : 'Thêm Sản phẩm'}">
    <div class="container container-sm">
        <h2 class="text-center text-primary mb-3">
            ${isEdit ? 'Cập nhật Sản phẩm' : 'Thêm Sản phẩm Mới'}
        </h2>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                ${error}
            </div>
        </c:if>

        <form action="${isEdit ? pageContext.request.contextPath.concat('/admin/products/edit/').concat(product.id) : pageContext.request.contextPath.concat('/admin/products/create')}" method="POST" enctype="multipart/form-data">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            
            <div class="form-group">
                <label for="categoryId">Danh mục sản phẩm</label>
                <select id="categoryId" name="categoryId" class="form-control" required>
                    <option value="">-- Chọn danh mục --</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.id}" ${((isEdit && product.categoryId == cat.id) || (not isEdit && categoryId == cat.id)) ? 'selected' : ''}>
                            <c:out value="${cat.name}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="grid-2-col">
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
                <textarea id="description" name="description" class="form-control textarea-field" placeholder="Nhập mô tả sản phẩm, cấu hình kỹ thuật..."><c:out value="${isEdit ? product.description : description}"/></textarea>
            </div>

            <div class="form-group">
                <label for="imageFile">Ảnh sản phẩm</label>
                <c:if test="${isEdit && not empty product.imageUrl}">
                    <div class="mb-2">
                        <img src="<c:out value='${product.imageUrl}'/>" alt="Ảnh hiện tại" class="img-preview" />
                        <span class="text-sm text-muted">Ảnh hiện tại</span>
                    </div>
                </c:if>
                <input type="file" id="imageFile" name="imageFile" class="form-control" accept="image/*" />
            </div>

            <div class="form-group">
                <label for="status">Trạng thái kinh doanh</label>
                <select id="status" name="status" class="form-control" required>
                    <option value="ACTIVE" ${((isEdit && product.status == 'ACTIVE') || (not isEdit && status == 'ACTIVE')) ? 'selected' : ''}>Hoạt động</option>
                    <option value="INACTIVE" ${((isEdit && product.status == 'INACTIVE') || (not isEdit && status == 'INACTIVE')) ? 'selected' : ''}>Ngừng kinh doanh</option>
                </select>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn flex-1">Lưu Sản phẩm</button>
                <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-secondary flex-1 text-center">Hủy bỏ</a>
            </div>
        </form>
    </div>
</t:layout>
