package com.optitop.optitop_api.repository;

// ===== IMPORTS SPRING DATA =====
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

// ===== IMPORTS MODÈLES =====
import com.optitop.optitop_api.model.PendingAccount;

// ===== IMPORTS UTILITAIRES =====
import java.util.List;

/**
 * Repository pour la gestion des demandes de comptes utilisateurs
 * 
 * Interface de persistance pour les entités PendingAccount.
 * Fournit les opérations CRUD de base via JpaRepository et
 * des méthodes de recherche personnalisées pour les besoins métier.
 */
@Repository
public interface PendingAccountRepository extends JpaRepository<PendingAccount, Integer> {

    // ===== MÉTHODES DE VÉRIFICATION =====

    /**
     * Vérifie si une demande existe déjà pour un login donné
     * 
     * @param login Identifiant de connexion à vérifier
     * @return true si une demande existe pour ce login, false sinon
     */
    boolean existsByLogin(String login);

    // ===== MÉTHODES DE RECHERCHE PAR CRÉATEUR =====

    /**
     * Trouve toutes les demandes créées par un utilisateur spécifique
     * 
     * @param userId ID de l'utilisateur créateur
     * @return Liste des demandes triées par date de création décroissante
     */
    @Query("SELECT pa FROM PendingAccount pa WHERE pa.createdBy.id = :userId ORDER BY pa.createdAt DESC")
    List<PendingAccount> findByCreatedById(Integer userId);

    // ===== MÉTHODES DE RECHERCHE GÉNÉRALE =====

    /**
     * Récupère toutes les demandes en attente avec les informations du créateur
     * 
     * @return Liste de toutes les demandes triées par date de création décroissante
     */
    @Query("SELECT pa FROM PendingAccount pa JOIN FETCH pa.createdBy ORDER BY pa.createdAt DESC")
    List<PendingAccount> findAllByOrderByCreatedAtDesc();

    // ===== MÉTHODES DE PROJECTION =====

    /**
     * Récupère tous les logins des demandes en attente
     * 
     * Retourne uniquement les logins pour optimiser les performances
     * lors de l'affichage des listes déroulantes ou de vérifications.
     * 
     * @return Liste des logins de toutes les demandes en attente
     */
    @Query("SELECT p.login FROM PendingAccount p")
    List<String> findAllLogins();
}