package com.optitop.optitop_api.model;

// ===== IMPORTS JPA =====
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

// ===== IMPORTS UTILITAIRES =====
import java.time.LocalDateTime;

/**
 * Entité représentant un vendeur/commercial optique
 * 
 * Contient les informations d'identification d'un vendeur :
 * - Référence unique vendeur (code commercial)
 * - Association optionnelle avec un utilisateur système
 * - Informations de traçabilité (date de création)
 * 
 * Utilisée pour :
 * - L'affectation des ventes et devis aux commerciaux
 * - Le calcul des commissions et statistiques de performance
 * - La gestion des droits d'accès par vendeur (collaborateurs)
 * - Le filtrage des données selon le rôle utilisateur
 */
@Entity
@Table(name = "seller")
public class Seller {

    // ===== PROPRIÉTÉS PRINCIPALES =====

    /**
     * Identifiant technique unique du vendeur
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Référence métier unique du vendeur (code commercial)
     * Utilisée comme clé fonctionnelle dans les relations
     */
    @Column(name = "seller_ref", unique = true, nullable = false, length = 50)
    private String sellerRef;

    // ===== PROPRIÉTÉS TECHNIQUES =====

    /**
     * Date et heure de création de l'enregistrement
     * Initialisée automatiquement à la création
     */
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // ===== RELATIONS =====

    /**
     * Utilisateur système associé au vendeur (optionnel)
     * 
     * Permet de lier un vendeur à un compte utilisateur pour :
     * - L'authentification et les droits d'accès
     * - Le filtrage des données selon le rôle
     * - La gestion des sessions collaborateurs
     * 
     * Relation optionnelle : un vendeur peut exister sans compte utilisateur
     */
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_seller_user"), nullable = true)
    private User user;

    // ===== CONSTRUCTEURS =====

    /**
     * Constructeur par défaut pour JPA
     */
    public Seller() {
    }

    /**
     * Constructeur métier avec référence vendeur
     * 
     * Initialise automatiquement la date de création
     * 
     * @param sellerRef Référence unique du vendeur (obligatoire)
     */
    public Seller(String sellerRef) {
        this.sellerRef = sellerRef;
        this.createdAt = LocalDateTime.now();
    }

    // ===== GETTERS ET SETTERS =====

    /**
     * @return Identifiant technique du vendeur
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id Identifiant technique à définir
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return Référence métier du vendeur
     */
    public String getSellerRef() {
        return sellerRef;
    }

    /**
     * @param sellerRef Référence vendeur à définir
     */
    public void setSellerRef(String sellerRef) {
        this.sellerRef = sellerRef;
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

    /**
     * @return Utilisateur système associé (peut être null)
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user Utilisateur à associer au vendeur
     */
    public void setUser(User user) {
        this.user = user;
    }
}
