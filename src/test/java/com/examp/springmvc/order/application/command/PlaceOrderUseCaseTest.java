package com.examp.springmvc.order.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examp.springmvc.catalog.domain.model.Product;
import com.examp.springmvc.catalog.domain.model.ProductStatus;
import com.examp.springmvc.catalog.domain.ports.output.ProductPersistencePort;
import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.model.OrderStatus;
import com.examp.springmvc.order.domain.model.PaymentMethod;
import com.examp.springmvc.order.domain.ports.output.OrderPersistencePort;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class PlaceOrderUseCaseTest {

    private OrderPersistencePort orderPersistencePort;
    private ProductPersistencePort productPersistencePort;
    private PlaceOrderUseCase placeOrderUseCase;

    @BeforeEach
    void setUp() {
        orderPersistencePort = mock(OrderPersistencePort.class);
        productPersistencePort = mock(ProductPersistencePort.class);
        placeOrderUseCase = new PlaceOrderUseCase(orderPersistencePort, productPersistencePort);
    }

    @Test
    void shouldPlaceOrderSuccessfully() {
        Long userId = 1L;
        Long productId = 101L;
        Product mockProduct = new Product(
                productId,
                2L,
                "IPHONE15",
                "iPhone 15",
                "Mô tả",
                new BigDecimal("20000000"),
                ProductStatus.ACTIVE,
                null);

        when(productPersistencePort.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlaceOrderCommand.OrderItemRequest itemReq = new PlaceOrderCommand.OrderItemRequest(productId, 2);
        PlaceOrderCommand command = new PlaceOrderCommand(
                userId,
                List.of(itemReq),
                "Nguyen A",
                "0987654321",
                "123 Street",
                "Ward 1",
                "District 1",
                "HCMC",
                "Note",
                PaymentMethod.CASH);

        Order result = placeOrderUseCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.getTotalAmount()).isEqualTo(new BigDecimal("40000000"));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getUserId()).isEqualTo(userId);
        assertThat(savedOrder.getItems().get(0).getProductId()).isEqualTo(productId);
        assertThat(savedOrder.getItems().get(0).getProductName()).isEqualTo("iPhone 15");
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        Long userId = 1L;
        Long productId = 101L;

        when(productPersistencePort.findById(productId)).thenReturn(Optional.empty());

        PlaceOrderCommand.OrderItemRequest itemReq = new PlaceOrderCommand.OrderItemRequest(productId, 2);
        PlaceOrderCommand command = new PlaceOrderCommand(
                userId,
                List.of(itemReq),
                "Nguyen A",
                "0987654321",
                "123 Street",
                "Ward 1",
                "District 1",
                "HCMC",
                "Note",
                PaymentMethod.CASH);

        assertThatThrownBy(() -> placeOrderUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sản phẩm không tồn tại với ID: " + productId);
    }
}
