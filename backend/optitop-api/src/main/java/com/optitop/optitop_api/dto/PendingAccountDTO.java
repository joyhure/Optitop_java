package com.optitop.optitop_api.dto;

// ===== IMPORTS VALIDATION =====
import jakarta.validation.constraints.NotBlank;

// ===== IMPORTS MODÈLES =====
import com.optitop.optitop_api.model.User.Role;

// ===== IMPORTS UTILITAIRES =====
import java.util.Objects;

/**
 * DTO pour les demandes de comptes utilisateurs
 * 
 * Classe de transfert de données pour transporter les informations
 * des demandes de création, modification ou suppression de comptes
 * entre le frontend et le backend.
 * 
 * Contient toutes les informations nécessaires pour traiter
 * une demande d'action sur un compte utilisateur dans le système Optitop.
 */
public class PendingAccountDTO {

        // ===== PROPRIÉTÉS =====

        /**
         * Nom de famille de l'utilisateur
         * Optionnel pour les demandes de suppression
         */
        private String lastname;

        /**
         * Prénom de l'utilisateur
         * Optionnel pour les demandes de suppression
         */
        private String firstname;

        /**
         * Adresse email de l'utilisateur
         * Optionnel pour les demandes de suppression
         */
        private String email;

        /**
         * Identifiant de connexion unique de l'utilisateur
         * Obligatoire pour toutes les demandes
         */
        @NotBlank(message = "L'identifiant est obligatoire")
        private String login;

        /**
         * Rôle de l'utilisateur dans le système
         * Optionnel pour les demandes de suppression
         */
        private Role role;

        /**
         * Type de demande à effectuer
         * Valeurs possibles : "ajout", "modification", "suppression"
         */
        @NotBlank(message = "Le type de demande est obligatoire")
        private String requestType;

        // ===== CONSTRUCTEURS =====

        /**
         * Constructeur par défaut
         * Requis pour la désérialisation JSON par Spring Boot
         */
        public PendingAccountDTO() {
        }

        /**
         * Constructeur avec tous les paramètres
         * 
         * @param lastname    Nom de famille
         * @param firstname   Prénom
         * @param email       Adresse email
         * @param login       Identifiant de connexion
         * @param role        Rôle utilisateur
         * @param requestType Type de demande
         */
        public PendingAccountDTO(String lastname, String firstname, String email,
                        String login, Role role, String requestType) {
                this.lastname = lastname;
                this.firstname = firstname;
                this.email = email;
                this.login = login;
                this.role = role;
                this.requestType = requestType;
        }

        // ===== GETTERS =====

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

        // ===== SETTERS =====

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

        // ===== MÉTHODES UTILITAIRES =====

        /**
         * Vérifie l'égalité de deux objets PendingAccountDTO
         * 
         * @param obj Objet à comparer
         * @return true si les objets sont égaux, false sinon
         */
        @Override
        public boolean equals(Object obj) {
                if (this == obj)
                        return true;
                if (obj == null || getClass() != obj.getClass())
                        return false;

                PendingAccountDTO that = (PendingAccountDTO) obj;
                return Objects.equals(lastname, that.lastname) &&
                                Objects.equals(firstname, that.firstname) &&
                                Objects.equals(email, that.email) &&
                                Objects.equals(login, that.login) &&
                                Objects.equals(role, that.role) &&
                                Objects.equals(requestType, that.requestType);
        }

        /**
         * Calcule le hash code de l'objet
         * 
         * @return Hash code basé sur toutes les propriétés
         */
        @Override
        public int hashCode() {
                return Objects.hash(lastname, firstname, email, login, role, requestType);
        }

        /**
         * Représentation textuelle de l'objet
         * 
         * @return String décrivant l'objet
         */
        @Override
        public String toString() {
                return "PendingAccountDTO{" +
                                "lastname='" + lastname + '\'' +
                                ", firstname='" + firstname + '\'' +
                                ", email='" + email + '\'' +
                                ", login='" + login + '\'' +
                                ", role=" + role +
                                ", requestType='" + requestType + '\'' +
                                '}';
        }
}