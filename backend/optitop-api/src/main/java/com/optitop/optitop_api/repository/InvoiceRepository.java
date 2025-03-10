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

        @Query("SELECT i.sellerRef as sellerRef, " +
                        "SUM(DISTINCT CASE WHEN i.invoiceRef = i.invoiceRef THEN i.totalInvoice ELSE 0 END) as totalAmount "
                        +
                        "FROM Invoice i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'VER' " +
                        "GROUP BY i.sellerRef " +
                        "ORDER BY i.sellerRef")
        List<Object[]> calculateTotalAmounts(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.sellerRef as sellerRef, " +
                        "(SELECT COUNT(DISTINCT f.invoiceRef) FROM Invoice f " +
                        "WHERE f.sellerRef = i.sellerRef " +
                        "AND f.date BETWEEN :startDate AND :endDate " +
                        "AND f.family = 'VER' " +
                        "AND f.status = 'facture') - " +
                        "(SELECT COUNT(DISTINCT a.invoiceRef) FROM Invoice a " +
                        "WHERE a.sellerRef = i.sellerRef " +
                        "AND a.date BETWEEN :startDate AND :endDate " +
                        "AND a.family = 'VER' " +
                        "AND a.status = 'avoir') as invoiceCount " +
                        "FROM Invoice i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "GROUP BY i.sellerRef")
        List<Object[]> calculateInvoiceCounts(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.sellerRef as sellerRef, " +
                        "SUM(CASE WHEN i.pair = 1 THEN i.totalTtc ELSE 0 END) as totalAmountP1MON " +
                        "FROM Invoice i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'MON' " +
                        "GROUP BY i.sellerRef " +
                        "ORDER BY i.sellerRef")
        List<Object[]> calculateP1FramesTotalAmounts(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.sellerRef as sellerRef, " +
                        "COUNT(CASE WHEN i.pair = 1 THEN 1 ELSE null END) as countP1MON " +
                        "FROM Invoice i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'MON' " +
                        "GROUP BY i.sellerRef " +
                        "ORDER BY i.sellerRef")
        List<Object[]> calculateP1FramesCounts(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.sellerRef as sellerRef, " +
                        "SUM(CASE WHEN i.pair = 1 THEN i.totalTtc ELSE 0 END) as totalAmountP1VER " +
                        "FROM Invoice i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'VER' " +
                        "GROUP BY i.sellerRef " +
                        "ORDER BY i.sellerRef")
        List<Object[]> calculateP1LensesTotalAmounts(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.sellerRef as sellerRef, " +
                        "COUNT(CASE WHEN i.pair = 1 THEN 1 ELSE null END) as countP1VER " +
                        "FROM Invoice i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'VER' " +
                        "GROUP BY i.sellerRef " +
                        "ORDER BY i.sellerRef")
        List<Object[]> calculateP1LensesCounts(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.sellerRef as sellerRef, " +
                        "SUM(CASE WHEN i.pair = 2 THEN i.totalTtc ELSE 0 END) as totalAmountP2 " +
                        "FROM Invoice i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "GROUP BY i.sellerRef " +
                        "ORDER BY i.sellerRef")
        List<Object[]> calculateP2TotalAmounts(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.sellerRef as sellerRef, " +
                        "COUNT(CASE WHEN i.pair = 2 THEN 1 ELSE null END) as countP2 " +
                        "FROM Invoice i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "GROUP BY i.sellerRef " +
                        "ORDER BY i.sellerRef")
        List<Object[]> calculateP2Counts(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.sellerRef as sellerRef, " +
                        "COUNT(i) as totalFrames " +
                        "FROM Invoice i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'MON' " +
                        "AND i.pair = 1 " +
                        "GROUP BY i.sellerRef " +
                        "ORDER BY i.sellerRef")
        List<Object[]> calculateTotalFramesCount(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.sellerRef as sellerRef, " +
                        "(SELECT COUNT(f) FROM Invoice f WHERE f.sellerRef = i.sellerRef " +
                        "AND f.date BETWEEN :startDate AND :endDate " +
                        "AND f.family = 'MON' AND f.totalTtc >= 200) - " +
                        "(SELECT COUNT(a) FROM Invoice a WHERE a.sellerRef = i.sellerRef " +
                        "AND a.date BETWEEN :startDate AND :endDate " +
                        "AND a.family = 'MON' AND a.totalTtc <= -200) as premiumFrames " +
                        "FROM Invoice i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'MON' " +
                        "GROUP BY i.sellerRef " +
                        "ORDER BY i.sellerRef")
        List<Object[]> calculatePremiumFramesCount(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT DISTINCT YEAR(i.date) as year FROM Invoice i ORDER BY year DESC")
        List<Integer> findDistinctYears();
}