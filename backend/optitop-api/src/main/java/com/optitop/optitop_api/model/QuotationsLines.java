package com.optitop.optitop_api.model;

// ===== IMPORTS JPA =====
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// ===== IMPORTS UTILITAIRES =====
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant une ligne de devis optique
 * 
 * Contient le détail des articles ou services d'un devis :
 * - Informations client et référence du devis
 * - Détails de l'article (famille, quantité, prix)
 * - Vendeur responsable de la ligne
 * - Montants TTC et totaux du devis
 * - Statut et informations de paire (OD/OG)
 * 
 * Utilisée pour :
 * - Le calcul des statistiques de vente par article
 * - L'analyse des performances par famille de produits
 * - Le suivi détaillé des devis et de leur composition
 * - Les rapports de commission par vendeur
 */
@Entity
@Table(name = "quotations_lines")
public class QuotationsLines {

    // ===== PROPRIÉTÉS PRINCIPALES =====

    /**
     * Identifiant unique de la ligne de devis
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Date de création du devis
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Identifiant unique du client
     */
    @Column(nullable = false)
    private String clientId;

    /**
     * Nom du client pour affichage
     */
    @Column(nullable = false)
    private String client;

    /**
     * Référence du devis parent
     * Permet de regrouper les lignes d'un même devis
     */
    @Column(nullable = false)
    private String quotationRef;

    // ===== PROPRIÉTÉS PRODUIT =====

    /**
     * Famille de produits de l'article
     * Utilisée pour les analyses par catégorie
     */
    @Column(nullable = true)
    private String family;

    /**
     * Quantité commandée pour cet article
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Montant TTC de cette ligne
     */
    @Column(nullable = false)
    private Double totalTtc;

    /**
     * Montant total du devis complet
     * Répété sur chaque ligne pour faciliter les calculs
     */
    @Column(nullable = false)
    private Double totalQuotation;

    /**
     * Numéro de paire (OD/OG) pour les produits optiques
     * Permet le suivi des articles appariés
     */
    @Column(nullable = true)
    private Integer pair;

    /**
     * Statut de la ligne de devis
     * Indique l'état de traitement ou de livraison
     */
    @Column(nullable = false)
    private String status;

    // ===== RELATIONS =====

    /**
     * Vendeur responsable de cette ligne de devis
     * Utilisé pour le calcul des commissions et statistiques
     */
    @ManyToOne
    @JoinColumn(name = "seller_ref", referencedColumnName = "seller_ref", nullable = true, foreignKey = @ForeignKey(name = "fk_quotations_lines_seller"))
    private Seller seller;

    // ===== PROPRIÉTÉS TECHNIQUES =====

    /**
     * Date et heure de création de l'enregistrement
     * Utilisée pour l'audit et le tri chronologique
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ===== CONSTRUCTEURS =====

    /**
     * Constructeur par défaut pour JPA
     */
    public QuotationsLines() {
    }

    /**
     * Constructeur complet pour création programmatique
     * 
     * @param date           Date du devis
     * @param clientId       Identifiant du client
     * @param client         Nom du client
     * @param quotationRef   Référence du devis
     * @param family         Famille de produits
     * @param quantity       Quantité
     * @param totalTtc       Montant TTC de la ligne
     * @param totalQuotation Montant total du devis
     * @param status         Statut de la ligne
     */
    public QuotationsLines(LocalDate date, String clientId, String client,
            String quotationRef, String family, Integer quantity,
            Double totalTtc, Double totalQuotation, String status) {
        this.date = date;
        this.clientId = clientId;
        this.client = client;
        this.quotationRef = quotationRef;
        this.family = family;
        this.quantity = quantity;
        this.totalTtc = totalTtc;
        this.totalQuotation = totalQuotation;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // ===== GETTERS ET SETTERS =====

    /**
     * @return Identifiant unique de la ligne de devis
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id Identifiant unique à définir
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Date de création du devis
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @param date Date de création à définir
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * @return Identifiant du client
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId Identifiant du client à définir
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return Nom du client
     */
    public String getClient() {
        return client;
    }

    /**
     * @param client Nom du client à définir
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * @return Référence du devis parent
     */
    public String getQuotationRef() {
        return quotationRef;
    }

    /**
     * @param quotationRef Référence du devis à définir
     */
    public void setQuotationRef(String quotationRef) {
        this.quotationRef = quotationRef;
    }

    /**
     * @return Famille de produits
     */
    public String getFamily() {
        return family;
    }

    /**
     * @param family Famille de produits à définir
     */
    public void setFamily(String family) {
        this.family = family;
    }

    /**
     * @return Quantité commandée
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * @param quantity Quantité à définir
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @return Montant TTC de la ligne
     */
    public Double getTotalTtc() {
        return totalTtc;
    }

    /**
     * @param totalTtc Montant TTC à définir
     */
    public void setTotalTtc(Double totalTtc) {
        this.totalTtc = totalTtc;
    }

    /**
     * @return Vendeur responsable de la ligne
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * @param seller Vendeur à associer
     */
    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    /**
     * @return Montant total du devis
     */
    public Double getTotalQuotation() {
        return totalQuotation;
    }

    /**
     * @param totalQuotation Montant total du devis à définir
     */
    public void setTotalQuotation(Double totalQuotation) {
        this.totalQuotation = totalQuotation;
    }

    /**
     * @return Numéro de paire (OD/OG)
     */
    public Integer getPair() {
        return pair;
    }

    /**
     * @param pair Numéro de paire à définir
     */
    public void setPair(Integer pair) {
        this.pair = pair;
    }

    /**
     * @return Statut de la ligne de devis
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status Statut à définir
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return Date de création de l'enregistrement
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt Date de création à définir
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}