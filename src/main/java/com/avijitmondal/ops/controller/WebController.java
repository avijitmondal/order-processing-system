package com.avijitmondal.ops.controller;

import com.avijitmondal.ops.dto.OrderResponse;
import com.avijitmondal.ops.dto.UserResponse;
import com.avijitmondal.ops.model.OrderStatus;
import com.avijitmondal.ops.model.User;
import com.avijitmondal.ops.repository.UserRepository;
import com.avijitmondal.ops.service.OrderService;
import com.avijitmondal.ops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/users/me/orders")
    public String myOrders(@RequestParam(required = false) String status, Model model) {
        User currentUser = getCurrentUser();
        UserResponse user = userService.getUserById(currentUser.getId());

        // Fetch orders for the current user
        OrderStatus orderStatus = (status != null && !status.isEmpty()) 
                                  ? OrderStatus.valueOf(status) 
                                  : null;
        List<OrderResponse> orders = orderService.getAllOrders(currentUser.getId(), orderStatus);

        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", status);
        return "user-orders";
    }

    @GetMapping("/users/me/orders/{orderId}")
    public String myOrderDetails(@PathVariable UUID orderId, Model model) {
        User currentUser = getCurrentUser();
        UserResponse user = userService.getUserById(currentUser.getId());

        // Fetch order details
        OrderResponse order = orderService.getOrderById(currentUser.getId(), orderId);

        model.addAttribute("user", user);
        model.addAttribute("order", order);
        return "order-details";
    }
}
