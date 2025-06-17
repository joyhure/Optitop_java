package com.optitop.optitop_api.dto;

// ===== IMPORTS MODÈLES =====
import com.optitop.optitop_api.model.User.Role;

// ===== IMPORTS UTILITAIRES =====
import java.time.LocalDateTime;

/**
 * DTO d'affichage pour les demandes de comptes utilisateurs
 * 
 * Classe de transfert de données optimisée pour l'affichage
 * des demandes de comptes dans l'interface d'administration.
 * 
 * Contient toutes les informations nécessaires pour :
 * - Afficher la liste des demandes en attente
 * - Identifier la demande (ID, dates, créateur)
 * - Présenter les détails utilisateur de manière lisible
 * - Faciliter les actions d'approbation/rejet
 * 
 * Différence avec PendingAccountDTO :
 * - PendingAccountDTO : Pour créer des demandes
 * - PendingAccountDisplayDTO : Pour afficher des demandes existantes
 */
public class PendingAccountDisplayDTO {

        // ===== PROPRIÉTÉS D'IDENTIFICATION =====

        /**
         * Identifiant unique de la demande
         * Utilisé pour les actions de validation/rejet
         */
        private Integer id;

        /**
         * Date et heure de création de la demande
         * Permet le tri chronologique des demandes
         */
        private LocalDateTime createdAt;

        /**
         * Login de l'utilisateur qui a créé la demande
         * Pour traçabilité et identification du demandeur
         */
        private String createdByLogin;

        // ===== PROPRIÉTÉS UTILISATEUR =====

        /**
         * Nom de famille de l'utilisateur concerné
         * Peut être null pour les demandes de suppression
         */
        private String lastname;

        /**
         * Prénom de l'utilisateur concerné
         * Peut être null pour les demandes de suppression
         */
        private String firstname;

        /**
         * Adresse email de l'utilisateur concerné
         * Peut être null pour les demandes de suppression
         */
        private String email;

        /**
         * Identifiant de connexion de l'utilisateur concerné
         * Toujours présent, identifie l'utilisateur cible
         */
        private String login;

        /**
         * Rôle demandé pour l'utilisateur
         * Peut être null pour les demandes de suppression
         */
        private Role role;

        // ===== PROPRIÉTÉS DE DEMANDE =====

        /**
         * Type de demande effectuée
         * Valeurs : "ajout", "modification", "suppression"
         */
        private String requestType;

        // ===== CONSTRUCTEURS =====

        /**
         * Constructeur par défaut
         * Requis pour la sérialisation/désérialisation
         */
        public PendingAccountDisplayDTO() {
        }

        /**
         * Constructeur avec tous les paramètres
         * 
         * Utilisé pour créer un DTO d'affichage à partir
         * d'une entité PendingAccount récupérée de la base.
         * 
         * @param id             Identifiant unique de la demande
         * @param lastname       Nom de famille
         * @param firstname      Prénom
         * @param email          Adresse email
         * @param login          Identifiant de connexion
         * @param role           Rôle utilisateur
         * @param requestType    Type de demande
         * @param createdAt      Date de création
         * @param createdByLogin Login du créateur
         */
        public PendingAccountDisplayDTO(Integer id, String lastname, String firstname,
                        String email, String login, Role role,
                        String requestType, LocalDateTime createdAt,
                        String createdByLogin) {
                this.id = id;
                this.lastname = lastname;
                this.firstname = firstname;
                this.email = email;
                this.login = login;
                this.role = role;
                this.requestType = requestType;
                this.createdAt = createdAt;
                this.createdByLogin = createdByLogin;
        }

        // ===== GETTERS =====

        /**
         * @return Identifiant unique de la demande
         */
        public Integer getId() {
                return id;
        }

        /**
         * @return Nom de famille de l'utilisateur
         */
        public String getLastname() {
                return lastname;
        }

        /**
         * @return Prénom de l'utilisateur
         */
        public String getFirstname() {
                return firstname;
        }

        /**
         * @return Adresse email de l'utilisateur
         */
        public String getEmail() {
                return email;
        }

        /**
         * @return Identifiant de connexion de l'utilisateur
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
         * @return Type de demande
         */
        public String getRequestType() {
                return requestType;
        }

        /**
         * @return Date et heure de création de la demande
         */
        public LocalDateTime getCreatedAt() {
                return createdAt;
        }

        /**
         * @return Login de l'utilisateur créateur de la demande
         */
        public String getCreatedByLogin() {
                return createdByLogin;
        }

        // ===== SETTERS =====

        /**
         * @param id Identifiant unique à définir
         */
        public void setId(Integer id) {
                this.id = id;
        }

        /**
         * @param lastname Nom de famille à définir
         */
        public void setLastname(String lastname) {
                this.lastname = lastname;
        }

        /**
         * @param firstname Prénom à définir
         */
        public void setFirstname(String firstname) {
                this.firstname = firstname;
        }

        /**
         * @param email Adresse email à définir
         */
        public void setEmail(String email) {
                this.email = email;
        }

        /**
         * @param login Identifiant de connexion à définir
         */
        public void setLogin(String login) {
                this.login = login;
        }

        /**
         * @param role Rôle à définir
         */
        public void setRole(Role role) {
                this.role = role;
        }

        /**
         * @param requestType Type de demande à définir
         */
        public void setRequestType(String requestType) {
                this.requestType = requestType;
        }

        /**
         * @param createdAt Date de création à définir
         */
        public void setCreatedAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
        }

        /**
         * @param createdByLogin Login du créateur à définir
         */
        public void setCreatedByLogin(String createdByLogin) {
                this.createdByLogin = createdByLogin;
        }
}