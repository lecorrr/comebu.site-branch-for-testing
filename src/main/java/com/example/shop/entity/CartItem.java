package com.example.shop.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity @Table(name="cart_items")
public class CartItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="user_id")
    @JsonIgnore
    private User user;
    @ManyToOne(optional=false) @JoinColumn(name="product_id") private Product product;
    private int qty;
    public CartItem() {}
    public CartItem(User u, Product p, int q){this.user=u;this.product=p;this.qty=q;}
    public Long getId(){return id;}
    public User getUser(){return user;} public void setUser(User v){user=v;}
    public Product getProduct(){return product;} public void setProduct(Product v){product=v;}
    public int getQty(){return qty;} public void setQty(int v){qty=v;}
}
