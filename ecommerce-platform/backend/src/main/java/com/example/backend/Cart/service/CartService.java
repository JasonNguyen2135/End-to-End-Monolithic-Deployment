package com.example.backend.Cart.service;

import com.example.backend.Cart.dto.CartItemRequest;
import com.example.backend.Cart.dto.CartResponse;
import java.util.UUID;

public interface CartService {
    CartResponse getCart(String email);
    CartResponse addToCart(String email, CartItemRequest request);
    CartResponse updateQuantity(String email, UUID productId, Integer quantity);
    void removeFromCart(String email, UUID productId);
    void clearCart(String email);
}
