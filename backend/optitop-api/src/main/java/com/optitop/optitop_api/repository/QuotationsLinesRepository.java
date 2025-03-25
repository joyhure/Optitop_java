package com.optitop.optitop_api.repository;

import com.optitop.optitop_api.model.QuotationsLines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationsLinesRepository extends JpaRepository<QuotationsLines, Long> {
    QuotationsLines findByQuotationRef(String quotationRef);

    Optional<QuotationsLines> findTopByOrderByCreatedAtDesc();

    @Query("SELECT q FROM QuotationsLines q WHERE q.date BETWEEN :startDate AND :endDate AND q.family IN :families")
    List<QuotationsLines> findByDateBetweenAndFamilyIn(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("families") List<String> families);

    void deleteByDateBetween(LocalDate startDate, LocalDate endDate);
}