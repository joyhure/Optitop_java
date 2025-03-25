package com.optitop.optitop_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.ForeignKey;

@Entity
@Table(name = "quotations_lines")
public class QuotationsLines {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String client;

    @Column(nullable = false)
    private String quotationRef;

    @Column(nullable = true)
    private String family;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double totalTtc;

    @ManyToOne
    @JoinColumn(name = "seller_ref", referencedColumnName = "seller_ref", nullable = false, foreignKey = @ForeignKey(name = "fk_quotations_lines_seller_ref"))
    private Seller seller;

    @Column(nullable = false)
    private Double totalQuotation;

    @Column(nullable = true)
    private Integer pair;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getQuotationRef() {
        return quotationRef;
    }

    public void setQuotationRef(String quotationRef) {
        this.quotationRef = quotationRef;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotalTtc() {
        return totalTtc;
    }

    public void setTotalTtc(Double totalTtc) {
        this.totalTtc = totalTtc;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Double getTotalQuotation() {
        return totalQuotation;
    }

    public void setTotalQuotation(Double totalQuotation) {
        this.totalQuotation = totalQuotation;
    }

    public Integer getPair() {
        return pair;
    }

    public void setPair(Integer pair) {
        this.pair = pair;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}