package com.optitop.optitop_api.dto;

public class AverageBasketDTO {
    private String sellerRef;
    private Double averageBasket;
    private Long invoiceCount;
    private Double averageFramesP1;
    private Double averageLensesP1;
    private Double averageP2;

    public AverageBasketDTO(String sellerRef, Double averageBasket, Long invoiceCount,
            Double averageFramesP1, Double averageLensesP1, Double averageP2) {
        this.sellerRef = sellerRef;
        this.averageBasket = averageBasket;
        this.invoiceCount = invoiceCount;
        this.averageFramesP1 = averageFramesP1;
        this.averageLensesP1 = averageLensesP1;
        this.averageP2 = averageP2;
    }

    // Getters
    public String getSellerRef() {
        return sellerRef;
    }

    public Double getAverageBasket() {
        return averageBasket;
    }

    public Long getInvoiceCount() {
        return invoiceCount;
    }

    public Double getAverageFramesP1() {
        return averageFramesP1;
    }

    public Double getAverageLensesP1() {
        return averageLensesP1;
    }

    public Double getAverageP2() {
        return averageP2;
    }
}