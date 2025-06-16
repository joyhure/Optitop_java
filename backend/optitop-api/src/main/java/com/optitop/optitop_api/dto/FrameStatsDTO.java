package com.optitop.optitop_api.dto;

/**
 * DTO pour les statistiques de vente des montures par vendeur
 * 
 * Contient les données de performance liées aux montures optiques :
 * - Nombre total de montures vendues
 * - Nombre de montures primées vendues (éligibles aux primes)
 * 
 * Utilisé pour calculer les taux de montures primées et les primes
 * associées dans le système de rémunération variable.
 */
public class FrameStatsDTO {

    // ===== PROPRIÉTÉS =====

    /**
     * Référence du vendeur (identifiant unique)
     */
    private String sellerRef;

    /**
     * Nombre total de montures vendues sur la période
     */
    private Long totalFrames;

    /**
     * Nombre de montures primées vendues (éligibles aux primes)
     */
    private Long premiumFrames;

    // ===== CONSTRUCTEUR =====

    /**
     * Constructeur principal pour initialiser les statistiques de montures
     * 
     * @param sellerRef     Référence du vendeur
     * @param totalFrames   Nombre total de montures vendues
     * @param premiumFrames Nombre de montures primées vendues
     */
    public FrameStatsDTO(String sellerRef, Long totalFrames, Long premiumFrames) {
        this.sellerRef = sellerRef;
        this.totalFrames = totalFrames;
        this.premiumFrames = premiumFrames;
    }

    // ===== GETTERS =====

    /**
     * @return Référence du vendeur
     */
    public String getSellerRef() {
        return sellerRef;
    }

    /**
     * @return Nombre total de montures vendues
     */
    public Long getTotalFrames() {
        return totalFrames;
    }

    /**
     * @return Nombre de montures primées vendues
     */
    public Long getPremiumFrames() {
        return premiumFrames;
    }
}