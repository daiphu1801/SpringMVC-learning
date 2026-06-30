package com.examp.springmvc.order.application.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.model.OrderItem;
import com.examp.springmvc.order.domain.model.OrderStatus;
import com.examp.springmvc.order.domain.model.PaymentMethod;
import com.examp.springmvc.order.domain.model.PaymentStatus;
import com.examp.springmvc.order.domain.model.ShippingAddress;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderDTOTest {

    @Test
    void shouldMapFromDomainCorrectly() {
        Long orderId = 1L;
        Long userId = 2L;
        ShippingAddress address = new ShippingAddress("Nguyen A", "0987654321", "123 Street, District 1, HCMC");
        List<OrderItem> items =
                List.of(new OrderItem(10L, 101L, "iPhone 15", "IPHONE15", new BigDecimal("20000000"), 1));

        LocalDateTime now = LocalDateTime.now();
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
                now,
                now);

        OrderDTO dto = OrderDTO.fromDomain(order);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(orderId);
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getStatus()).isEqualTo("PENDING");
        assertThat(dto.getTotalAmount()).isEqualTo(new BigDecimal("20000000"));
        assertThat(dto.getReceiverName()).isEqualTo("Nguyen A");
        assertThat(dto.getReceiverPhone()).isEqualTo("0987654321");
        assertThat(dto.getShippingAddress()).isEqualTo("123 Street, District 1, HCMC");
        assertThat(dto.getNote()).isEqualTo("Note");
        assertThat(dto.getPaymentMethod()).isEqualTo("CASH");
        assertThat(dto.getPaymentStatus()).isEqualTo("PENDING");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);

        assertThat(dto.getItems()).hasSize(1);
        OrderItemDTO itemDto = dto.getItems().get(0);
        assertThat(itemDto.getId()).isEqualTo(10L);
        assertThat(itemDto.getProductId()).isEqualTo(101L);
        assertThat(itemDto.getProductName()).isEqualTo("iPhone 15");
        assertThat(itemDto.getProductSku()).isEqualTo("IPHONE15");
        assertThat(itemDto.getUnitPrice()).isEqualTo(new BigDecimal("20000000"));
        assertThat(itemDto.getQuantity()).isEqualTo(1);
        assertThat(itemDto.getSubtotal()).isEqualTo(new BigDecimal("20000000"));
    }
}
