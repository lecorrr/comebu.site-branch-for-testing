package com.example.shop.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity @Table(name="orders")
public class Order {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="user_id") private User user;
    private Instant createdAt;
    private double total;
    @OneToMany(mappedBy="order", cascade=CascadeType.ALL, orphanRemoval=true)
    @JsonManagedReference
    private List<OrderItem> items;
    public Order() {}
    public Long getId(){return id;}
    public User getUser(){return user;} public void setUser(User v){user=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public double getTotal(){return total;} public void setTotal(double v){total=v;}
    public List<OrderItem> getItems(){return items;} public void setItems(List<OrderItem> v){items=v;}
}
