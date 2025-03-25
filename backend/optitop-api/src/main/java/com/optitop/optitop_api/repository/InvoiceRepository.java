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

    @Query("SELECT DISTINCT i.seller.sellerRef FROM Invoice i " +
            "WHERE i.date BETWEEN :startDate AND :endDate " +
            "ORDER BY i.seller.sellerRef")
    List<String> findDistinctSellersBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = """
                SELECT sub.seller_ref, SUM(sub.total_invoice) AS totalAmount
                FROM (
                    SELECT DISTINCT i.invoice_ref, i.seller_ref, i.total_invoice
                    FROM Invoice i
                    WHERE i.date BETWEEN :startDate AND :endDate
                    AND i.family = 'VER'
                ) sub
                GROUP BY sub.seller_ref
                ORDER BY sub.seller_ref
            """, nativeQuery = true)
    List<Object[]> calculateTotalAmounts(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT i.seller.sellerRef as sellerRef, " +
            "(SELECT COUNT(DISTINCT f.invoiceRef) FROM Invoice f " +
            "WHERE f.seller.sellerRef = i.seller.sellerRef " +
            "AND f.date BETWEEN :startDate AND :endDate " +
            "AND f.family = 'VER' " +
            "AND f.status = 'facture') - " +
            "(SELECT COUNT(DISTINCT a.invoiceRef) FROM Invoice a " +
            "WHERE a.seller.sellerRef = i.seller.sellerRef " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "AND a.family = 'VER' " +
            "AND a.status = 'avoir') as invoiceCount " +
            "FROM Invoice i " +
            "WHERE i.date BETWEEN :startDate AND :endDate " +
            "GROUP BY i.seller.sellerRef " +
            "ORDER BY i.seller.sellerRef")
    List<Object[]> calculateInvoiceCounts(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = """
                SELECT
                    i.seller_ref,
                    SUM(CASE WHEN i.pair = 1 THEN i.total_ttc ELSE 0 END) as total_amount
                FROM Invoice i
                WHERE i.date BETWEEN :startDate AND :endDate
                AND i.family = 'MON'
                GROUP BY i.seller_ref
                ORDER BY i.seller_ref
            """, nativeQuery = true)
    List<Object[]> calculateP1FramesTotalAmounts(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = """
                SELECT
                    i.seller_ref,
                    SUM(CASE WHEN i.pair = 1 THEN i.quantity ELSE 0 END) as count
                FROM Invoice i
                WHERE i.date BETWEEN :startDate AND :endDate
                AND i.family = 'MON'
                GROUP BY i.seller_ref
                ORDER BY i.seller_ref
            """, nativeQuery = true)
    List<Object[]> calculateP1FramesCounts(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = """
                SELECT
                    i.seller_ref,
                    SUM(CASE WHEN i.pair = 1 THEN i.quantity ELSE 0 END) as count
                FROM Invoice i
                WHERE i.date BETWEEN :startDate AND :endDate
                AND i.family = 'VER'
                GROUP BY i.seller_ref
                ORDER BY i.seller_ref
            """, nativeQuery = true)
    List<Object[]> calculateP1LensesCounts(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT i.seller.sellerRef as sellerRef, " +
            "SUM(CASE WHEN i.pair = 1 THEN i.totalTtc ELSE 0 END) as totalAmountP1VER " +
            "FROM Invoice i " +
            "WHERE i.date BETWEEN :startDate AND :endDate " +
            "AND i.family = 'VER' " +
            "GROUP BY i.seller.sellerRef " +
            "ORDER BY i.seller.sellerRef")
    List<Object[]> calculateP1LensesTotalAmounts(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT i.seller.sellerRef as sellerRef, " +
            "SUM(CASE WHEN i.pair = 2 THEN i.totalTtc ELSE 0 END) as totalAmountP2 " +
            "FROM Invoice i " +
            "WHERE i.date BETWEEN :startDate AND :endDate " +
            "GROUP BY i.seller.sellerRef " +
            "ORDER BY i.seller.sellerRef")
    List<Object[]> calculateP2TotalAmounts(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = """
                SELECT
                    i.seller_ref as seller_ref,
                    SUM(CASE WHEN i.pair = 2 THEN i.quantity ELSE 0 END) as count
                FROM Invoice i
                WHERE i.date BETWEEN :startDate AND :endDate
                GROUP BY i.seller_ref
                ORDER BY i.seller_ref
            """, nativeQuery = true)
    List<Object[]> calculateP2Counts(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT i.seller.sellerRef as sellerRef, " +
            "(SELECT COUNT(f) FROM Invoice f WHERE f.seller.sellerRef = i.seller.sellerRef " +
            "AND f.date BETWEEN :startDate AND :endDate " +
            "AND f.family = 'MON' AND f.totalTtc >= 200) - " +
            "(SELECT COUNT(a) FROM Invoice a WHERE a.seller.sellerRef = i.seller.sellerRef " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "AND a.family = 'MON' AND a.totalTtc <= -200) as premiumFrames " +
            "FROM Invoice i " +
            "WHERE i.date BETWEEN :startDate AND :endDate " +
            "AND i.family = 'MON' " +
            "GROUP BY i.seller.sellerRef " +
            "ORDER BY i.seller.sellerRef")
    List<Object[]> calculatePremiumFramesCount(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT DISTINCT YEAR(i.date) as year FROM Invoice i ORDER BY year DESC")
    List<Integer> findDistinctYears();

    @Query(value = """
                SELECT MONTH(sub.date) AS month,
                       SUM(sub.total_invoice) AS monthlyRevenue
                FROM (
                    SELECT DISTINCT i.invoice_ref, i.date, i.total_invoice
                    FROM Invoice i
                    WHERE YEAR(i.date) = :year
                ) sub
                GROUP BY MONTH(sub.date)
                ORDER BY month
            """, nativeQuery = true)
    List<Object[]> calculateMonthlyRevenue(@Param("year") int year);

    @Query(value = """
                SELECT
                    (SELECT COALESCE(SUM(total_invoice), 0)
                     FROM (
                         SELECT DISTINCT invoice_ref, total_invoice
                         FROM Invoice
                         WHERE date BETWEEN :startDate AND :endDate
                     ) current_period) as totalCurrentPeriod,
                    (SELECT COALESCE(SUM(total_invoice), 0)
                     FROM (
                         SELECT DISTINCT invoice_ref, total_invoice
                         FROM Invoice
                         WHERE date BETWEEN DATE_SUB(:startDate, INTERVAL 1 YEAR)
                               AND DATE_SUB(:endDate, INTERVAL 1 YEAR)
                     ) previous_period) as totalPreviousPeriod
            """, nativeQuery = true)
    List<Object[]> getTotalInvoicesForPeriodAndPreviousYear(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = """
                SELECT
                    sub.seller_ref,
                    SUM(sub.total_invoice) as seller_amount,
                    (SUM(sub.total_invoice) /
                        (SELECT COALESCE(SUM(total_invoice), 0)
                         FROM (
                             SELECT DISTINCT invoice_ref, total_invoice
                             FROM Invoice
                             WHERE date BETWEEN :startDate AND :endDate
                         ) total)) * 100 as percentage
                FROM (
                    SELECT DISTINCT i.invoice_ref, i.seller_ref, i.total_invoice
                    FROM Invoice i
                    WHERE i.date BETWEEN :startDate AND :endDate
                ) sub
                GROUP BY sub.seller_ref
                ORDER BY seller_amount DESC
            """, nativeQuery = true)
    List<Object[]> getSellerRevenueStats(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}