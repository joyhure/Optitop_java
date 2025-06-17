package com.optitop.optitop_api.dto;

// ===== IMPORTS MODÈLES =====
import com.optitop.optitop_api.model.User.Role;

// ===== IMPORTS UTILITAIRES =====
import java.time.LocalDateTime;

/**
 * DTO d'affichage pour les informations utilisateur
 * 
 * Classe de transfert de données optimisée pour l'affichage
 * des informations utilisateur dans les interfaces.
 */
public class UserDisplayDTO {

    // ===== PROPRIÉTÉS =====

    /**
     * Identifiant unique de l'utilisateur
     */
    private Integer id;

    /**
     * Identifiant de connexion
     */
    private String login;

    /**
     * Rôle de l'utilisateur
     */
    private Role role;

    /**
     * Nom de famille
     */
    private String lastname;

    /**
     * Prénom
     */
    private String firstname;

    /**
     * Adresse email
     */
    private String email;

    /**
     * Date de création du compte
     */
    private LocalDateTime createdAt;

    // ===== CONSTRUCTEUR =====

    /**
     * Constructeur avec tous les paramètres
     * 
     * @param id        Identifiant unique
     * @param login     Identifiant de connexion
     * @param role      Rôle utilisateur
     * @param lastname  Nom de famille
     * @param firstname Prénom
     * @param email     Adresse email
     * @param createdAt Date de création
     */
    public UserDisplayDTO(
            Integer id,
            String login,
            Role role,
            String lastname,
            String firstname,
            String email,
            LocalDateTime createdAt) {
        this.id = id;
        this.login = login;
        this.role = role;
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.createdAt = createdAt;
    }

    // ===== GETTERS =====

    /**
     * @return Identifiant unique de l'utilisateur
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return Identifiant de connexion
     */
    public String getLogin() {
        return login;
    }

    /**
     * @return Rôle de l'utilisateur
     */
    public Role getRole() {
        return role;
    }

    /**
     * @return Nom de famille
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * @return Prénom
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @return Adresse email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return Date de création du compte
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}