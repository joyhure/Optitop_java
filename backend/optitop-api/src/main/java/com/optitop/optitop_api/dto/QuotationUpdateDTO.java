package com.optitop.optitop_api.dto;

/**
 * DTO pour la mise à jour des devis optiques
 * 
 * Contient les données modifiables d'un devis lors des opérations batch :
 * - Identifiant unique du devis (immutable pour la traçabilité)
 * - Action à appliquer au devis (validation, refus, attente...)
 * - Commentaire libre pour justifier l'action
 * 
 * Utilisé spécifiquement pour les mises à jour en lot depuis l'interface
 * de gestion des devis non validés. Optimise les performances en regroupant
 * plusieurs modifications en une seule transaction.
 */
public class QuotationUpdateDTO {

    // ===== PROPRIÉTÉS =====

    /**
     * Identifiant unique du devis à modifier (immutable)
     * Référence technique pour identifier le devis ciblé
     */
    private final Long id;

    /**
     * Action à appliquer au devis
     * Correspond aux valeurs de l'enum QuotationAction
     */
    private String action;

    /**
     * Commentaire libre associé à l'action
     * Permet de justifier ou préciser la décision prise
     */
    private String comment;

    // ===== CONSTRUCTEUR =====

    /**
     * Constructeur principal avec identifiant obligatoire
     * 
     * L'ID est immutable pour garantir l'intégrité lors des mises à jour
     * en lot et éviter les erreurs de ciblage
     * 
     * @param id Identifiant unique du devis à modifier (obligatoire)
     */
    public QuotationUpdateDTO(Long id) {
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
     * @return Action à appliquer au devis
     */
    public String getAction() {
        return action;
    }

    /**
     * @return Commentaire associé à l'action
     */
    public String getComment() {
        return comment;
    }

    // ===== SETTERS =====

    /**
     * @param action Action à appliquer au devis
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @param comment Commentaire à associer à l'action
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}