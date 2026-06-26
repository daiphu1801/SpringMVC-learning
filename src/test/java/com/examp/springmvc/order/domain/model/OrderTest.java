package com.examp.springmvc.order.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void shouldPlaceOrderSuccessfully() {
        Long userId = 1L;
        ShippingAddress address = new ShippingAddress("Nguyen A", "0987654321", "123 Street, District 1, HCMC");

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(null, 101L, "iPhone 15", "IPHONE15", new BigDecimal("20000000"), 2));
        items.add(new OrderItem(null, 102L, "Case", "CASE15", new BigDecimal("500000"), 1));

        Order order = Order.place(userId, items, address, "Giao gio hanh chinh", PaymentMethod.CASH);

        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getTotalAmount()).isEqualTo(new BigDecimal("40500000"));
        assertThat(order.getShippingAddress()).isEqualTo(address);
        assertThat(order.getNote()).isEqualTo("Giao gio hanh chinh");
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getDomainEvents()).isNotEmpty();
    }

    @Test
    void shouldThrowExceptionWhenOrderHasNoItems() {
        Long userId = 1L;
        ShippingAddress address = new ShippingAddress("Nguyen A", "0987654321", "123 Street, District 1, HCMC");

        assertThatThrownBy(() -> Order.place(userId, Collections.emptyList(), address, "No items", PaymentMethod.CASH))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Đơn hàng phải có ít nhất một sản phẩm");
    }

    @Test
    void shouldConfirmOrderSuccessfully() {
        Long userId = 1L;
        ShippingAddress address = new ShippingAddress("Nguyen A", "0987654321", "123 Street, District 1, HCMC");
        List<OrderItem> items =
                List.of(new OrderItem(null, 101L, "iPhone 15", "IPHONE15", new BigDecimal("20000000"), 1));

        Order order = Order.place(userId, items, address, "", PaymentMethod.CASH);
        order.confirm();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void shouldThrowExceptionWhenConfirmingNonPendingOrder() {
        Long userId = 1L;
        ShippingAddress address = new ShippingAddress("Nguyen A", "0987654321", "123 Street, District 1, HCMC");
        List<OrderItem> items =
                List.of(new OrderItem(null, 101L, "iPhone 15", "IPHONE15", new BigDecimal("20000000"), 1));

        Order order = Order.place(userId, items, address, "", PaymentMethod.CASH);
        order.confirm(); // Now CONFIRMED

        assertThatThrownBy(order::confirm)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Chỉ có thể xác nhận đơn hàng đang ở trạng thái PENDING");
    }

    @Test
    void shouldCancelOrderSuccessfullyWhenPending() {
        Long userId = 1L;
        ShippingAddress address = new ShippingAddress("Nguyen A", "0987654321", "123 Street, District 1, HCMC");
        List<OrderItem> items =
                List.of(new OrderItem(null, 101L, "iPhone 15", "IPHONE15", new BigDecimal("20000000"), 1));

        Order order = Order.place(userId, items, address, "", PaymentMethod.CASH);
        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldCancelOrderSuccessfullyWhenConfirmed() {
        Long userId = 1L;
        ShippingAddress address = new ShippingAddress("Nguyen A", "0987654321", "123 Street, District 1, HCMC");
        List<OrderItem> items =
                List.of(new OrderItem(null, 101L, "iPhone 15", "IPHONE15", new BigDecimal("20000000"), 1));

        Order order = Order.place(userId, items, address, "", PaymentMethod.CASH);
        order.confirm();
        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldThrowExceptionWhenCancellingShippedOrder() {
        Long userId = 1L;
        ShippingAddress address = new ShippingAddress("Nguyen A", "0987654321", "123 Street, District 1, HCMC");
        List<OrderItem> items =
                List.of(new OrderItem(null, 101L, "iPhone 15", "IPHONE15", new BigDecimal("20000000"), 1));

        Order order = Order.place(userId, items, address, "", PaymentMethod.CASH);
        order.confirm();
        order.markShipping();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Không thể huỷ đơn hàng đang ở trạng thái SHIPPING");
    }
}
