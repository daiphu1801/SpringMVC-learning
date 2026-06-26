package com.examp.springmvc.order.infrastructure.persistence;

import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.model.OrderItem;
import com.examp.springmvc.order.domain.model.OrderStatus;
import com.examp.springmvc.order.domain.model.ShippingAddress;
import com.examp.springmvc.order.domain.ports.output.OrderPersistencePort;
import com.examp.springmvc.order.infrastructure.mapper.OrderItemMapper;
import com.examp.springmvc.order.infrastructure.mapper.OrderMapper;
import com.examp.springmvc.shared.domain.DomainEvent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

@Repository
public class OrderPersistenceAdapter implements OrderPersistencePort {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ApplicationEventPublisher eventPublisher;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrderPersistenceAdapter(
            OrderMapper orderMapper, OrderItemMapper orderItemMapper, ApplicationEventPublisher eventPublisher) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Order save(Order order) {
        List<DomainEvent> events = new ArrayList<>(order.getDomainEvents());

        OrderDbEntity dbEntity = toDbEntity(order);
        if (dbEntity.getId() == null) {
            orderMapper.insert(dbEntity);
            order.setId(dbEntity.getId());

            // Cascade Save: lưu toàn bộ OrderItems
            for (OrderItem item : order.getItems()) {
                OrderItemDbEntity itemEntity = toItemDbEntity(item, order.getId());
                orderItemMapper.insert(itemEntity);
            }
        } else {
            orderMapper.update(dbEntity);
        }

        // Publish domain events sau khi lưu thành công
        order.clearDomainEvents();
        for (DomainEvent event : events) {
            eventPublisher.publishEvent(event);
        }

        return order;
    }

    @Override
    public Optional<Order> findById(Long id) {
        OrderDbEntity entity = orderMapper.findById(id);
        if (entity == null) {
            return Optional.empty();
        }
        List<OrderItemDbEntity> itemEntities = orderItemMapper.findByOrderId(id);
        return Optional.of(toDomain(entity, itemEntities));
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderMapper.findByUserId(userId).stream()
                .map(e -> {
                    List<OrderItemDbEntity> items = orderItemMapper.findByOrderId(e.getId());
                    return toDomain(e, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findAll() {
        return orderMapper.findAll().stream()
                .map(e -> {
                    List<OrderItemDbEntity> items = orderItemMapper.findByOrderId(e.getId());
                    return toDomain(e, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        orderItemMapper.deleteByOrderId(id);
        orderMapper.deleteById(id);
    }

    private OrderDbEntity toDbEntity(Order domain) {
        OrderDbEntity entity = new OrderDbEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setStatus(domain.getStatus().name());
        entity.setTotalAmount(domain.getTotalAmount());
        entity.setReceiverName(domain.getShippingAddress().getReceiverName());
        entity.setReceiverPhone(domain.getShippingAddress().getReceiverPhone());
        entity.setShippingAddress(domain.getShippingAddress().getFullAddress());
        entity.setNote(domain.getNote());
        entity.setPaymentMethod(domain.getPaymentMethod().name());
        entity.setPaymentStatus(domain.getPaymentStatus().name());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private OrderItemDbEntity toItemDbEntity(OrderItem item, Long orderId) {
        OrderItemDbEntity entity = new OrderItemDbEntity();
        entity.setOrderId(orderId);
        entity.setProductId(item.getProductId());
        entity.setProductName(item.getProductName());
        entity.setProductSku(item.getProductSku());
        entity.setUnitPrice(item.getUnitPrice());
        entity.setQuantity(item.getQuantity());
        entity.setSubtotal(item.getSubtotal());
        return entity;
    }

    private Order toDomain(OrderDbEntity entity, List<OrderItemDbEntity> itemEntities) {
        List<OrderItem> items = itemEntities.stream()
                .map(i -> new OrderItem(
                        i.getId(),
                        i.getProductId(),
                        i.getProductName(),
                        i.getProductSku(),
                        i.getUnitPrice(),
                        i.getQuantity()))
                .collect(Collectors.toList());

        ShippingAddress address =
                new ShippingAddress(entity.getReceiverName(), entity.getReceiverPhone(), entity.getShippingAddress());

        return new Order(
                entity.getId(),
                entity.getUserId(),
                OrderStatus.valueOf(entity.getStatus()),
                items,
                entity.getTotalAmount(),
                address,
                entity.getNote(),
                entity.getPaymentMethod() != null
                        ? com.examp.springmvc.order.domain.model.PaymentMethod.valueOf(entity.getPaymentMethod())
                        : com.examp.springmvc.order.domain.model.PaymentMethod.CASH,
                entity.getPaymentStatus() != null
                        ? com.examp.springmvc.order.domain.model.PaymentStatus.valueOf(entity.getPaymentStatus())
                        : com.examp.springmvc.order.domain.model.PaymentStatus.PENDING,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
