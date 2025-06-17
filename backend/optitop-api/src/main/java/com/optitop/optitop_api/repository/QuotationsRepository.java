package com.optitop.optitop_api.repository;

// ===== IMPORTS MODÈLES =====
import com.optitop.optitop_api.model.Quotations;

// ===== IMPORTS SPRING DATA JPA =====
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// ===== IMPORTS UTILITAIRES =====
import java.time.LocalDate;
import java.util.List;

/**
 * Repository pour la gestion des devis optiques
 * 
 * Fournit l'accès aux données des devis avec les fonctionnalités :
 * - Récupération des devis non validés avec filtrage par vendeur
 * - Calculs statistiques globaux et par vendeur
 * - Taux de concrétisation et métriques de performance
 * - Requêtes spécialisées pour la gestion du pipeline commercial
 * - Opérations CRUD sur les devis
 * 
 * Utilisé pour alimenter les tableaux de bord managers et collaborateurs.
 */
@Repository
public interface QuotationsRepository extends JpaRepository<Quotations, Long> {

        // ===== OPÉRATIONS CRUD DE BASE =====

        /**
         * Recherche des devis par client et date
         * 
         * @param clientId Identifiant du client
         * @param date     Date de création du devis
         * @return Liste des devis correspondants
         */
        List<Quotations> findByClientIdAndDate(String clientId, LocalDate date);

        // ===== REQUÊTES DEVIS NON VALIDÉS =====

        /**
         * Récupère tous les devis non validés sur une période
         * 
         * Filtre les devis en attente de traitement (isValidated = false ou null)
         * Utilisé pour l'affichage manager de tous les devis à traiter
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste des devis non validés triés chronologiquement
         */
        @Query("SELECT q FROM Quotations q WHERE q.date BETWEEN :startDate AND :endDate " +
                        "AND (q.isValidated IS NULL OR q.isValidated = false) " +
                        "ORDER BY q.date DESC")
        List<Quotations> findUnvalidatedByDateBetween(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Récupère les devis non validés d'un vendeur spécifique
         * 
         * Filtre par vendeur pour l'affichage collaborateur
         * Permet aux commerciaux de voir uniquement leurs devis en attente
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @param sellerRef Référence du vendeur
         * @return Liste des devis non validés du vendeur
         */
        @Query("SELECT q FROM Quotations q " +
                        "WHERE q.date BETWEEN :startDate AND :endDate " +
                        "AND q.seller.sellerRef = :sellerRef " +
                        "AND (q.isValidated = false OR q.isValidated IS NULL) " +
                        "ORDER BY q.date DESC")
        List<Quotations> findUnvalidatedByDateBetweenAndSellerRef(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("sellerRef") String sellerRef);

        // ===== STATISTIQUES GLOBALES =====

        /**
         * Compte le nombre total de devis sur une période
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Nombre total de devis créés
         */
        @Query("SELECT COUNT(q) FROM Quotations q WHERE q.date BETWEEN :startDate AND :endDate")
        Long countQuotationsBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        /**
         * Compte le nombre de devis validés (concrétisés) sur une période
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Nombre de devis concrétisés en vente
         */
        @Query("SELECT COUNT(q) FROM Quotations q WHERE q.date BETWEEN :startDate AND :endDate AND q.isValidated = true")
        Long countValidatedQuotationsBetween(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Compte le nombre de devis non validés sur une période
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Nombre de devis en attente de traitement
         */
        @Query("SELECT COUNT(q) FROM Quotations q WHERE q.date BETWEEN :startDate AND :endDate AND (q.isValidated IS NULL OR q.isValidated = false)")
        Long countUnvalidatedQuotationsBetween(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // ===== STATISTIQUES PAR VENDEUR =====

        /**
         * Liste tous les vendeurs ayant créé des devis sur une période
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste des références vendeurs triée alphabétiquement
         */
        @Query("SELECT DISTINCT q.seller.sellerRef FROM Quotations q " +
                        "WHERE q.date BETWEEN :startDate AND :endDate " +
                        "ORDER BY q.seller.sellerRef")
        List<String> findDistinctSellersBetweenDates(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Calcule les statistiques de devis par vendeur
         * 
         * Retourne pour chaque vendeur :
         * - Son nombre total de devis
         * - Son nombre de devis non validés
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste [sellerRef, total, unvalidated] pour chaque vendeur
         */
        @Query("SELECT q.seller.sellerRef as sellerRef, " +
                        "COUNT(q) as total, " +
                        "COUNT(CASE WHEN q.isValidated = false OR q.isValidated IS NULL THEN 1 END) as unvalidated " +
                        "FROM Quotations q " +
                        "WHERE q.date BETWEEN :startDate AND :endDate " +
                        "GROUP BY q.seller.sellerRef " +
                        "ORDER BY q.seller.sellerRef")
        List<Object[]> getSellerStats(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // ===== CALCULS DE PERFORMANCE =====

        /**
         * Calcule le taux de concrétisation sur une période
         * 
         * Formule : (devis validés / total devis) * 100
         * Utilisé pour les comparaisons N-1 et les indicateurs de performance
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Taux de concrétisation en pourcentage (0-100)
         */
        @Query("SELECT CAST(COUNT(CASE WHEN q.isValidated = true THEN 1 END) AS float) / CAST(COUNT(q) AS float) * 100 "
                        +
                        "FROM Quotations q WHERE q.date BETWEEN :startDate AND :endDate")
        Double getPreviousConcretizationRate(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // ===== REQUÊTES TECHNIQUES =====

        /**
         * Récupère les valeurs possibles de l'enum action depuis la base de données
         * 
         * Requête native MySQL pour extraire les valeurs de l'enum directement
         * depuis la définition de colonne. Utilisé pour la validation côté client.
         * 
         * @return String contenant les valeurs de l'enum action
         */
        @Query(value = "SELECT SUBSTRING(COLUMN_TYPE, 6, LENGTH(COLUMN_TYPE) - 6) " +
                        "FROM information_schema.COLUMNS " +
                        "WHERE TABLE_SCHEMA = DATABASE() " +
                        "AND TABLE_NAME = 'quotations' " +
                        "AND COLUMN_NAME = 'action'", nativeQuery = true)
        String getActionEnumValues();
}