package dto;

public class SellerStatsDTO {
    private String sellerRef;
    private Long totalQuotations;
    private Long unvalidatedQuotations;
    private Double concretizationRate;

    public SellerStatsDTO(String sellerRef, Long totalQuotations, Long unvalidatedQuotations) {
        this.sellerRef = sellerRef;
        this.totalQuotations = totalQuotations;
        this.unvalidatedQuotations = unvalidatedQuotations;
        this.concretizationRate = totalQuotations > 0
                ? ((totalQuotations - unvalidatedQuotations) * 100.0) / totalQuotations
                : 0.0;
    }

    // Getters
    public String getSellerRef() {
        return sellerRef;
    }

    public Long getTotalQuotations() {
        return totalQuotations;
    }

    public Long getUnvalidatedQuotations() {
        return unvalidatedQuotations;
    }

    public Double getConcretizationRate() {
        return concretizationRate;
    }
}