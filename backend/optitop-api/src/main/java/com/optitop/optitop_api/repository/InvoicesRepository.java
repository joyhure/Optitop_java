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

    @Query("SELECT i.seller.sellerRef, " +
            "SUM(i.totalInvoice) as totalAmount " +
            "FROM Invoices i " +
            "WHERE i.date BETWEEN :startDate AND :endDate " +
            "AND i.isOptical = true " +
            "GROUP BY i.seller.sellerRef " +
            "ORDER BY i.seller.sellerRef")
    List<Object[]> calculateTotalAmounts(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

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

    @Query("SELECT SUM(i.totalInvoice) FROM Invoices i " +
            "WHERE i.date BETWEEN :startDate AND :endDate " +
            "AND i.isOptical = true")
    Double calculateTotalAmount(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(CASE WHEN i.status = 'facture' THEN 1 END) - " +
            "COUNT(CASE WHEN i.status = 'avoir' THEN 1 END) " +
            "FROM Invoices i " +
            "WHERE i.date BETWEEN :startDate AND :endDate " +
            "AND i.isOptical = true")
    Long calculateTotalInvoiceCount(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}