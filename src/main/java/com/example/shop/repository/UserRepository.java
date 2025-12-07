package com.example.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.shop.entity.User;

public interface UserRepository extends JpaRepository<User, Long> { Optional<User> findByEmail(String email); }
