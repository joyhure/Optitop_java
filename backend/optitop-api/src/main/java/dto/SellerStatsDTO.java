package dto;

public class SellerStatsDTO {
    private String initials;
    private Long total;
    private Long unvalidated;
    private Double rate;

    // Constructeur
    public SellerStatsDTO(String initials, Long total, Long unvalidated) {
        this.initials = initials;
        this.total = total;
        this.unvalidated = unvalidated;
        this.rate = total > 0 ? ((total - unvalidated) * 100.0) / total : 0.0;
    }

    // Getters et Setters
    public String getInitials() {
        return initials;
    }

    public Long getTotal() {
        return total;
    }

    public Long getUnvalidated() {
        return unvalidated;
    }

    public Double getRate() {
        return rate;
    }
}