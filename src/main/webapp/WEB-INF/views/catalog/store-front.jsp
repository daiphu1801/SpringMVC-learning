<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Cửa hàng Trực tuyến">
    <div style="width: 100%; max-width: 1100px; margin: 30px auto; display: grid; grid-template-columns: 260px 1fr; gap: 30px; padding: 0 15px;">
        
        <!-- Sidebar Categories -->
        <div class="container" style="padding: 25px; align-self: start; border-radius: 16px;">
            <h3 style="margin-top: 0; margin-bottom: 20px; font-size: 1.15rem; color: var(--primary); font-weight: 700; border-bottom: 1px solid var(--border-color); padding-bottom: 10px;">
                Danh mục
            </h3>
            <ul style="list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: 10px;">
                <li>
                    <c:choose>
                        <c:when test="${empty selectedCategoryId}">
                            <a href="${pageContext.request.contextPath}/products" style="display: block; padding: 10px 15px; border-radius: 10px; text-decoration: none; font-weight: 600; font-size: 0.95rem; transition: all 0.2s; background: var(--primary); color: white;">
                                Tất cả sản phẩm
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/products" style="display: block; padding: 10px 15px; border-radius: 10px; text-decoration: none; font-weight: 600; font-size: 0.95rem; transition: all 0.2s; color: var(--text-main); background: transparent;" 
                               onmouseover="this.style.background='rgba(159, 161, 255, 0.05)'; this.style.color='var(--primary)';" 
                               onmouseout="this.style.background='transparent'; this.style.color='var(--text-main)';">
                                Tất cả sản phẩm
                            </a>
                        </c:otherwise>
                    </c:choose>
                </li>
                <c:forEach var="cat" items="${categories}">
                    <li>
                        <c:choose>
                            <c:when test="${selectedCategoryId == cat.id}">
                                <a href="${pageContext.request.contextPath}/products?category=${cat.id}" style="display: block; padding: 10px 15px; border-radius: 10px; text-decoration: none; font-weight: 600; font-size: 0.95rem; transition: all 0.2s; background: var(--primary); color: white;">
                                    ${cat.name}
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/products?category=${cat.id}" style="display: block; padding: 10px 15px; border-radius: 10px; text-decoration: none; font-weight: 600; font-size: 0.95rem; transition: all 0.2s; color: var(--text-main); background: transparent;"
                                   onmouseover="this.style.background='rgba(159, 161, 255, 0.05)'; this.style.color='var(--primary)';" 
                                   onmouseout="this.style.background='transparent'; this.style.color='var(--text-main)';">
                                    ${cat.name}
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </li>
                </c:forEach>
            </ul>
        </div>

        <!-- Product Grid -->
        <div>
            <div style="margin-bottom: 25px; display: flex; justify-content: space-between; align-items: center;">
                <h2 style="margin: 0; font-size: 1.6rem; color: var(--text-main); font-weight: 700;">Sản phẩm Nổi bật</h2>
                <span style="font-size: 0.9rem; color: var(--text-muted);">Tìm thấy <strong>${products.size()}</strong> sản phẩm</span>
            </div>

            <c:choose>
                <c:when test="${empty products}">
                    <div class="container" style="text-align: center; padding: 60px; border-radius: 16px;">
                        <h3 style="color: var(--text-muted); margin-bottom: 10px;">Không tìm thấy sản phẩm nào</h3>
                        <p style="color: var(--text-muted); margin: 0;">Hiện chưa có sản phẩm nào thuộc danh mục này.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 25px;">
                        <c:forEach var="prod" items="${products}">
                            <div class="container" style="padding: 0; display: flex; flex-direction: column; height: 100%; border-radius: 16px; overflow: hidden; transition: transform 0.2s, box-shadow 0.2s; border: 1px solid var(--border-color); box-shadow: 0 4px 15px rgba(0,0,0,0.02);" 
                                 onmouseover="this.style.transform='translateY(-5px)'; this.style.boxShadow='0 12px 25px rgba(159, 161, 255, 0.15)';" 
                                 onmouseout="this.style.transform='none'; this.style.boxShadow='0 4px 15px rgba(0,0,0,0.02)';">
                                
                                <!-- Product Image -->
                                <div style="height: 180px; display: flex; align-items: center; justify-content: center; position: relative; background: var(--bg-main);">
                                    <c:choose>
                                        <c:when test="${not empty prod.imageUrl}">
                                            <img src="<c:out value='${prod.imageUrl}'/>" alt="${prod.name}" style="width: 100%; height: 100%; object-fit: cover;" />
                                        </c:when>
                                        <c:otherwise>
                                            <div style="width: 100%; height: 100%; background: linear-gradient(135deg, #7F84FF, #9FA1FF); display: flex; align-items: center; justify-content: center;">
                                                <span style="color: white; font-weight: 800; font-size: 2.2rem; opacity: 0.25;">${prod.sku}</span>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <span style="position: absolute; bottom: 12px; left: 12px; background: rgba(0,0,0,0.6); color: white; padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 600;">
                                        ${prod.categoryName}
                                    </span>
                                </div>

                                <!-- Product Info -->
                                <div style="padding: 24px; display: flex; flex-direction: column; flex-grow: 1; justify-content: space-between; gap: 20px;">
                                    <div>
                                        <h4 style="margin: 0 0 8px 0; font-size: 1.05rem; font-weight: 700; color: var(--text-main); line-height: 1.4;">
                                            ${prod.name}
                                        </h4>
                                        <p style="margin: 0; font-size: 0.85rem; color: var(--text-muted); display: -webkit-box; -webkit-line-clamp: 2; line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; line-height: 1.5;">
                                            ${prod.description}
                                        </p>
                                    </div>
                                    
                                    <div style="display: flex; flex-direction: column; gap: 12px; margin-top: auto;">
                                        <span style="font-size: 1.25rem; font-weight: 800; color: #2ecc71; white-space: nowrap;">
                                            <fmt:formatNumber value="${prod.price}" pattern="#,##0"/> đ
                                        </span>
                                        <form action="${pageContext.request.contextPath}/cart/add" method="post">
                                            <input type="hidden" name="productId" value="${prod.id}">
                                            <input type="hidden" name="quantity" value="1">
                                            <button type="submit" class="btn" style="padding: 10px 16px; font-size: 0.85rem; border-radius: 10px; box-shadow: none; width: 100%;">
                                                🛒 Thêm vào giỏ
                                            </button>
                                        </form>
                                        <a href="${pageContext.request.contextPath}/products/${prod.id}" style="text-align: center; font-size: 0.82rem; color: var(--text-muted); text-decoration: none; font-weight: 500;" onmouseover="this.style.color='var(--primary)'" onmouseout="this.style.color='var(--text-muted)'">
                                            Xem chi tiết →
                                        </a>
                                    </div>
                                </div>

                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

    </div>
</t:layout>
