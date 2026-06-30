package com.examp.springmvc.order.application.query;

import com.examp.springmvc.order.domain.ports.output.OrderPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindAllOrdersUseCase {

    private final OrderPersistencePort orderPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public FindAllOrdersUseCase(OrderPersistencePort orderPersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> execute() {
        return orderPersistencePort.findAll().stream().map(OrderDTO::fromDomain).collect(Collectors.toList());
    }
}
