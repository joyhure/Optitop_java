package com.optitop.optitop_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Seller {

    @Id
    @Column(nullable = false, unique = true)
    private String sellerRef;

    // Getters et setters
    public String getSellerRef() {
        return sellerRef;
    }

    public void setSellerRef(String sellerRef) {
        this.sellerRef = sellerRef;
    }
}
