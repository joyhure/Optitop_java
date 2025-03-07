package com.optitop.optitop_api.repository;

import com.optitop.optitop_api.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
        @Modifying
        @Query("DELETE FROM Invoice i WHERE i.date BETWEEN :startDate AND :endDate")
        void deleteByDateBetween(LocalDate startDate, LocalDate endDate);

        Invoice findByInvoiceRef(String invoiceRef);

        Optional<Invoice> findTopByOrderByCreatedAtDesc();

        @Query("SELECT DISTINCT i.sellerRef FROM Invoice i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "ORDER BY i.sellerRef")
        List<String> findDistinctSellersBetweenDates(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.sellerRef, " +
                        "COALESCE(AVG(i.totalInvoice), 0) as avgBasket, " +
                        "(SELECT COUNT(DISTINCT sub.clientId) FROM Invoice sub " +
                        "WHERE sub.sellerRef = i.sellerRef " +
                        "AND sub.date BETWEEN :startDate AND :endDate " +
                        "AND sub.family = 'VER' " +
                        "AND sub.status = 'facture') - " +
                        "(SELECT COUNT(DISTINCT sub.clientId) FROM Invoice sub " +
                        "WHERE sub.sellerRef = i.sellerRef " +
                        "AND sub.date BETWEEN :startDate AND :endDate " +
                        "AND sub.family = 'VER' " +
                        "AND sub.status = 'avoir') as invoiceCount, " +
                        "COALESCE(" +
                        "(SELECT SUM(CASE WHEN sub.status = 'facture' THEN sub.totalTtc " +
                        "            WHEN sub.status = 'avoir' THEN -sub.totalTtc ELSE 0 END) " +
                        " FROM Invoice sub " +
                        " WHERE sub.sellerRef = i.sellerRef " +
                        " AND sub.date BETWEEN :startDate AND :endDate " +
                        " AND sub.family = 'MON' " +
                        " AND sub.pair = 1) / " +
                        "NULLIF((SELECT COUNT(*) FROM Invoice sub " +
                        " WHERE sub.sellerRef = i.sellerRef " +
                        " AND sub.date BETWEEN :startDate AND :endDate " +
                        " AND sub.family = 'MON' " +
                        " AND sub.pair = 1), 0), 0) as avgFramesP1, " +
                        "COALESCE(" +
                        "(SELECT SUM(CASE WHEN sub.status = 'facture' THEN sub.totalTtc " +
                        "            WHEN sub.status = 'avoir' THEN -sub.totalTtc ELSE 0 END) " +
                        " FROM Invoice sub " +
                        " WHERE sub.sellerRef = i.sellerRef " +
                        " AND sub.date BETWEEN :startDate AND :endDate " +
                        " AND sub.family = 'VER' " +
                        " AND sub.pair = 1) / " +
                        "NULLIF((SELECT COUNT(*) FROM Invoice sub " +
                        " WHERE sub.sellerRef = i.sellerRef " +
                        " AND sub.date BETWEEN :startDate AND :endDate " +
                        " AND sub.family = 'VER' " +
                        " AND sub.pair = 1), 0), 0) as avgLensesP1, " +
                        "COALESCE(" +
                        "(SELECT SUM(CASE WHEN sub.status = 'facture' THEN sub.totalTtc " +
                        "            WHEN sub.status = 'avoir' THEN -sub.totalTtc ELSE 0 END) " +
                        " FROM Invoice sub " +
                        " WHERE sub.sellerRef = i.sellerRef " +
                        " AND sub.date BETWEEN :startDate AND :endDate " +
                        " AND sub.pair = 2) / " +
                        "NULLIF((SELECT COUNT(*) FROM Invoice sub " +
                        " WHERE sub.sellerRef = i.sellerRef " +
                        " AND sub.date BETWEEN :startDate AND :endDate " +
                        " AND sub.pair = 2), 0), 0) as avgP2 " +
                        "FROM Invoice i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'VER' " +
                        "GROUP BY i.sellerRef " +
                        "ORDER BY i.sellerRef")
        List<Object[]> calculateAverageBaskets(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}