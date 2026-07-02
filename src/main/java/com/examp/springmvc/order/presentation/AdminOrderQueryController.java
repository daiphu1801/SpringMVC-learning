package com.examp.springmvc.order.presentation;

import com.examp.springmvc.order.application.query.FindAllOrdersUseCase;
import com.examp.springmvc.order.application.query.FindOrderByIdUseCase;
import com.examp.springmvc.order.application.query.OrderDTO;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderQueryController {

    private final FindAllOrdersUseCase findAllOrdersUseCase;
    private final FindOrderByIdUseCase findOrderByIdUseCase;

    public AdminOrderQueryController(
            FindAllOrdersUseCase findAllOrdersUseCase, FindOrderByIdUseCase findOrderByIdUseCase) {
        this.findAllOrdersUseCase = findAllOrdersUseCase;
        this.findOrderByIdUseCase = findOrderByIdUseCase;
    }

    @GetMapping
    public String listAllOrders(Model model) {
        List<OrderDTO> orders = findAllOrdersUseCase.execute();
        model.addAttribute("orders", orders);
        return "order/admin-order-list";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable("id") Long id, Model model) {
        OrderDTO order = findOrderByIdUseCase.execute(id);
        model.addAttribute("order", order);
        return "order/admin-order-detail";
    }
}
