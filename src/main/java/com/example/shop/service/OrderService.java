package com.example.shop.service;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import com.example.shop.entity.*;
import com.example.shop.repository.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    public OrderService(OrderRepository orderRepository, CartItemRepository cartItemRepository, ProductRepository productRepository){
        this.orderRepository = orderRepository; this.cartItemRepository = cartItemRepository; this.productRepository = productRepository;
    }
    public Order createOrderFromCart(User user){
        List<CartItem> cart = cartItemRepository.findByUserId(user.getId());
        if (cart.isEmpty()) throw new RuntimeException("Cart is empty");
        Order order = new Order(); order.setUser(user); order.setCreatedAt(Instant.now());
        List<OrderItem> items = new ArrayList<>(); double total = 0.0;
        for (CartItem ci : cart) {
            Product p = ci.getProduct();
            if (p.getStock() < ci.getQty()) throw new RuntimeException("Not enough stock for " + p.getTitle());
            p.setStock(p.getStock() - ci.getQty()); productRepository.save(p);
            OrderItem oi = new OrderItem(); oi.setOrder(order); oi.setProduct(p); oi.setQty(ci.getQty()); oi.setUnitPrice(p.getPrice());
            items.add(oi); total += p.getPrice() * ci.getQty();
        }
        order.setItems(items); order.setTotal(total);
        Order saved = orderRepository.save(order);
        cartItemRepository.deleteByUserId(user.getId());
        return saved;
    }
}
