package com.example.backend.Product.service;

import com.example.backend.Product.dto.ProductRequest;
import com.example.backend.Product.dto.ProductResponse;
import com.example.backend.auth.dto.Responses.MessageResponse;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request, String sellerEmail);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(UUID id);
    ProductResponse updateProduct(UUID id, ProductRequest request, String sellerEmail);
    MessageResponse deleteProduct(UUID id, String sellerEmail);
}
