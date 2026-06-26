package com.examp.springmvc.order.application.command;

import com.examp.springmvc.catalog.domain.model.Product;
import com.examp.springmvc.catalog.domain.ports.output.ProductPersistencePort;
import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.model.OrderItem;
import com.examp.springmvc.order.domain.model.ShippingAddress;
import com.examp.springmvc.order.domain.ports.output.OrderPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaceOrderUseCase {

    private final OrderPersistencePort orderPersistencePort;
    private final ProductPersistencePort productPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public PlaceOrderUseCase(OrderPersistencePort orderPersistencePort, ProductPersistencePort productPersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
        this.productPersistencePort = productPersistencePort;
    }

    @Transactional
    public Order execute(PlaceOrderCommand command) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (PlaceOrderCommand.OrderItemRequest req : command.getItems()) {
            Product product = productPersistencePort
                    .findById(req.getProductId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + req.getProductId()));

            OrderItem item = new OrderItem(
                    null, product.getId(), product.getName(), product.getSku(), product.getPrice(), req.getQuantity());
            orderItems.add(item);
        }

        ShippingAddress shippingAddress = ShippingAddress.of(
                command.getReceiverName(),
                command.getReceiverPhone(),
                command.getStreetDetail(),
                command.getWard(),
                command.getDistrict(),
                command.getProvince());

        Order order = Order.place(
                command.getUserId(), orderItems, shippingAddress, command.getNote(), command.getPaymentMethod());

        return orderPersistencePort.save(order);
    }
}
