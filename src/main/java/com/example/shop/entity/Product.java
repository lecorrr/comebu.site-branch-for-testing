package com.example.shop.entity;

import jakarta.persistence.*;

@Entity
@Table(name="products")
public class Product {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(length=2048) private String description;
    private double price;
    private int stock;
    private String imageUrl;
    public Product() {}
    public Product(String title, String description, double price, int stock, String imageUrl) {
        this.title=title; this.description=description; this.price=price; this.stock=stock; this.imageUrl=imageUrl;
    }
    public Long getId(){return id;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public double getPrice(){return price;} public void setPrice(double v){price=v;}
    public int getStock(){return stock;} public void setStock(int v){stock=v;}
    public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;}
}
