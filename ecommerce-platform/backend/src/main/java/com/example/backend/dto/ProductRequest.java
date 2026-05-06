package com.example.backend.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price; // Đổi sang BigDecimal
    private Long categoryId;
    private Integer stock;
    private String imageUrl;
}
