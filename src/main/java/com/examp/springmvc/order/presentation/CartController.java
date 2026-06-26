package com.examp.springmvc.order.presentation;

import com.examp.springmvc.catalog.application.product.query.FindProductByIdUseCase;
import com.examp.springmvc.catalog.application.product.query.ProductDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * Controller quản lý Giỏ hàng (Cart) lưu trong Session.
 * Cart là một tính năng thuần tuý của Presentation Layer, không cần tầng Domain.
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    private final FindProductByIdUseCase findProductByIdUseCase;

    public CartController(FindProductByIdUseCase findProductByIdUseCase) {
        this.findProductByIdUseCase = findProductByIdUseCase;
    }

    @GetMapping
    public String viewCart(@SessionAttribute(name = "cart", required = false) Map<Long, Integer> cart, Model model) {
        if (cart == null || cart.isEmpty()) {
            model.addAttribute("cartItems", new ArrayList<>());
            model.addAttribute("cartTotal", 0);
            return "order/cart";
        }

        List<CartItemView> items = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            try {
                ProductDTO product = findProductByIdUseCase.execute(entry.getKey());
                CartItemView item = new CartItemView(
                        product.getId(),
                        product.getName(),
                        product.getSku(),
                        product.getPrice(),
                        entry.getValue(),
                        product.getImageUrl());
                items.add(item);
            } catch (IllegalArgumentException ignored) {
                // Bỏ qua sản phẩm không còn tồn tại
            }
        }

        java.math.BigDecimal total = items.stream()
                .map(CartItemView::getSubtotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        model.addAttribute("cartItems", items);
        model.addAttribute("cartTotal", total);
        return "order/cart";
    }

    @PostMapping("/add")
    public String addToCart(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
            jakarta.servlet.http.HttpSession session) {
        Map<Long, Integer> cart = getOrCreateCart(session);
        cart.merge(productId, quantity, Integer::sum);
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("productId") Long productId, jakarta.servlet.http.HttpSession session) {
        Map<Long, Integer> cart = getOrCreateCart(session);
        cart.remove(productId);
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") int quantity,
            jakarta.servlet.http.HttpSession session) {
        if (quantity <= 0) {
            return removeFromCart(productId, session);
        }
        Map<Long, Integer> cart = getOrCreateCart(session);
        cart.put(productId, quantity);
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(jakarta.servlet.http.HttpSession session) {
        session.removeAttribute("cart");
        return "redirect:/products";
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getOrCreateCart(jakarta.servlet.http.HttpSession session) {
        Object cartObj = session.getAttribute("cart");
        if (cartObj instanceof Map) {
            return (Map<Long, Integer>) cartObj;
        }
        return new HashMap<>();
    }

    /**
     * View model cho một dòng trong giỏ hàng.
     */
    public static class CartItemView {
        private final Long productId;
        private final String productName;
        private final String productSku;
        private final java.math.BigDecimal unitPrice;
        private final int quantity;
        private final String imageUrl;

        public CartItemView(
                Long productId,
                String productName,
                String productSku,
                java.math.BigDecimal unitPrice,
                int quantity,
                String imageUrl) {
            this.productId = productId;
            this.productName = productName;
            this.productSku = productSku;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.imageUrl = imageUrl;
        }

        public java.math.BigDecimal getSubtotal() {
            return unitPrice.multiply(java.math.BigDecimal.valueOf(quantity));
        }

        public Long getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public String getProductSku() {
            return productSku;
        }

        public java.math.BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}
