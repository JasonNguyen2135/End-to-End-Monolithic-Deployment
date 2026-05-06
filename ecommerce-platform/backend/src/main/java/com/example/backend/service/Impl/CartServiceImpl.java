package com.example.backend.service.Impl;

import com.example.backend.dto.CartItemRequest;
import com.example.backend.dto.CartItemResponse;
import com.example.backend.dto.CartResponse;
import com.example.backend.entity.Cart;
import com.example.backend.entity.CartItem;
import com.example.backend.repository.CartItemRepository;
import com.example.backend.repository.CartRepository;
import com.example.backend.service.CartService;
import com.example.backend.entity.Product;
import com.example.backend.repository.ProductRepository;
import com.example.backend.entity.Users;
import com.example.backend.repository.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UsersRepo usersRepo;

    @Override
    public CartResponse getCart(String email) {
        Users user = usersRepo.findByEmail(email).orElse(null);
        if (user == null) {
            return CartResponse.builder().items(new ArrayList<>()).totalPrice(BigDecimal.ZERO).build();
        }
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).items(new ArrayList<>()).build()));
        return mapToResponse(cart);
    }

    @Override
    public CartResponse addToCart(String email, CartItemRequest request) {
        Users user = usersRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).items(new ArrayList<>()).build()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst().orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = CartItem.builder().cart(cart).product(product).quantity(request.getQuantity()).price(product.getPrice()).build();
            cart.getItems().add(newItem);
        }
        return mapToResponse(cartRepository.save(cart));
    }

    @Override
    public CartResponse updateQuantity(String email, UUID productId, Integer quantity) {
        Users user = usersRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found"));
        
        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    if (quantity <= 0) cartItemRepository.delete(item);
                });
        
        return mapToResponse(cartRepository.save(cart));
    }

    @Override
    public void removeFromCart(String email, UUID productId) {
        Users user = usersRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().removeIf(item -> {
            if (item.getProduct().getId().equals(productId)) {
                cartItemRepository.delete(item);
                return true;
            }
            return false;
        });
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(String email) {
        Users user = usersRepo.findByEmail(email).orElse(null);
        if (user != null) {
            cartRepository.findByUser(user).ifPresent(cart -> {
                cartItemRepository.deleteAll(cart.getItems());
                cart.getItems().clear();
                cartRepository.save(cart);
            });
        }
    }

    private CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> CartItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .name(item.getProduct().getName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .imageUrl(item.getProduct().getImageUrl())
                        .build())
                .collect(Collectors.toList());

        BigDecimal total = itemResponses.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder().items(itemResponses).totalPrice(total).build();
    }
}
