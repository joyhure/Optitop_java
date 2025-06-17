package com.optitop.optitop_api.dto;

// ===== IMPORTS UTILITAIRES =====
import java.util.List;

/**
 * DTO pour les statistiques globales des devis optiques
 * 
 * Contient les métriques de performance des devis sur une période :
 * - Nombre total de devis créés
 * - Nombre de devis validés (concrétisés en vente)
 * - Nombre de devis non validés (en attente de traitement)
 * - Taux de concrétisation calculé automatiquement
 * - Détail des statistiques par vendeur
 * 
 * Utilisé pour l'affichage des tableaux de bord et le suivi des performances
 * commerciales dans l'interface de gestion des devis.
 */
public class QuotationStatsDTO {

    // ===== PROPRIÉTÉS GLOBALES =====

    /**
     * Nombre total de devis sur la période
     */
    private Long totalQuotations;

    /**
     * Nombre de devis validés (concrétisés en vente)
     */
    private Long validatedQuotations;

    /**
     * Nombre de devis non validés (en attente de traitement)
     */
    private Long unvalidatedQuotations;

    /**
     * Taux de concrétisation en pourcentage (0-100)
     * Calculé automatiquement : (validés / total) * 100
     */
    private Double concretizationRate;

    // ===== PROPRIÉTÉS DÉTAILLÉES =====

    /**
     * Statistiques détaillées par vendeur
     * Permet l'analyse des performances individuelles
     */
    private List<SellerStatsDTO> sellerStats;

    // ===== CONSTRUCTEUR =====

    /**
     * Constructeur principal avec calcul automatique du taux de concrétisation
     * 
     * @param total       Nombre total de devis
     * @param validated   Nombre de devis validés
     * @param unvalidated Nombre de devis non validés
     */
    public QuotationStatsDTO(Long total, Long validated, Long unvalidated) {
        this.totalQuotations = total;
        this.validatedQuotations = validated;
        this.unvalidatedQuotations = unvalidated;

        // Calcul automatique du taux de concrétisation avec protection division par
        // zéro
        this.concretizationRate = (total != null && total > 0)
                ? (validated * 100.0) / total
                : 0.0;
    }

    // ===== GETTERS =====

    /**
     * @return Nombre total de devis
     */
    public Long getTotalQuotations() {
        return totalQuotations;
    }

    /**
     * @return Nombre de devis validés
     */
    public Long getValidatedQuotations() {
        return validatedQuotations;
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

    /**
     * @return Liste des statistiques par vendeur
     */
    public List<SellerStatsDTO> getSellerStats() {
        return sellerStats;
    }

    // ===== SETTERS =====

    /**
     * @param sellerStats Statistiques par vendeur à définir
     */
    public void setSellerStats(List<SellerStatsDTO> sellerStats) {
        this.sellerStats = sellerStats;
    }
}