package com.example.backend.service;

import com.example.backend.dto.ProductRequest;
import com.example.backend.dto.ProductResponse;
import com.example.backend.dto.MessageResponse;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request, String sellerEmail);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(UUID id);
    ProductResponse updateProduct(UUID id, ProductRequest request, String sellerEmail);
    MessageResponse deleteProduct(UUID id, String sellerEmail);
}
