package com.optitop.optitop_api.dto;

// ===== IMPORTS UTILITAIRES =====
import java.time.LocalDate;

/**
 * DTO pour les informations de devis optiques
 * 
 * Contient les données essentielles d'un devis pour l'affichage et la
 * modification :
 * - Identification unique du devis (ID immutable)
 * - Informations temporelles (date de création)
 * - Données commerciales (vendeur, client)
 * - Statut de traitement (action appliquée, commentaires)
 * 
 * Utilisé pour les échanges API entre le frontend et le backend
 * dans la gestion des devis non validés et leurs mises à jour.
 */
public class QuotationDTO {

    // ===== PROPRIÉTÉS =====

    /**
     * Identifiant unique du devis (immutable)
     * Référence technique pour les opérations de mise à jour
     */
    private final Long id;

    /**
     * Date de création du devis
     */
    private LocalDate date;

    /**
     * Référence du vendeur responsable du devis
     * Utilisée pour le filtrage par collaborateur
     */
    private String seller;

    /**
     * Nom du client concerné par le devis
     */
    private String client;

    /**
     * Action appliquée au devis (libellé affiché)
     * Correspond aux valeurs de l'enum QuotationAction
     */
    private String action;

    /**
     * Commentaire libre associé au devis
     * Permet d'ajouter des précisions sur le traitement
     */
    private String comment;

    // ===== CONSTRUCTEUR =====

    /**
     * Constructeur principal avec identifiant obligatoire
     * 
     * L'ID est immutable pour garantir l'intégrité référentielle
     * lors des opérations de mise à jour
     * 
     * @param id Identifiant unique du devis (obligatoire)
     */
    public QuotationDTO(Long id) {
        this.id = id;
    }

    // ===== GETTERS =====

    /**
     * @return Identifiant unique du devis
     */
    public Long getId() {
        return id;
    }

    /**
     * @return Date de création du devis
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @return Référence du vendeur responsable
     */
    public String getSeller() {
        return seller;
    }

    /**
     * @return Nom du client
     */
    public String getClient() {
        return client;
    }

    /**
     * @return Action appliquée au devis
     */
    public String getAction() {
        return action;
    }

    /**
     * @return Commentaire associé au devis
     */
    public String getComment() {
        return comment;
    }

    // ===== SETTERS =====

    /**
     * @param date Date de création à définir
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * @param seller Référence vendeur à définir
     */
    public void setSeller(String seller) {
        this.seller = seller;
    }

    /**
     * @param client Nom du client à définir
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * @param action Action à appliquer au devis
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @param comment Commentaire à associer au devis
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}