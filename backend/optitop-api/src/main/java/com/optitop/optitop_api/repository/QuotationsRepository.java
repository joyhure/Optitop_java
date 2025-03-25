package com.optitop.optitop_api.repository;

import com.optitop.optitop_api.model.Quotations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface QuotationsRepository extends JpaRepository<Quotations, Long> {
        List<Quotations> findByClientIdAndDate(String clientId, LocalDate date);

        /**
         * Trouve tous les devis non validés entre deux dates
         * 
         * @param startDate date de début
         * @param endDate   date de fin
         * @return liste des devis non validés
         */
        @Query("SELECT q FROM Quotations q WHERE q.date BETWEEN :startDate AND :endDate " +
                        "AND (q.isValidated IS NULL OR q.isValidated = false)")
        List<Quotations> findUnvalidatedByDateBetween(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT q FROM Quotations q " +
                        "WHERE q.date BETWEEN :startDate AND :endDate " +
                        "AND q.seller.sellerRef = :sellerRef " +
                        "AND (q.isValidated = false OR q.isValidated IS NULL)")
        List<Quotations> findUnvalidatedByDateBetweenAndSellerRef(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("sellerRef") String sellerRef);

        /**
         * Récupère toutes les valeurs possibles de l'enum action
         */
        @Query(value = "SELECT SUBSTRING(COLUMN_TYPE, 6, LENGTH(COLUMN_TYPE) - 6) " +
                        "FROM information_schema.COLUMNS " +
                        "WHERE TABLE_SCHEMA = DATABASE() " +
                        "AND TABLE_NAME = 'quotations' " +
                        "AND COLUMN_NAME = 'action'", nativeQuery = true)
        String getActionEnumValues();

        @Query("SELECT COUNT(q) FROM Quotations q WHERE q.date BETWEEN :startDate AND :endDate")
        Long countQuotationsBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        @Query("SELECT COUNT(q) FROM Quotations q WHERE q.date BETWEEN :startDate AND :endDate AND q.isValidated = true")
        Long countValidatedQuotationsBetween(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT COUNT(q) FROM Quotations q WHERE q.date BETWEEN :startDate AND :endDate AND (q.isValidated IS NULL OR q.isValidated = false)")
        Long countUnvalidatedQuotationsBetween(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT DISTINCT q.seller.sellerRef FROM Quotations q " +
                        "WHERE q.date BETWEEN :startDate AND :endDate " +
                        "ORDER BY q.seller.sellerRef")
        List<String> findDistinctSellersBetweenDates(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

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

        @Query("SELECT CAST(COUNT(CASE WHEN q.isValidated = true THEN 1 END) AS float) / CAST(COUNT(q) AS float) * 100 "
                        +
                        "FROM Quotations q WHERE q.date BETWEEN :startDate AND :endDate")
        Double getPreviousConcretizationRate(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}