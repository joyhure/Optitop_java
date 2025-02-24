package com.optitop.optitop_api.repository;

import com.optitop.optitop_api.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    @Modifying
    @Query("DELETE FROM Invoice i WHERE i.date BETWEEN :startDate AND :endDate")
    void deleteByDateBetween(LocalDate startDate, LocalDate endDate);

    Invoice findByInvoiceRef(String invoiceRef);

    Optional<Invoice> findTopByOrderByCreatedAtDesc();
}