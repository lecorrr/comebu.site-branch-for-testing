package com.example.shop.service;

import com.example.shop.entity.Order;
import com.example.shop.entity.OrderItem;
import com.example.shop.entity.Product;
import com.example.shop.entity.User;
import com.example.shop.repository.OrderRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order save(Order order) {
        if (order.getUser() != null && order.getUser().getId() != null) {
            User user = userRepository.findById(order.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + order.getUser().getId()));
            order.setUser(user);
        }

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {

                Product product = null;

                if (item.getProduct() != null && item.getProduct().getId() != null) {
                    Long productId = item.getProduct().getId();
                    product = productRepository.findById(productId)
                            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
                } else if (item.getProductId() != null) {
                    Long productId = item.getProductId();
                    product = productRepository.findById(productId)
                            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
                }

                if (product == null) {
                    throw new IllegalArgumentException("Product id is required for order item");
                }

                item.setProduct(product);
                item.setOrder(order);
            }
        }

        if (order.getCreatedAt() == null) {
            order.setCreatedAt(LocalDateTime.now());
        }

        if (order.getStatus() == null) {
            order.setStatus("NEW");
        }

        return orderRepository.save(order);
    }

    public List<Order> getOrdersForUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }
}
