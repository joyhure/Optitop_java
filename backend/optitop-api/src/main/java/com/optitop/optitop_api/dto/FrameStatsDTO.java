package com.optitop.optitop_api.dto;

public class FrameStatsDTO {
    private String sellerRef;
    private Long totalFrames;

    public FrameStatsDTO(String sellerRef, Long totalFrames) {
        this.sellerRef = sellerRef;
        this.totalFrames = totalFrames;
    }

    public String getSellerRef() {
        return sellerRef;
    }

    public Long getTotalFrames() {
        return totalFrames;
    }
}