package com.example.shop.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity @Table(name="users")
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true, nullable=false) private String email;
    @JsonIgnore
    @Column(nullable=false) private String passwordHash;
    private String name;
    public User() {}
    public Long getId(){return id;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
}
