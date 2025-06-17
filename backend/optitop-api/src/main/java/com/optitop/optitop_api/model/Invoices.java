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
 * Entité représentant une facture (niveau entête)
 * 
 * Contient les informations globales d'une facture :
 * - Identification client et facture
 * - Montant total TTC de la facture complète
 * - Vendeur responsable de la vente
 * - Statut (facture/avoir) pour la gestion des retours
 * - Indicateur optique pour distinguer les ventes optiques des autres
 * 
 * Utilisée pour les calculs de chiffre d'affaires global et statistiques
 * vendeur.
 * Associée à InvoicesLines pour le détail des produits vendus.
 */
@Entity
@Table(name = "invoices")
public class Invoices {

    // ===== PROPRIÉTÉS PRINCIPALES =====

    /**
     * Identifiant unique de la facture
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Date de facturation
     */
    @Column(name = "date", nullable = false)
    private LocalDate date;

    /**
     * Identifiant unique du client
     */
    @Column(name = "client_id", nullable = false)
    private String clientId;

    /**
     * Nom du client pour affichage
     */
    @Column(name = "client", nullable = false)
    private String client;

    /**
     * Référence unique de la facture (numéro de facture)
     */
    @Column(name = "invoice_ref", nullable = false, unique = true)
    private String invoiceRef;

    // ===== PROPRIÉTÉS MÉTIER =====

    /**
     * Vendeur responsable de la vente (relation Many-to-One)
     * Utilisé pour le calcul des commissions et statistiques de performance
     */
    @ManyToOne
    @JoinColumn(name = "seller_ref", referencedColumnName = "seller_ref", foreignKey = @ForeignKey(name = "fk_invoices_seller_ref"))
    private Seller seller;

    /**
     * Montant total TTC de la facture
     * Peut être négatif pour les avoirs (remboursements)
     */
    @Column(name = "total_invoice", nullable = false)
    private Double totalInvoice;

    /**
     * Statut de la facture : "facture" ou "avoir"
     * Permet de distinguer les ventes des retours/remboursements
     */
    @Column(name = "status", nullable = false)
    private String status;

    /**
     * Indicateur de vente optique (true) ou non-optique (false)
     * Filtre les ventes optiques pour les statistiques métier
     */
    @Column(name = "is_optical")
    private Boolean isOptical;

    // ===== PROPRIÉTÉS TECHNIQUES =====

    /**
     * Date et heure de création de l'enregistrement
     * Utilisée pour l'audit et le tri chronologique
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ===== GETTERS ET SETTERS =====

    /**
     * @return Identifiant unique de la facture
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
     * @return Référence unique de la facture
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
     * @return Vendeur responsable de la vente
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
     * @return Montant total TTC de la facture
     */
    public Double getTotalInvoice() {
        return totalInvoice;
    }

    /**
     * @param totalInvoice Montant total à définir
     */
    public void setTotalInvoice(Double totalInvoice) {
        this.totalInvoice = totalInvoice;
    }

    /**
     * @return Statut de la facture (facture/avoir)
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
     * @return Indicateur de vente optique
     */
    public Boolean getIsOptical() {
        return isOptical;
    }

    /**
     * @param isOptical Indicateur optique à définir
     */
    public void setIsOptical(Boolean isOptical) {
        this.isOptical = isOptical;
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
