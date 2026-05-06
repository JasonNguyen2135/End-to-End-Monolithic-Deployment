package com.example.backend.mapper;

import com.example.backend.dto.*;
import com.example.backend.entity.*;
import java.util.stream.Collectors;

public final class OrderMapper {
    private OrderMapper() {}

    public static OrderResponse toDto(Order o) {
        OrderResponse dto = new OrderResponse();
        dto.setId(o.getId());
        dto.setTotalAmount(o.getTotalAmount());
        dto.setStatus(o.getStatus());
        dto.setShippingAddress(o.getShippingAddress());
        dto.setCreatedAt(o.getCreatedAt());
        dto.setUpdatedAt(o.getUpdatedAt());
        dto.setItems(o.getItems().stream().map(OrderMapper::toItemDto).collect(Collectors.toList()));
        return dto;
    }

    public static OrderItemResponse toItemDto(OrderItem i) {
        OrderItemResponse d = new OrderItemResponse();
        d.setId(i.getId());
        d.setProductId(i.getProduct().getId());
        d.setProductName(i.getProduct().getName());
        d.setQuantity(i.getQuantity());
        d.setPriceAtPurchase(i.getPriceAtPurchase());
        return d;
    }
}
