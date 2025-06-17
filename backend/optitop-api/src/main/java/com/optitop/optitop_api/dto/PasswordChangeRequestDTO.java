package com.optitop.optitop_api.dto;

/**
 * DTO pour les demandes de changement de mot de passe
 * 
 * Classe de transfert de données pour transporter les informations
 * nécessaires lors d'une modification de mot de passe utilisateur.
 */
public class PasswordChangeRequestDTO {

    // ===== PROPRIÉTÉS =====

    /**
     * Mot de passe actuel de l'utilisateur
     */
    private String currentPassword;

    /**
     * Nouveau mot de passe souhaité
     */
    private String newPassword;

    // ===== GETTERS ET SETTERS =====

    /**
     * @return Mot de passe actuel de l'utilisateur
     */
    public String getCurrentPassword() {
        return currentPassword;
    }

    /**
     * @param currentPassword Mot de passe actuel à définir
     */
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    /**
     * @return Nouveau mot de passe souhaité
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * @param newPassword Nouveau mot de passe à définir
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}