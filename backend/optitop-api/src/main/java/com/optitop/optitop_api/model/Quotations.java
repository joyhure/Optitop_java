package com.optitop.optitop_api.model;

// ===== IMPORTS JPA =====
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Entité représentant un devis optique
 * 
 * Contient les informations d'un devis client avec son cycle de vie :
 * - Identification client et devis
 * - Vendeur responsable du devis
 * - Statut de validation (validé/non validé)
 * - Action de suivi appliquée au devis
 * - Commentaires libres pour le suivi
 * 
 * Utilisée pour la gestion du pipeline commercial et le suivi des prospects.
 * Les devis non validés nécessitent un traitement par les managers.
 */
@Entity
@Table(name = "quotations")
public class Quotations {

    // ===== PROPRIÉTÉS PRINCIPALES =====

    /**
     * Identifiant unique du devis
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
    @Column(name = "client_id", nullable = false)
    private String clientId;

    /**
     * Nom du client pour affichage
     */
    @Column(nullable = false)
    private String client;

    // ===== PROPRIÉTÉS MÉTIER =====

    /**
     * Vendeur responsable du devis (relation Many-to-One)
     * Utilisé pour le filtrage par collaborateur et les statistiques
     */
    @ManyToOne
    @JoinColumn(name = "seller_ref", referencedColumnName = "seller_ref", foreignKey = @ForeignKey(name = "fk_quotations_seller_ref"))
    private Seller seller;

    /**
     * Statut de validation du devis
     * true = devis validé (concrétisé en vente)
     * false = devis non validé (en attente de traitement)
     */
    @Column(name = "is_validated", nullable = false)
    private Boolean isValidated;

    /**
     * Action de suivi appliquée au devis
     * Définit l'étape suivante dans le processus commercial
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private QuotationAction action;

    /**
     * Commentaire libre associé au devis
     * Permet d'ajouter des précisions sur le suivi ou l'action
     */
    @Column(name = "comment")
    private String comment;

    // ===== PROPRIÉTÉS TECHNIQUES =====

    /**
     * Date et heure de création de l'enregistrement
     * Utilisée pour l'audit et le tri chronologique
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ===== ENUM ACTIONS =====

    /**
     * Énumération des actions possibles sur un devis
     * 
     * Définit les étapes du processus de suivi commercial :
     * - VOIR_OPTICIEN : Rendez-vous opticien nécessaire
     * - NON_VALIDE : Devis refusé ou abandonné
     * - ATTENTE_MUTUELLE : En attente de réponse mutuelle
     * - A_RELANCER : Client à relancer prochainement
     * - ATTENTE_RETOUR : En attente de retour client
     */
    public enum QuotationAction {
        VOIR_OPTICIEN("Voir opticien"),
        NON_VALIDE("Non validé"),
        ATTENTE_MUTUELLE("Attente mutuelle"),
        A_RELANCER("A relancer"),
        ATTENTE_RETOUR("Attente de retour");

        /**
         * Libellé affiché pour l'action
         */
        private final String value;

        /**
         * Constructeur de l'enum avec libellé
         * 
         * @param value Libellé à afficher pour l'action
         */
        QuotationAction(String value) {
            this.value = value;
        }

        /**
         * @return Libellé de l'action pour affichage
         */
        public String getValue() {
            return value;
        }
    }

    // ===== CONSTRUCTEURS =====

    /**
     * Constructeur par défaut pour JPA
     */
    public Quotations() {
    }

    /**
     * Constructeur complet pour création programmatique
     * 
     * @param date        Date de création du devis
     * @param clientId    Identifiant du client
     * @param client      Nom du client
     * @param seller      Vendeur responsable
     * @param isValidated Statut de validation
     */
    public Quotations(LocalDate date, String clientId, String client, Seller seller, Boolean isValidated) {
        this.date = date;
        this.clientId = clientId;
        this.client = client;
        this.seller = seller;
        this.isValidated = isValidated;
        this.createdAt = LocalDateTime.now();
    }

    // ===== GETTERS ET SETTERS =====

    /**
     * @return Identifiant unique du devis
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
     * @return Vendeur responsable du devis
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
     * @return Statut de validation du devis
     */
    public Boolean getIsValidated() {
        return isValidated;
    }

    /**
     * @param isValidated Statut de validation à définir
     */
    public void setIsValidated(Boolean isValidated) {
        this.isValidated = isValidated;
    }

    /**
     * @return Action de suivi appliquée
     */
    public QuotationAction getAction() {
        return action;
    }

    /**
     * @param action Action de suivi à définir
     */
    public void setAction(QuotationAction action) {
        this.action = action;
    }

    /**
     * @return Commentaire associé au devis
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment Commentaire à associer
     */
    public void setComment(String comment) {
        this.comment = comment;
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