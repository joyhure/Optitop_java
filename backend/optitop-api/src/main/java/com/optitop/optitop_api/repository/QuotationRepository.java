package com.optitop.optitop_api.repository;

import com.optitop.optitop_api.model.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Integer> {
    @Modifying
    @Query("DELETE FROM Quotation q WHERE q.date BETWEEN :startDate AND :endDate")
    void deleteByDateBetween(LocalDate startDate, LocalDate endDate);

    Quotation findByQuotationRef(String quotationRef);

    Optional<Quotation> findTopByOrderByCreatedAtDesc();
}