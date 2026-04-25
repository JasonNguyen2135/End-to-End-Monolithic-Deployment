package com.example.backend.Cart.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {
    private UUID productId;
    private String name;
    private BigDecimal price; // Dùng BigDecimal
    private Integer quantity;
    private String imageUrl;
}
