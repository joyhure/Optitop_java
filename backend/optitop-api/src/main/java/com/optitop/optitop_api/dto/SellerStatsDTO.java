package com.optitop.optitop_api.dto;

/**
 * DTO pour les statistiques de devis par vendeur
 * 
 * Contient les métriques de performance individuelles d'un vendeur :
 * - Référence unique du vendeur (identifiant commercial)
 * - Nombre total de devis créés sur la période
 * - Nombre de devis non validés (en attente de traitement)
 * - Taux de concrétisation calculé automatiquement
 * 
 * Utilisé pour l'affichage des performances individuelles dans les tableaux
 * de bord de gestion des devis et le suivi des équipes commerciales.
 */
public class SellerStatsDTO {

    // ===== PROPRIÉTÉS =====

    /**
     * Référence unique du vendeur (identifiant commercial)
     */
    private String sellerRef;

    /**
     * Nombre total de devis créés par le vendeur
     */
    private Long totalQuotations;

    /**
     * Nombre de devis non validés (en attente de traitement)
     */
    private Long unvalidatedQuotations;

    /**
     * Taux de concrétisation en pourcentage (0-100)
     * Calculé automatiquement : ((total - non validés) / total) * 100
     */
    private Double concretizationRate;

    // ===== CONSTRUCTEUR =====

    /**
     * Constructeur principal avec calcul automatique du taux de concrétisation
     * 
     * Le taux est calculé selon la formule :
     * (devis validés / total) * 100 = ((total - non validés) / total) * 100
     * 
     * @param sellerRef             Référence du vendeur
     * @param totalQuotations       Nombre total de devis
     * @param unvalidatedQuotations Nombre de devis non validés
     */
    public SellerStatsDTO(String sellerRef, Long totalQuotations, Long unvalidatedQuotations) {
        this.sellerRef = sellerRef;
        this.totalQuotations = totalQuotations;
        this.unvalidatedQuotations = unvalidatedQuotations;

        // Calcul automatique du taux avec protection division par zéro
        this.concretizationRate = (totalQuotations != null && totalQuotations > 0)
                ? ((totalQuotations - unvalidatedQuotations) * 100.0) / totalQuotations
                : 0.0;
    }

    // ===== GETTERS =====

    /**
     * @return Référence unique du vendeur
     */
    public String getSellerRef() {
        return sellerRef;
    }

    /**
     * @return Nombre total de devis créés
     */
    public Long getTotalQuotations() {
        return totalQuotations;
    }

    /**
     * @return Nombre de devis non validés
     */
    public Long getUnvalidatedQuotations() {
        return unvalidatedQuotations;
    }

    /**
     * @return Taux de concrétisation en pourcentage
     */
    public Double getConcretizationRate() {
        return concretizationRate;
    }
}