package com.example.backend.service.Impl;

import com.example.backend.dto.CartItemResponse;
import com.example.backend.service.CartService;
import com.example.backend.entity.*;
import com.example.backend.repository.OrderRepository;
import com.example.backend.service.OrderService;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UsersRepo;
import com.example.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final UsersRepo usersRepo;

    @Override
    @Transactional
    public Order createOrderFromCart(String userEmail, String shippingAddress) {
        Users user = usersRepo.findByEmail(userEmail).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", userEmail)
        );

        List<CartItemResponse> cartItems = cartService.getCart(userEmail).getItems();

        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .status(OrderStatus.PAID)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItemResponse ci : cartItems) {
            Product p = productRepository.findById(ci.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", ci.getProductId()));

            if (p.getStock() < ci.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + p.getName() + " không đủ hàng.");
            }
            p.setStock(p.getStock() - ci.getQuantity());
            productRepository.save(p);

            BigDecimal subtotal = p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            total = total.add(subtotal);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(p)
                    .quantity(ci.getQuantity())
                    .priceAtPurchase(p.getPrice())
                    .build();

            order.getItems().add(item);
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        cartService.clearCart(userEmail);

        return saved;
    }

    @Override
    @Transactional
    public Order createDirectOrder(String userEmail, UUID productId, int quantity, String shippingAddress) {
        Users user = usersRepo.findByEmail(userEmail).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", userEmail)
        );

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .status(OrderStatus.PAID)
                .build();

        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .priceAtPurchase(product.getPrice())
                .build();

        order.getItems().add(item);
        order.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long id, String userEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        if (order.getUser() == null || !userEmail.equals(order.getUser().getEmail())) {
            throw new ResourceNotFoundException("Order", "id", id);
        }
        return order;
    }

    @Override
    public Page<Order> getOrdersForUser(String userEmail, Pageable pageable) {
        Users user = usersRepo.findByEmail(userEmail).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", userEmail)
        );
        return orderRepository.findAllByUser(user, pageable);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!userEmail.equals(order.getUser().getEmail())) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Order cannot be cancelled at this stage");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}
