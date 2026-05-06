package com.example.backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private List<CartItemResponse> items;
    private BigDecimal totalPrice; // Dùng BigDecimal
}
