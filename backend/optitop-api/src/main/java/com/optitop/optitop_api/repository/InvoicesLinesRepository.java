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
                        "SUM(CASE WHEN i.pair = 1 THEN i.totalTtc ELSE 0 END) as totalAmount " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'MON' " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculateP1FramesTotalAmounts(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT i.seller.sellerRef, " +
                        "SUM(CASE WHEN i.pair = 1 THEN i.quantity ELSE 0 END) as count " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'MON' " +
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
                        "(SELECT COUNT(f) FROM InvoicesLines f WHERE f.seller.sellerRef = i.seller.sellerRef " +
                        "AND f.date BETWEEN :startDate AND :endDate " +
                        "AND f.family = 'MON' AND f.totalTtc >= 200) - " +
                        "(SELECT COUNT(a) FROM InvoicesLines a WHERE a.seller.sellerRef = i.seller.sellerRef " +
                        "AND a.date BETWEEN :startDate AND :endDate " +
                        "AND a.family = 'MON' AND a.totalTtc <= -200) as premiumFrames " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "AND i.family = 'MON' " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY i.seller.sellerRef")
        List<Object[]> calculatePremiumFramesCount(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT DISTINCT YEAR(i.date) as year FROM InvoicesLines i ORDER BY year DESC")
        List<Integer> findDistinctYears();

        @Query("SELECT MONTH(i.date) as month, " +
                        "SUM(DISTINCT i.totalInvoice) as monthlyRevenue " +
                        "FROM InvoicesLines i " +
                        "WHERE FUNCTION('YEAR', i.date) = :year " +
                        "GROUP BY MONTH(i.date) " +
                        "ORDER BY month")
        List<Object[]> calculateMonthlyRevenue(@Param("year") int year);

        @Query("SELECT " +
                        "(SELECT COALESCE(SUM(DISTINCT i1.totalInvoice), 0) " +
                        "FROM InvoicesLines i1 " +
                        "WHERE i1.date BETWEEN :startDate AND :endDate) as totalCurrentPeriod, " +
                        "(SELECT COALESCE(SUM(DISTINCT i2.totalInvoice), 0) " +
                        "FROM InvoicesLines i2 " +
                        "WHERE i2.date BETWEEN function('DATE_SUB', :startDate, 1, 'YEAR') " +
                        "AND function('DATE_SUB', :endDate, 1, 'YEAR')) as totalPreviousPeriod")
        List<Object[]> getTotalInvoicesForPeriodAndPreviousYear(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT DISTINCT i.seller.sellerRef, " +
                        "SUM(DISTINCT i.totalInvoice) as sellerAmount, " +
                        "(SUM(DISTINCT i.totalInvoice) / " +
                        "(SELECT COALESCE(SUM(DISTINCT s.totalInvoice), 0) " +
                        "FROM InvoicesLines s " +
                        "WHERE s.date BETWEEN :startDate AND :endDate)) * 100 as percentage " +
                        "FROM InvoicesLines i " +
                        "WHERE i.date BETWEEN :startDate AND :endDate " +
                        "GROUP BY i.seller.sellerRef " +
                        "ORDER BY sellerAmount DESC")
        List<Object[]> getSellerRevenueStats(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        List<InvoicesLines> findByDateBetween(LocalDate startDate, LocalDate endDate);

        @Query("SELECT i FROM InvoicesLines i LEFT JOIN FETCH i.seller WHERE i.date BETWEEN :startDate AND :endDate")
        List<InvoicesLines> findByDateBetweenWithSeller(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}