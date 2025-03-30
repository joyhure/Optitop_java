package com.optitop.optitop_api.repository;

import com.optitop.optitop_api.model.Invoices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoicesRepository extends JpaRepository<Invoices, Long> {

    // Méthode pour trouver une facture par référence et date
    List<Invoices> findByInvoiceRefAndDate(String invoiceRef, LocalDate date);

    // Méthode pour supprimer les factures dans une période
    @Modifying
    @Query("DELETE FROM Invoices i WHERE i.date BETWEEN :startDate AND :endDate")
    void deleteByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}