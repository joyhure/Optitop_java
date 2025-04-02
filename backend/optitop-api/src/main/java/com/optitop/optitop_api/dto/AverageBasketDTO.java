package com.optitop.optitop_api.dto;

public class AverageBasketDTO {
    private String sellerRef;
    private Double totalAmount;
    private Long invoiceCount;
    private Double averageBasket;
    private Double averageP1MON;
    private Double averageP1VER;
    private Double averageP2;
    private Long p2Count;

    public AverageBasketDTO(String sellerRef, Double totalAmount, Long invoiceCount,
            Double totalAmountP1MON, Long countP1MON,
            Double totalAmountP1VER, Long countP1VER,
            Double totalAmountP2, Long countP2) {
        this.sellerRef = sellerRef;
        this.totalAmount = totalAmount;
        this.invoiceCount = invoiceCount;
        this.averageBasket = calculateAverage(totalAmount, invoiceCount);
        this.averageP1MON = calculateAverage(totalAmountP1MON, countP1MON);
        this.averageP1VER = calculateAverage(totalAmountP1VER, countP1VER);
        this.averageP2 = calculateAverage(totalAmountP2, countP2);
        this.p2Count = countP2;
    }

    private Double calculateAverage(Double amount, Long count) {
        return count != null && count > 0 && amount != null ? amount / count : 0.0;
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

    public Double getAverageP1MON() {
        return averageP1MON;
    }

    public Double getAverageP1VER() {
        return averageP1VER;
    }

    public Double getAverageP2() {
        return averageP2;
    }

    public Long getP2Count() {
        return p2Count;
    }
}