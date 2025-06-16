package com.optitop.optitop_api.repository;

// ===== IMPORTS MODÈLES =====
import com.optitop.optitop_api.model.Invoices;
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

/**
 * Repository pour la gestion des factures globales
 * 
 * Fournit l'accès aux données de facturation niveau entête :
 * - Calculs des montants totaux par vendeur et période
 * - Statistiques globales du magasin (CA total, nombre de factures)
 * - Analyses temporelles (évolution mensuelle, comparaisons N-1)
 * - Répartition des performances par vendeur
 * - Opérations CRUD sur les factures
 */
@Repository
public interface InvoicesRepository extends JpaRepository<Invoices, Long> {

        // ===== OPÉRATIONS CRUD DE BASE =====

        /**
         * Recherche des factures par référence et date
         * 
         * @param invoiceRef Référence de la facture
         * @param date       Date de facturation
         * @return Liste des factures correspondantes
         */
        List<Invoices> findByInvoiceRefAndDate(String invoiceRef, LocalDate date);

        /**
         * Supprime toutes les factures dans une période donnée
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         */
        @Modifying
        @Query("DELETE FROM Invoices i WHERE i.date BETWEEN :startDate AND :endDate")
        void deleteByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        /**
         * Récupère les factures avec leurs lignes détaillées sur une période
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste des lignes de factures associées
         */
        List<InvoicesLines> findByDateBetween(LocalDate startDate, LocalDate endDate);

        // ===== CALCULS MONTANTS TOTAUX PAR VENDEUR =====

        /**
         * Calcule le montant total des ventes optiques par vendeur
         * 
         * Seules les factures optiques (isOptical = true) sont prises en compte
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste [sellerRef, totalAmount] triée par référence vendeur
         */
        @Query("SELECT i.seller.sellerRef, " +
                        "SUM(i.totalInvoice) as totalAmount " +
                        "FROM Invoices i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.isOptical = true " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculateTotalAmounts(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Calcule le nombre de factures nettes par vendeur (factures - avoirs)
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste [sellerRef, invoiceCount] avec comptage net
         */
        @Query("SELECT i.seller.sellerRef as sellerRef, " +
                        "(SELECT COUNT(*) FROM Invoices f " +
                        "WHERE f.seller.sellerRef = i.seller.sellerRef " +
                        "AND f.date BETWEEN :startDate AND :endDate " +
                        "AND f.isOptical = true " +
                        "AND f.status = 'facture') - " +
                        "(SELECT COUNT(*) FROM Invoices a " +
                        "WHERE a.seller.sellerRef = i.seller.sellerRef " +
                        "AND a.date BETWEEN :startDate AND :endDate " +
                        "AND a.isOptical = true " +
                        "AND a.status = 'avoir') as invoiceCount " +
                        "FROM Invoices i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculateInvoiceCounts(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // ===== STATISTIQUES GLOBALES MAGASIN =====

        /**
         * Calcule le chiffre d'affaires total du magasin sur une période
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Montant total des ventes optiques
         */
        @Query("SELECT SUM(i.totalInvoice) FROM Invoices i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.isOptical = true")
        Double calculateTotalAmount(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Calcule le nombre total de factures nettes du magasin (factures - avoirs)
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Nombre net de factures émises
         */
        @Query("SELECT COUNT(CASE WHEN i.status = 'facture' THEN 1 END) - " +
                        "COUNT(CASE WHEN i.status = 'avoir' THEN 1 END) " +
                        "FROM Invoices i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.isOptical = true")
        Long calculateTotalInvoiceCount(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // ===== ANALYSES TEMPORELLES =====

        /**
         * Calcule le chiffre d'affaires mensuel pour une année donnée
         * 
         * @param year Année d'analyse (ex: 2024)
         * @return Liste [month, monthlyRevenue] pour chaque mois de l'année
         */
        @Query("SELECT MONTH(i.date) as month, " +
                        "SUM(totalInvoice) as monthlyRevenue " +
                        "FROM Invoices i " +
                        "WHERE FUNCTION('YEAR', i.date) = :year " +
                        "GROUP BY MONTH(i.date) " +
                        "ORDER BY month")
        List<Object[]> calculateMonthlyRevenue(@Param("year") int year);

        /**
         * Calcule le montant total pour une période donnée (utilisé pour comparaisons
         * N-1)
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Montant total ou 0 si aucune donnée
         */
        @Query("SELECT COALESCE(SUM(i.totalInvoice), 0) FROM Invoices i WHERE i.date BETWEEN :startDate AND :endDate")
        Double getCurrentPeriodTotal(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        // ===== ANALYSES PAR VENDEUR =====

        /**
         * Calcule les statistiques de chiffre d'affaires et pourcentages par vendeur
         * 
         * Retourne pour chaque vendeur :
         * - Son CA total sur la période
         * - Son pourcentage de contribution au CA total magasin
         * 
         * @param startDate Date de début de la période
         * @param endDate   Date de fin de la période
         * @return Liste [sellerRef, sellerAmount, percentage] triée par CA décroissant
         */
        @Query("SELECT DISTINCT i.seller.sellerRef, " +
                        "SUM(i.totalInvoice) as sellerAmount, " +
                        "(SUM(i.totalInvoice) / " +
                        "(SELECT COALESCE(SUM(s.totalInvoice), 0) " +
                        "FROM Invoices s " +
                        "WHERE s.date BETWEEN :startDate AND :endDate)) * 100 as percentage " +
                        "FROM Invoices i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY sellerAmount DESC")
        List<Object[]> getSellerRevenueStats(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}