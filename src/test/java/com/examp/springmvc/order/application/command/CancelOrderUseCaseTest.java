package com.examp.springmvc.order.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.model.OrderItem;
import com.examp.springmvc.order.domain.model.OrderStatus;
import com.examp.springmvc.order.domain.model.PaymentMethod;
import com.examp.springmvc.order.domain.model.PaymentStatus;
import com.examp.springmvc.order.domain.model.ShippingAddress;
import com.examp.springmvc.order.domain.ports.output.OrderPersistencePort;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CancelOrderUseCaseTest {

    private OrderPersistencePort orderPersistencePort;
    private CancelOrderUseCase cancelOrderUseCase;

    @BeforeEach
    void setUp() {
        orderPersistencePort = mock(OrderPersistencePort.class);
        cancelOrderUseCase = new CancelOrderUseCase(orderPersistencePort);
    }

    @Test
    void shouldCancelOrderSuccessfully() {
        Long orderId = 1L;
        Long userId = 2L;
        ShippingAddress address = new ShippingAddress("Nguyen A", "0987654321", "123 Street, District 1, HCMC");
        List<OrderItem> items =
                List.of(new OrderItem(null, 101L, "iPhone 15", "IPHONE15", new BigDecimal("20000000"), 1));

        Order order = new Order(
                orderId,
                userId,
                OrderStatus.PENDING,
                items,
                new BigDecimal("20000000"),
                address,
                "Note",
                PaymentMethod.CASH,
                PaymentStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(order));

        cancelOrderUseCase.execute(orderId, userId, false);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderPersistencePort).save(order);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotOwner() {
        Long orderId = 1L;
        Long ownerId = 2L;
        Long otherUserId = 3L;
        ShippingAddress address = new ShippingAddress("Nguyen A", "0987654321", "123 Street, District 1, HCMC");
        List<OrderItem> items =
                List.of(new OrderItem(null, 101L, "iPhone 15", "IPHONE15", new BigDecimal("20000000"), 1));

        Order order = new Order(
                orderId,
                ownerId,
                OrderStatus.PENDING,
                items,
                new BigDecimal("20000000"),
                address,
                "Note",
                PaymentMethod.CASH,
                PaymentStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> cancelOrderUseCase.execute(orderId, otherUserId, false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Bạn không có quyền huỷ đơn hàng này");
    }

    @Test
    void shouldCancelOrderSuccessfullyWhenAdminEvenIfNotOwner() {
        Long orderId = 1L;
        Long ownerId = 2L;
        Long adminUserId = 99L;
        ShippingAddress address = new ShippingAddress("Nguyen A", "0987654321", "123 Street, District 1, HCMC");
        List<OrderItem> items =
                List.of(new OrderItem(null, 101L, "iPhone 15", "IPHONE15", new BigDecimal("20000000"), 1));

        Order order = new Order(
                orderId,
                ownerId,
                OrderStatus.PENDING,
                items,
                new BigDecimal("20000000"),
                address,
                "Note",
                PaymentMethod.CASH,
                PaymentStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(order));

        cancelOrderUseCase.execute(orderId, adminUserId, true);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderPersistencePort).save(order);
    }
}
