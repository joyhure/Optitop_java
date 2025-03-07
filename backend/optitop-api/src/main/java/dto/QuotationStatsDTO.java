package dto;

import java.util.List;

public class QuotationStatsDTO {
    private Long totalQuotations;
    private Long validatedQuotations;
    private Long unvalidatedQuotations;
    private Double concretizationRate;
    private List<SellerStatsDTO> sellerStats; // Nouvelle propriété

    // Constructeur
    public QuotationStatsDTO(Long total, Long validated, Long unvalidated) {
        this.totalQuotations = total;
        this.validatedQuotations = validated;
        this.unvalidatedQuotations = unvalidated;
        this.concretizationRate = total > 0 ? (validated * 100.0) / total : 0.0;
    }

    // Getters
    public Long getTotalQuotations() {
        return totalQuotations;
    }

    public Long getValidatedQuotations() {
        return validatedQuotations;
    }

    public Long getUnvalidatedQuotations() {
        return unvalidatedQuotations;
    }

    public Double getConcretizationRate() {
        return concretizationRate;
    }

    public List<SellerStatsDTO> getSellerStats() {
        return sellerStats;
    }

    // Setter
    public void setSellerStats(List<SellerStatsDTO> sellerStats) {
        this.sellerStats = sellerStats;
    }
}