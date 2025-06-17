package com.optitop.optitop_api.repository;

// ===== IMPORTS MODÈLES =====
import com.optitop.optitop_api.model.QuotationsLines;

// ===== IMPORTS SPRING DATA JPA =====
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// ===== IMPORTS UTILITAIRES =====
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des lignes de devis optiques
 * 
 * Fournit l'accès aux données des lignes de devis avec les fonctionnalités :
 * - Recherche par référence de devis et période
 * - Filtrage par famille de produits pour analyses ciblées
 * - Récupération avec jointure vendeur pour optimiser les performances
 * - Suppression par période pour la maintenance des données
 * - Requêtes spécialisées pour les statistiques produits
 * 
 * Utilisé pour :
 * - L'analyse des ventes par famille de produits
 * - Le calcul des commissions par vendeur
 * - Les rapports détaillés de composition des devis
 * - La maintenance et purge des données historiques
 */
@Repository
public interface QuotationsLinesRepository extends JpaRepository<QuotationsLines, Long> {

        // ===== RECHERCHES PAR RÉFÉRENCE =====

        /**
         * Recherche une ligne de devis par référence de devis
         * 
         * Utilisée pour retrouver les détails d'un devis spécifique.
         * Attention : peut retourner plusieurs lignes pour un même devis.
         * 
         * @param quotationRef Référence du devis
         * @return Première ligne trouvée pour ce devis
         */
        QuotationsLines findByQuotationRef(String quotationRef);

        // ===== REQUÊTES CHRONOLOGIQUES =====

        /**
         * Récupère la ligne de devis la plus récente
         * 
         * Utilisée pour obtenir la dernière activité de devis du système.
         * Pratique pour les vérifications d'import et la synchronisation.
         * 
         * @return Optional contenant la ligne la plus récente si elle existe
         */
        Optional<QuotationsLines> findTopByOrderByCreatedAtDesc();

        // ===== ANALYSES PAR FAMILLE DE PRODUITS =====

        /**
         * Recherche les lignes de devis par période et familles de produits
         * 
         * Filtre les données pour des analyses ciblées par catégorie de produits.
         * Utilisée pour les rapports de performance par famille.
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @param families  Liste des familles de produits à inclure
         * @return Liste des lignes correspondant aux critères
         */
        @Query("SELECT q FROM QuotationsLines q " +
                        "WHERE q.date BETWEEN :startDate AND :endDate " +
                        "AND q.family IN :families " +
                        "ORDER BY q.date DESC")
        List<QuotationsLines> findByDateBetweenAndFamilyIn(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("families") List<String> families);

        /**
         * Recherche les lignes de devis avec vendeur associé (optimisée)
         * 
         * Utilise LEFT JOIN FETCH pour éviter le problème N+1 lors de l'accès
         * aux informations vendeur. Optimise les performances pour les rapports
         * nécessitant les données vendeur.
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @param families  Liste des familles de produits à inclure
         * @return Liste des lignes avec vendeurs chargés en une seule requête
         */
        @Query("SELECT q FROM QuotationsLines q " +
                        "LEFT JOIN FETCH q.seller " +
                        "WHERE q.date BETWEEN :startDate AND :endDate " +
                        "AND q.family IN :families " +
                        "ORDER BY q.date DESC")
        List<QuotationsLines> findByDateBetweenAndFamilyInFetchSeller(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("families") List<String> families);

        // ===== MAINTENANCE DES DONNÉES =====

        /**
         * Supprime les lignes de devis dans une période donnée
         * 
         * Utilisée pour la maintenance et la purge des données historiques.
         * Attention : suppression définitive, à utiliser avec précaution.
         * 
         * @param startDate Date de début de la période à supprimer
         * @param endDate   Date de fin de la période à supprimer
         */
        void deleteByDateBetween(LocalDate startDate, LocalDate endDate);
}