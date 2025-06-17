package com.optitop.optitop_api.model;

// ===== IMPORTS JPA =====
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// ===== IMPORTS MODÈLES =====
import com.optitop.optitop_api.model.User.Role;

// ===== IMPORTS UTILITAIRES =====
import java.time.LocalDateTime;

/**
 * Entité représentant une demande de compte utilisateur en attente
 * 
 * Stocke les demandes de création, modification ou suppression de comptes
 * soumises par les utilisateurs autorisés et en attente de validation
 * par un administrateur.
 */
@Entity
@Table(name = "pending_accounts")
public class PendingAccount {

    // ===== ÉNUMÉRATION =====

    /**
     * Types de demandes possibles sur les comptes utilisateurs
     */
    public enum RequestType {
        ajout,
        modification,
        suppression
    }

    // ===== PROPRIÉTÉS =====

    /**
     * Identifiant unique de la demande
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Nom de famille de l'utilisateur concerné
     */
    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;

    /**
     * Prénom de l'utilisateur concerné
     */
    @Column(name = "firstname", nullable = false, length = 100)
    private String firstname;

    /**
     * Adresse email de l'utilisateur concerné
     */
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    /**
     * Identifiant de connexion de l'utilisateur
     */
    @Column(name = "login", nullable = false, length = 50, unique = true)
    private String login;

    /**
     * Rôle de l'utilisateur
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    /**
     * Type de demande à effectuer
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private RequestType requestType;

    /**
     * Utilisateur qui a créé cette demande
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_pending_account_created_by"), nullable = false)
    private User createdBy;

    /**
     * Date et heure de création de la demande
     */
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // ===== CONSTRUCTEURS =====

    /**
     * Constructeur par défaut requis par JPA
     */
    public PendingAccount() {
    }

    /**
     * Constructeur avec paramètres
     */
    public PendingAccount(String lastname, String firstname, String email,
            String login, Role role, User createdBy, RequestType requestType) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.login = login;
        this.role = role;
        this.createdBy = createdBy;
        this.requestType = requestType;
        this.createdAt = LocalDateTime.now();
    }

    // ===== GETTERS ET SETTERS =====

    /**
     * @return Identifiant unique de la demande
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id Identifiant unique à définir
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return Nom de famille de l'utilisateur
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * @param lastname Nom de famille à définir
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * @return Prénom de l'utilisateur
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname Prénom à définir
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return Adresse email de l'utilisateur
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email Adresse email à définir
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Identifiant de connexion de l'utilisateur
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login Identifiant de connexion à définir
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return Rôle de l'utilisateur
     */
    public Role getRole() {
        return role;
    }

    /**
     * @param role Rôle à définir
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * @return Utilisateur créateur de la demande
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy Utilisateur créateur à définir
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return Date et heure de création de la demande
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @return Type de demande
     */
    public RequestType getRequestType() {
        return requestType;
    }

    /**
     * @param requestType Type de demande à définir
     */
    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
