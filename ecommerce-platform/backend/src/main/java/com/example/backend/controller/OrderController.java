package com.example.backend.controller;

import com.example.backend.dto.CreateOrderRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.entity.Order;
import com.example.backend.mapper.OrderMapper;
import com.example.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final String MOCK_EMAIL = "admin@vshop.com";

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        
        String email = (authentication != null) ? authentication.getName() : MOCK_EMAIL;
        Order order = orderService.createOrderFromCart(email, request.getShippingAddress());
        
        // Sử dụng OrderMapper.toDto() static method
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderMapper.toDto(order));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(Authentication authentication) {
        String email = (authentication != null) ? authentication.getName() : MOCK_EMAIL;
        
        // Trả về danh sách đơn hàng cho user hiện tại, chuyển sang DTO để tránh lỗi JSON
        return ResponseEntity.ok(
            orderService.getOrdersForUser(email, org.springframework.data.domain.Pageable.unpaged())
                .getContent()
                .stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList())
        );
    }
}
