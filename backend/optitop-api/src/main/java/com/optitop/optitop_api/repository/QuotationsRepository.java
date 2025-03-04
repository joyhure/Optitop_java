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
                        "AND (q.status IS NULL OR q.status != 'Validé')")
        List<Quotations> findUnvalidatedByDateBetween(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

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

        @Query("SELECT COUNT(q) FROM Quotations q WHERE q.date BETWEEN :startDate AND :endDate AND q.status = 'Validé'")
        Long countValidatedQuotationsBetween(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT COUNT(q) FROM Quotations q WHERE q.date BETWEEN :startDate AND :endDate AND (q.status IS NULL OR q.status != 'Validé')")
        Long countUnvalidatedQuotationsBetween(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}