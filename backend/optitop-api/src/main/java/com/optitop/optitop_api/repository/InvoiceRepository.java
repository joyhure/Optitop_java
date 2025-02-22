package com.optitop.optitop_api.repository;

import com.optitop.optitop_api.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Invoice findByInvoiceRef(String invoiceRef);

    @Modifying
    @Query("DELETE FROM Invoice i WHERE i.invoiceRef NOT IN :invoiceRefs AND i.date BETWEEN :startDate AND :endDate")
    void deleteByInvoiceRefNotInAndDateBetween(Set<String> invoiceRefs, LocalDate startDate, LocalDate endDate);
}