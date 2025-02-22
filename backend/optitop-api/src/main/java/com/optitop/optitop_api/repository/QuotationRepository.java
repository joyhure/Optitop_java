package com.optitop.optitop_api.repository;

import com.optitop.optitop_api.model.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    Quotation findByQuotationRef(String quotationRef);

    @Modifying
    @Query("DELETE FROM Quotation q WHERE q.quotationRef NOT IN :quotationRefs AND q.date BETWEEN :startDate AND :endDate")
    void deleteByQuotationRefNotInAndDateBetween(Set<String> quotationRefs, LocalDate startDate, LocalDate endDate);
}