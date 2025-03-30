package com.optitop.optitop_api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seller")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "seller_ref", unique = true, nullable = false, length = 50)
    private String sellerRef;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_seller_user"), nullable = true)
    private User user;

    // Constructeurs
    public Seller() {
    }

    public Seller(String sellerRef, User user) {
        this.sellerRef = sellerRef;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSellerRef() {
        return sellerRef;
    }

    public void setSellerRef(String sellerRef) {
        this.sellerRef = sellerRef;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
