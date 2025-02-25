package com.optitop.optitop_api.repository;

import com.optitop.optitop_api.model.Quotations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationsRepository extends JpaRepository<Quotations, Long> {
    @Query("SELECT q FROM Quotations q WHERE q.date BETWEEN :startDate AND :endDate")
    List<Quotations> findByDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    Optional<Quotations> findByClientIdAndDate(String clientId, LocalDate date);
}