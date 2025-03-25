package com.optitop.optitop_api.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.ForeignKey;

@Entity
@Table(name = "invoices")

public class Invoices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "client", nullable = false)
    private String client;

    @Column(name = "invoice_ref", nullable = false, unique = true)
    private String invoiceRef;

    @ManyToOne
    @JoinColumn(name = "seller_ref", referencedColumnName = "seller_ref", foreignKey = @ForeignKey(name = "fk_invoices_seller_ref"))
    private Seller seller;

    @Column(name = "total_invoice", nullable = false)
    private Double totalInvoice;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "is_optical")
    private Boolean isOptical;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getters
    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClient() {
        return client;
    }

    public String getInvoiceRef() {
        return invoiceRef;
    }

    public Seller getSeller() {
        return seller;
    }

    public Double getTotalInvoice() {
        return totalInvoice;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getIsOptical() {
        return isOptical;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setInvoiceRef(String invoiceRef) {
        this.invoiceRef = invoiceRef;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public void setTotalInvoice(Double totalInvoice) {
        this.totalInvoice = totalInvoice;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setIsOptical(Boolean isOptical) {
        this.isOptical = isOptical;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
