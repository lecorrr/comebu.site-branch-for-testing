package com.example.shop.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity @Table(name="order_items")
public class OrderItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="order_id")
    @JsonBackReference
    private Order order;
    @ManyToOne(optional=false) @JoinColumn(name="product_id") private Product product;
    private int qty; private double unitPrice;
    public OrderItem() {}
    public Long getId(){return id;}
    public Order getOrder(){return order;} public void setOrder(Order v){order=v;}
    public Product getProduct(){return product;} public void setProduct(Product v){product=v;}
    public int getQty(){return qty;} public void setQty(int v){qty=v;}
    public double getUnitPrice(){return unitPrice;} public void setUnitPrice(double v){unitPrice=v;}
}
