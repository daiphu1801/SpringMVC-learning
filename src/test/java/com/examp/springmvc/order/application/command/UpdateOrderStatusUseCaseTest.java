package com.examp.springmvc.order.application.command;

import static org.assertj.core.api.Assertions.assertThat;
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

class UpdateOrderStatusUseCaseTest {

    private OrderPersistencePort orderPersistencePort;
    private UpdateOrderStatusUseCase updateOrderStatusUseCase;

    @BeforeEach
    void setUp() {
        orderPersistencePort = mock(OrderPersistencePort.class);
        updateOrderStatusUseCase = new UpdateOrderStatusUseCase(orderPersistencePort);
    }

    private Order newOrder(Long id, OrderStatus status) {
        ShippingAddress address = new ShippingAddress("Nguyen A", "0987654321", "123 Street, District 1, HCMC");
        List<OrderItem> items =
                List.of(new OrderItem(null, 101L, "iPhone 15", "IPHONE15", new BigDecimal("20000000"), 1));
        return new Order(
                id,
                2L,
                status,
                items,
                new BigDecimal("20000000"),
                address,
                "Note",
                PaymentMethod.CASH,
                PaymentStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Test
    void shouldConfirmOrderSuccessfully() {
        Long orderId = 1L;
        Order order = newOrder(orderId, OrderStatus.PENDING);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(order));

        updateOrderStatusUseCase.execute(new UpdateOrderStatusCommand(orderId, OrderStatusAction.CONFIRM));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(orderPersistencePort).save(order);
    }

    @Test
    void shouldShipOrderSuccessfully() {
        Long orderId = 1L;
        Order order = newOrder(orderId, OrderStatus.CONFIRMED);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(order));

        updateOrderStatusUseCase.execute(new UpdateOrderStatusCommand(orderId, OrderStatusAction.SHIP));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPING);
        verify(orderPersistencePort).save(order);
    }

    @Test
    void shouldDeliverOrderSuccessfully() {
        Long orderId = 1L;
        Order order = newOrder(orderId, OrderStatus.SHIPPING);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(order));

        updateOrderStatusUseCase.execute(new UpdateOrderStatusCommand(orderId, OrderStatusAction.DELIVER));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        verify(orderPersistencePort).save(order);
    }
}
