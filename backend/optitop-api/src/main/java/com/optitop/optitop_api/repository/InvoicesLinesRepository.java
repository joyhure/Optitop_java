package com.optitop.optitop_api.repository;

// ===== IMPORTS MODÈLES =====
import com.optitop.optitop_api.model.InvoicesLines;

// ===== IMPORTS SPRING DATA JPA =====
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// ===== IMPORTS UTILITAIRES =====
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des lignes de factures
 * 
 * Fournit l'accès aux données des détails de facturation :
 * - Calculs des paniers moyens par type de produit (P1 MON, P1 VER, P2)
 * - Statistiques des montures primées (seuil 200€)
 * - Analyses temporelles et par vendeur
 * - Opérations CRUD sur les lignes de factures
 */
@Repository
public interface InvoicesLinesRepository extends JpaRepository<InvoicesLines, Long> {

        // ===== OPÉRATIONS CRUD DE BASE =====

        /**
         * Supprime toutes les lignes de factures dans une période donnée
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         */
        @Modifying
        @Query("DELETE FROM InvoicesLines i WHERE i.date BETWEEN :startDate AND :endDate")
        void deleteByDateBetween(LocalDate startDate, LocalDate endDate);

        /**
         * Recherche une ligne de facture par référence de facture
         * 
         * @param invoiceRef Référence de la facture
         * @return Ligne de facture correspondante ou null
         */
        InvoicesLines findByInvoiceRef(String invoiceRef);

        /**
         * Récupère la dernière ligne de facture créée
         * 
         * @return Optional contenant la dernière ligne créée
         */
        Optional<InvoicesLines> findTopByOrderByCreatedAtDesc();

        // ===== REQUÊTES VENDEURS =====

        /**
         * Liste tous les vendeurs distincts ayant des ventes sur une période
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste des références vendeurs triée alphabétiquement
         */
        @Query("SELECT DISTINCT i.seller.sellerRef FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "ORDER BY i.seller.sellerRef")
        List<String> findDistinctSellersBetweenDates(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // ===== STATISTIQUES P1 MONTURES =====

        /**
         * Calcule le montant total des ventes P1 Montures par vendeur
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste [sellerRef, totalAmount] pour les montures P1
         */
        @Query("SELECT i.seller.sellerRef, " +
                        "SUM(i.totalTtc) as totalAmount " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'MON' " +
                        "AND i.pair = 1 " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculateP1FramesTotalAmounts(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Calcule le nombre de montures P1 vendues par vendeur (factures - avoirs)
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste [sellerRef, count] pour les montures P1
         */
        @Query("SELECT i.seller.sellerRef, " +
                        "COUNT(CASE WHEN i.status = 'facture' THEN 1 END) - " +
                        "COUNT(CASE WHEN i.status = 'avoir' THEN 1 END) " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'MON' " +
                        "AND i.pair = 1 " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculateP1FramesCounts(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // ===== STATISTIQUES P1 VERRES =====

        /**
         * Calcule le nombre de verres P1 vendus par vendeur
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste [sellerRef, count] pour les verres P1
         */
        @Query("SELECT i.seller.sellerRef, " +
                        "SUM(CASE WHEN i.pair = 1 THEN i.quantity ELSE 0 END) as count " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'VER' " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculateP1LensesCounts(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Calcule le montant total des ventes P1 Verres par vendeur
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste [sellerRef, totalAmount] pour les verres P1
         */
        @Query("SELECT i.seller.sellerRef as sellerRef, " +
                        "SUM(CASE WHEN i.pair = 1 THEN i.totalTtc ELSE 0 END) as totalAmountP1VER " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'VER' " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculateP1LensesTotalAmounts(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // ===== STATISTIQUES P2 =====

        /**
         * Calcule le montant total des ventes P2 (équipements secondaires) par vendeur
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste [sellerRef, totalAmount] pour les équipements P2
         */
        @Query("SELECT i.seller.sellerRef as sellerRef, " +
                        "SUM(CASE WHEN i.pair = 2 THEN i.totalTtc ELSE 0 END) as totalAmountP2 " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculateP2TotalAmounts(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Calcule le nombre d'équipements P2 vendus par vendeur
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste [sellerRef, count] pour les équipements P2
         */
        @Query("SELECT i.seller.sellerRef, " +
                        "SUM(CASE WHEN i.pair = 2 THEN i.quantity ELSE 0 END) as count " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculateP2Counts(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // ===== STATISTIQUES MONTURES PRIMÉES =====

        /**
         * Calcule le nombre de montures primées par vendeur (seuil 200€)
         * 
         * Une monture est considérée comme primée si son montant TTC ≥ 200€
         * Les avoirs (montant négatif ≤ -200€) sont décomptés
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste [sellerRef, premiumFramesCount] pour les montures primées
         */
        @Query("SELECT i.seller.sellerRef as sellerRef, " +
                        "SUM(CASE " +
                        "WHEN i.totalTtc >= 200 THEN 1 " +
                        "WHEN i.totalTtc <= -200 THEN -1 " +
                        "ELSE 0 " +
                        "END) as premiumFrames " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'MON' " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculatePremiumFramesCount(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // ===== DONNÉES RÉFÉRENTIELLES =====

        /**
         * Liste toutes les années distinctes présentes dans les données
         * 
         * @return Liste des années d'activité triée par ordre décroissant
         */
        @Query("SELECT DISTINCT YEAR(i.date) as year FROM InvoicesLines i ORDER BY year DESC")
        List<Integer> findDistinctYears();

        // ===== REQUÊTES OPTIMISÉES AVEC JOINTURES =====

        /**
         * Récupère les lignes de factures avec leurs vendeurs sur une période
         * 
         * Utilise un LEFT JOIN FETCH pour éviter le problème N+1 des requêtes
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste des lignes avec vendeurs préchargés
         */
        @Query("SELECT i FROM InvoicesLines i LEFT JOIN FETCH i.seller WHERE i.date BETWEEN :startDate AND :endDate")
        List<InvoicesLines> findByDateBetweenWithSeller(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}