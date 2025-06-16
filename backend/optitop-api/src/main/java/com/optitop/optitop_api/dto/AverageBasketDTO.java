package com.optitop.optitop_api.dto;

/**
 * DTO pour les statistiques de paniers moyens par vendeur
 * 
 * Contient toutes les métriques calculées pour analyser les performances
 * de vente d'un vendeur sur une période donnée :
 * - Panier moyen global
 * - Panier moyen P1 Montures (équipements optiques)
 * - Panier moyen P1 Verres (verres correcteurs)
 * - Panier moyen P2 (équipements secondaires)
 * - Nombre de P2 vendus
 */
public class AverageBasketDTO {

    // ===== PROPRIÉTÉS =====

    /**
     * Référence du vendeur (identifiant unique)
     */
    private String sellerRef;

    /**
     * Montant total des ventes sur la période
     */
    private Double totalAmount;

    /**
     * Nombre total de factures émises
     */
    private Long invoiceCount;

    /**
     * Panier moyen global (totalAmount / invoiceCount)
     */
    private Double averageBasket;

    /**
     * Panier moyen P1 Montures (équipements optiques)
     */
    private Double averageP1MON;

    /**
     * Panier moyen P1 Verres (verres correcteurs)
     */
    private Double averageP1VER;

    /**
     * Panier moyen P2 (équipements secondaires)
     */
    private Double averageP2;

    /**
     * Nombre de P2 vendus sur la période
     */
    private Long p2Count;

    // ===== CONSTRUCTEUR =====

    /**
     * Constructeur principal avec calcul automatique des moyennes
     * 
     * @param sellerRef        Référence du vendeur
     * @param totalAmount      Montant total des ventes
     * @param invoiceCount     Nombre de factures
     * @param totalAmountP1MON Montant total P1 Montures
     * @param countP1MON       Nombre d'éléments P1 Montures
     * @param totalAmountP1VER Montant total P1 Verres
     * @param countP1VER       Nombre d'éléments P1 Verres
     * @param totalAmountP2    Montant total P2
     * @param countP2          Nombre d'éléments P2
     */
    public AverageBasketDTO(String sellerRef, Double totalAmount, Long invoiceCount,
            Double totalAmountP1MON, Long countP1MON,
            Double totalAmountP1VER, Long countP1VER,
            Double totalAmountP2, Long countP2) {

        this.sellerRef = sellerRef;
        this.totalAmount = totalAmount;
        this.invoiceCount = invoiceCount;
        this.p2Count = countP2;

        // Calcul automatique des moyennes
        this.averageBasket = calculateAverage(totalAmount, invoiceCount);
        this.averageP1MON = calculateAverage(totalAmountP1MON, countP1MON);
        this.averageP1VER = calculateAverage(totalAmountP1VER, countP1VER);
        this.averageP2 = calculateAverage(totalAmountP2, countP2);
    }

    // ===== MÉTHODES UTILITAIRES =====

    /**
     * Calcule une moyenne en gérant les cas de division par zéro
     * 
     * @param amount Montant total
     * @param count  Nombre d'éléments
     * @return Moyenne calculée ou null si impossible
     */
    private Double calculateAverage(Double amount, Long count) {
        return (count != null && count > 0 && amount != null) ? amount / count : null;
    }

    // ===== GETTERS ET SETTERS =====

    /**
     * @return Référence du vendeur
     */
    public String getSellerRef() {
        return sellerRef;
    }

    /**
     * @return Montant total des ventes
     */
    public Double getTotalAmount() {
        return totalAmount;
    }

    /**
     * @return Nombre total de factures
     */
    public Long getInvoiceCount() {
        return invoiceCount;
    }

    /**
     * @return Panier moyen global
     */
    public Double getAverageBasket() {
        return averageBasket;
    }

    /**
     * @return Panier moyen P1 Montures
     */
    public Double getAverageP1MON() {
        return averageP1MON;
    }

    /**
     * @return Panier moyen P1 Verres
     */
    public Double getAverageP1VER() {
        return averageP1VER;
    }

    /**
     * @return Panier moyen P2
     */
    public Double getAverageP2() {
        return averageP2;
    }

    /**
     * @return Nombre de P2 vendus
     */
    public Long getP2Count() {
        return p2Count;
    }
}