package com.example.backend.dto;



import com.example.backend.dto.ProductResponse;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private String message;
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String parentName;
    private List<CategoryResponse> subCategories; // nested children
    private List<ProductResponse> products; // products in this category

}

