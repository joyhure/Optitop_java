package com.optitop.optitop_api.repository;

import com.optitop.optitop_api.model.InvoicesLines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoicesLinesRepository extends JpaRepository<InvoicesLines, Long> {
        @Modifying
        @Query("DELETE FROM InvoicesLines i WHERE i.date BETWEEN :startDate AND :endDate")
        void deleteByDateBetween(LocalDate startDate, LocalDate endDate);

        InvoicesLines findByInvoiceRef(String invoiceRef);

        Optional<InvoicesLines> findTopByOrderByCreatedAtDesc();

        @Query("SELECT DISTINCT i.seller.sellerRef FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "ORDER BY i.seller.sellerRef")
        List<String> findDistinctSellersBetweenDates(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.seller.sellerRef, " +
                        "SUM(i.totalTtc) as totalAmount " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'MON' " +
                        "AND i.pair = 1 " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculateP1FramesTotalAmounts(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.seller.sellerRef, " +
                        "COUNT(CASE WHEN i.status = 'facture' THEN 1 END) - " +
                        "COUNT(CASE WHEN i.status = 'avoir' THEN 1 END) " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'MON' " +
                        "AND i.pair = 1 " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculateP1FramesCounts(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.seller.sellerRef, " +
                        "SUM(CASE WHEN i.pair = 1 THEN i.quantity ELSE 0 END) as count " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'VER' " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculateP1LensesCounts(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.seller.sellerRef as sellerRef, " +
                        "SUM(CASE WHEN i.pair = 1 THEN i.totalTtc ELSE 0 END) as totalAmountP1VER " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'VER' " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculateP1LensesTotalAmounts(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.seller.sellerRef as sellerRef, " +
                        "SUM(CASE WHEN i.pair = 2 THEN i.totalTtc ELSE 0 END) as totalAmountP2 " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculateP2TotalAmounts(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.seller.sellerRef, " +
                        "SUM(CASE WHEN i.pair = 2 THEN i.quantity ELSE 0 END) as count " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculateP2Counts(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.seller.sellerRef as sellerRef, " +
                        "SUM(CASE " +
                        "WHEN i.totalTtc >= 200 THEN 1 " +
                        "WHEN i.totalTtc <= -200 THEN -1 " +
                        "ELSE 0 " +
                        "END) as premiumFrames " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'MON' " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculatePremiumFramesCount(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT DISTINCT YEAR(i.date) as year FROM InvoicesLines i ORDER BY year DESC")
        List<Integer> findDistinctYears();

        @Query("SELECT i FROM InvoicesLines i LEFT JOIN FETCH i.seller WHERE i.date BETWEEN :startDate AND :endDate")
        List<InvoicesLines> findByDateBetweenWithSeller(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}