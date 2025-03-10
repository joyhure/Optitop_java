package com.optitop.optitop_api.dto;

public class FrameStatsDTO {
    private String sellerRef;
    private Long totalFrames;
    private Long premiumFrames;

    public FrameStatsDTO(String sellerRef, Long totalFrames, Long premiumFrames) {
        this.sellerRef = sellerRef;
        this.totalFrames = totalFrames;
        this.premiumFrames = premiumFrames;
    }

    public String getSellerRef() {
        return sellerRef;
    }

    public Long getTotalFrames() {
        return totalFrames;
    }

    public Long getPremiumFrames() {
        return premiumFrames;
    }
}