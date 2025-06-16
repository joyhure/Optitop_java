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
 * Entité représentant une ligne de facture
 * 
 * Chaque ligne correspond à un produit vendu avec ses détails :
 * - Informations client et facture
 * - Détails produit (famille, quantité, montant)
 * - Classification optique (P1/P2 pour paire principale/secondaire)
 * - Référence vendeur
 * - Statut (facture/avoir) pour la gestion des retours
 * 
 * Utilisée pour les calculs de statistiques de vente.
 */
@Entity
@Table(name = "invoices_lines")
public class InvoicesLines {

    // ===== PROPRIÉTÉS PRINCIPALES =====

    /**
     * Identifiant unique de la ligne de facture
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Date de la vente (date de facturation)
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Identifiant unique du client
     */
    @Column(nullable = false, name = "client_id")
    private String clientId;

    /**
     * Nom du client pour affichage
     */
    @Column(nullable = false)
    private String client;

    /**
     * Référence de la facture (numéro de facture)
     */
    @Column(nullable = false, name = "invoice_ref")
    private String invoiceRef;

    // ===== PROPRIÉTÉS PRODUIT =====

    /**
     * Famille de produit optique
     * Valeurs possibles : MON (montures), VER (verres), ACC (accessoires)
     */
    @Column(nullable = true)
    private String family;

    /**
     * Quantité du produit vendu
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Montant TTC de la ligne de facture
     * Peut être négatif pour les avoirs
     */
    @Column(nullable = false, name = "total_ttc")
    private Double totalTtc;

    /**
     * Classification optique : paire principale (1) ou secondaire (2)
     * P1 = Équipement principal, P2 = Équipement secondaire
     */
    @Column(nullable = true)
    private Integer pair;

    // ===== PROPRIÉTÉS MÉTIER =====

    /**
     * Vendeur associé à la vente (relation Many-to-One)
     * Utilisé pour le calcul des commissions et statistiques
     */
    @ManyToOne
    @JoinColumn(name = "seller_ref", referencedColumnName = "seller_ref", nullable = false, foreignKey = @ForeignKey(name = "fk_invoices_lines_seller_ref"))
    private Seller seller;

    /**
     * Montant total TTC de la facture complète
     * Permet de connaître le contexte global de la vente
     */
    @Column(nullable = false, name = "total_invoice")
    private Double totalInvoice;

    /**
     * Statut de la ligne : "facture" ou "avoir"
     * Permet de distinguer les ventes des retours
     */
    @Column(nullable = false)
    private String status;

    // ===== PROPRIÉTÉS TECHNIQUES =====

    /**
     * Date et heure de création de l'enregistrement
     * Utilisée pour l'audit et le tri chronologique
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ===== GETTERS ET SETTERS =====

    /**
     * @return Identifiant unique de la ligne
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
     * @return Date de facturation
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @param date Date de facturation à définir
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
     * @return Référence de la facture
     */
    public String getInvoiceRef() {
        return invoiceRef;
    }

    /**
     * @param invoiceRef Référence de facture à définir
     */
    public void setInvoiceRef(String invoiceRef) {
        this.invoiceRef = invoiceRef;
    }

    /**
     * @return Famille de produit (MON, VER, ACC)
     */
    public String getFamily() {
        return family;
    }

    /**
     * @param family Famille de produit à définir
     */
    public void setFamily(String family) {
        this.family = family;
    }

    /**
     * @return Quantité vendue
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
     * @return Vendeur associé
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
     * @return Montant total de la facture
     */
    public Double getTotalInvoice() {
        return totalInvoice;
    }

    /**
     * @param totalInvoice Montant total de facture à définir
     */
    public void setTotalInvoice(Double totalInvoice) {
        this.totalInvoice = totalInvoice;
    }

    /**
     * @return Classification paire (1=P1, 2=P2)
     */
    public Integer getPair() {
        return pair;
    }

    /**
     * @param pair Classification paire à définir
     */
    public void setPair(Integer pair) {
        this.pair = pair;
    }

    /**
     * @return Statut (facture/avoir)
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
