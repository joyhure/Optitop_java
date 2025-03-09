package com.optitop.optitop_api.dto;

public class AverageBasketDTO {
    private String sellerRef;
    private Double totalAmount;
    private Long invoiceCount;
    private Double averageBasket;

    public AverageBasketDTO(String sellerRef, Double totalAmount, Long invoiceCount) {
        this.sellerRef = sellerRef;
        this.totalAmount = totalAmount;
        this.invoiceCount = invoiceCount;
        this.averageBasket = calculateAverageBasket();
    }

    private Double calculateAverageBasket() {
        return invoiceCount != null && invoiceCount != 0
                ? totalAmount / invoiceCount
                : null;
    }

    // Getters
    public String getSellerRef() {
        return sellerRef;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public Long getInvoiceCount() {
        return invoiceCount;
    }

    public Double getAverageBasket() {
        return averageBasket;
    }
}