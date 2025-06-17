package com.optitop.optitop_api.repository;

// ===== IMPORTS MODÈLES =====
import com.optitop.optitop_api.model.Seller;

// ===== IMPORTS SPRING DATA JPA =====
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// ===== IMPORTS UTILITAIRES =====
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des vendeurs/commerciaux
 * 
 * Fournit l'accès aux données des vendeurs avec les fonctionnalités :
 * - Recherche par référence vendeur (clé métier)
 * - Vérification d'existence pour éviter les doublons
 * - Association avec les comptes utilisateurs
 * - Requêtes pour la gestion des droits d'accès
 * - Opérations CRUD sur les vendeurs
 * 
 * Utilisé pour :
 * - L'affectation des ventes et devis aux commerciaux
 * - La gestion des sessions et droits collaborateurs
 * - Le calcul des statistiques de performance
 * - La validation des références vendeurs
 */
@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer> {

    // ===== OPÉRATIONS DE VÉRIFICATION =====

    /**
     * Vérifie l'existence d'un vendeur par sa référence métier
     * 
     * Utilisé pour éviter les doublons lors de la création
     * et valider les références dans les imports de données
     * 
     * @param sellerRef Référence vendeur à vérifier
     * @return true si le vendeur existe, false sinon
     */
    boolean existsBySellerRef(String sellerRef);

    // ===== RECHERCHES PAR RÉFÉRENCE =====

    /**
     * Recherche un vendeur par sa référence métier
     * 
     * Méthode principale pour récupérer un vendeur par sa clé fonctionnelle.
     * Utilisée dans les affectations de ventes et la gestion des droits.
     * 
     * @param sellerRef Référence unique du vendeur
     * @return Optional contenant le vendeur si trouvé
     */
    Optional<Seller> findBySellerRef(String sellerRef);

    /**
     * Récupère tous les vendeurs triés par référence
     * 
     * Fournit la liste complète des vendeurs pour les interfaces
     * de sélection et les rapports managériaux
     * 
     * @return Liste des vendeurs triée alphabétiquement par référence
     */
    @Query("SELECT s FROM Seller s ORDER BY s.sellerRef")
    List<Seller> findAllOrderBySellerRef();

    // ===== GESTION DES UTILISATEURS ASSOCIÉS =====

    /**
     * Recherche un vendeur par l'identifiant de son utilisateur associé
     * 
     * Permet de retrouver le vendeur correspondant à un utilisateur connecté.
     * Utilisé pour filtrer les données selon le rôle collaborateur.
     * 
     * @param userId Identifiant de l'utilisateur système
     * @return Optional contenant le vendeur associé si trouvé
     */
    @Query("SELECT s FROM Seller s WHERE s.user.id = :userId")
    Optional<Seller> findByUserId(@Param("userId") Integer userId);

    /**
     * Récupère tous les vendeurs sans compte utilisateur associé
     * 
     * Identifie les vendeurs qui n'ont pas encore de compte système.
     * Utilisé pour la gestion des comptes et l'attribution des droits.
     * 
     * @return Liste des vendeurs sans utilisateur associé
     */
    @Query("SELECT s FROM Seller s WHERE s.user IS NULL ORDER BY s.sellerRef")
    List<Seller> findAllByUserIsNull();
}
