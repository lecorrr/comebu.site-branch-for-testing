package com.example.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import com.example.shop.entity.*;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  List<CartItem> findByUserId(Long userId);
  java.util.Optional<CartItem> findByUserAndProduct(User user, Product product);
  void deleteByUserId(Long userId);
}
