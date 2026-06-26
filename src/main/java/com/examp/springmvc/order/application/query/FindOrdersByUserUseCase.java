package com.examp.springmvc.order.application.query;

import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.model.OrderItem;
import com.examp.springmvc.order.domain.ports.output.OrderPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindOrdersByUserUseCase {

    private final OrderPersistencePort orderPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public FindOrdersByUserUseCase(OrderPersistencePort orderPersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> execute(Long userId) {
        return orderPersistencePort.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private OrderDTO toDto(Order order) {
        List<OrderItemDTO> itemDtos =
                order.getItems().stream().map(this::toItemDto).collect(Collectors.toList());
        return new OrderDTO(
                order.getId(),
                order.getUserId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getShippingAddress().getReceiverName(),
                order.getShippingAddress().getReceiverPhone(),
                order.getShippingAddress().getFullAddress(),
                order.getNote(),
                order.getPaymentMethod().name(),
                order.getPaymentStatus().name(),
                itemDtos,
                order.getCreatedAt(),
                order.getUpdatedAt());
    }

    private OrderItemDTO toItemDto(OrderItem item) {
        return new OrderItemDTO(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getProductSku(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal());
    }
}
