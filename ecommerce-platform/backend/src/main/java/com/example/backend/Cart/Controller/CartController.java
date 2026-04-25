package com.example.backend.Cart.Controller;

import com.example.backend.Cart.dto.CartItemRequest;
import com.example.backend.Cart.dto.CartResponse;
import com.example.backend.Cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final String MOCK_USER = "admin@vshop.com";

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCart(MOCK_USER));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartResponse> addToCart(@PathVariable String userId, @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(MOCK_USER, request));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) {
        cartService.clearCart(MOCK_USER);
        return ResponseEntity.ok().build();
    }
}
