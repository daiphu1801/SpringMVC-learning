package com.examp.springmvc.order.application.command;

import com.examp.springmvc.catalog.domain.model.Product;
import com.examp.springmvc.catalog.domain.model.ProductStatus;
import com.examp.springmvc.catalog.domain.ports.output.ProductPersistencePort;
import com.examp.springmvc.order.domain.model.Order;
import com.examp.springmvc.order.domain.model.OrderItem;
import com.examp.springmvc.order.domain.model.ShippingAddress;
import com.examp.springmvc.order.domain.ports.output.OrderPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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
        if (command.getItems() == null || command.getItems().isEmpty()) {
            throw new IllegalArgumentException("Đơn hàng phải có ít nhất một sản phẩm");
        }

        List<Long> productIds = command.getItems().stream()
                .map(PlaceOrderCommand.OrderItemRequest::getProductId)
                .distinct()
                .collect(Collectors.toList());

        List<Product> products = productPersistencePort.findByIds(productIds);
        Map<Long, Product> productMap =
                products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        List<OrderItem> orderItems = new ArrayList<>();
        String itemsCsv = command.getItems().stream()
                .map(item -> item.getProductId() + ":" + item.getQuantity())
                .collect(Collectors.joining(","));

        for (PlaceOrderCommand.OrderItemRequest req : command.getItems()) {
            Product product = productMap.get(req.getProductId());
            if (product == null) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + req.getProductId());
            }

            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw new IllegalArgumentException(
                        "Sản phẩm không hoạt động hoặc không khả dụng: " + product.getName());
            }

            // Deduct stock in memory for validation
            product.decreaseStock(req.getQuantity());

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

        return orderPersistencePort.saveViaProcedure(order, itemsCsv);
    }
}
