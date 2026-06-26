<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="${product.name}">
    <div class="container" style="max-width: 900px; margin: 30px auto; padding: 40px; display: grid; grid-template-columns: 350px 1fr; gap: 40px; border-radius: 20px;">
        
        <!-- Left Column: Product Image -->
        <div style="height: 350px; border-radius: 16px; display: flex; align-items: center; justify-content: center; position: relative; overflow: hidden; background: var(--bg-card); border: 1px solid var(--border-color);">
            <c:choose>
                <c:when test="${not empty product.imageUrl}">
                    <img src="<c:out value='${product.imageUrl}'/>" alt="${product.name}" style="width: 100%; height: 100%; object-fit: cover;" />
                </c:when>
                <c:otherwise>
                    <div style="width: 100%; height: 100%; background: linear-gradient(135deg, #7F84FF, #9FA1FF); display: flex; align-items: center; justify-content: center;">
                        <span style="color: white; font-weight: 800; font-size: 3.5rem; opacity: 0.25;">${product.sku}</span>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Right Column: Product Info -->
        <div style="display: flex; flex-direction: column; justify-content: space-between; height: 100%;">
            <div>
                <span style="font-size: 0.85rem; background: rgba(127, 132, 255, 0.1); color: var(--primary); padding: 5px 12px; border-radius: 20px; font-weight: 600; display: inline-block; margin-bottom: 15px;">
                    ${product.categoryName}
                </span>
                
                <h1 style="margin: 0 0 15px 0; font-size: 2rem; color: var(--text-main); font-weight: 800; line-height: 1.3;">
                    ${product.name}
                </h1>
                
                <div style="font-size: 1.8rem; font-weight: 800; color: #2ecc71; margin-bottom: 25px;">
                    <fmt:formatNumber value="${product.price}" pattern="#,##0"/> đ
                </div>

                <div style="border-top: 1px solid var(--border-color); padding-top: 20px; margin-bottom: 30px;">
                    <h3 style="margin-top: 0; font-size: 1.1rem; color: var(--text-main); font-weight: 700; margin-bottom: 10px;">
                        Mô tả sản phẩm
                    </h3>
                    <p style="margin: 0; font-size: 0.95rem; color: var(--text-muted); line-height: 1.6; white-space: pre-line;">
                        ${product.description}
                    </p>
                </div>
            </div>

            <!-- Action buttons -->
            <div style="border-top: 1px solid var(--border-color); padding-top: 20px;">
                <div style="display: flex; align-items: center; gap: 15px; margin-bottom: 15px;">
                    <label for="quantity" style="font-weight: 600; color: var(--text-main); white-space: nowrap;">Số lượng:</label>
                    <input type="number" id="quantityInput" value="1" min="1" max="99" class="form-control" style="width: 80px; text-align: center;">
                </div>
                <div style="display: flex; gap: 12px;">
                    <form action="${pageContext.request.contextPath}/cart/add" method="post" style="flex: 2;">
                        <input type="hidden" name="productId" value="${product.id}">
                        <input type="hidden" name="quantity" id="quantityHidden" value="1">
                        <button type="submit" class="btn" style="width: 100%; padding: 15px; font-size: 1rem; border-radius: 12px; font-weight: 700;"
                                onclick="document.getElementById('quantityHidden').value = document.getElementById('quantityInput').value">
                            🛒 Thêm vào giỏ hàng
                        </button>
                    </form>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary" style="flex: 1; text-align: center; padding: 15px; font-size: 1rem; border-radius: 12px; font-weight: 600; line-height: 20px;">
                        Quay lại
                    </a>
                </div>
            </div>
        </div>

    </div>
</t:layout>
