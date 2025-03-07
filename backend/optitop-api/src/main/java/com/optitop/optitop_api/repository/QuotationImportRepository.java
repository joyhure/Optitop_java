package com.optitop.optitop_api.repository;

import com.optitop.optitop_api.model.QuotationImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationImportRepository extends JpaRepository<QuotationImport, Long> {
    QuotationImport findByQuotationRef(String quotationRef);

    Optional<QuotationImport> findTopByOrderByCreatedAtDesc();

    @Query("SELECT q FROM QuotationImport q WHERE q.date BETWEEN :startDate AND :endDate AND q.family IN :families")
    List<QuotationImport> findByDateBetweenAndFamilyIn(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("families") List<String> families);

    void deleteByDateBetween(LocalDate startDate, LocalDate endDate);
}