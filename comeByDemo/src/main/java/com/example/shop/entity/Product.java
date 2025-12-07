package com.example.shop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(length = 255)
    private String weight;

    @Column(nullable = false)
    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    @JsonIgnore
    private Image imageEntity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Image getImageEntity() {
        return imageEntity;
    }

    public void setImageEntity(Image imageEntity) {
        this.imageEntity = imageEntity;
    }

    @Transient
    public String getImage() {
        if (imageEntity == null) {
            return null;
        }
        if (imageEntity.getPath() != null && !imageEntity.getPath().isBlank()) {
            return imageEntity.getPath();
        }
        String name = imageEntity.getName();
        if (name == null || name.isBlank()) {
            return null;
        }
        String ext = imageEntity.getExtension();
        if (ext == null || ext.isBlank()) {
            return "/images/goods/" + name;
        }
        return "/images/goods/" + name + "." + ext;
    }

    @Transient
    public String getImageName() {
        if (imageEntity == null) {
            return null;
        }
        return imageEntity.getName();
    }
}
