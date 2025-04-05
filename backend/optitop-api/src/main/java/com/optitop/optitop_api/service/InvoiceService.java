package com.optitop.optitop_api.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.optitop.optitop_api.dto.AverageBasketDTO;
import com.optitop.optitop_api.dto.FrameStatsDTO;
import com.optitop.optitop_api.repository.InvoicesLinesRepository;
import com.optitop.optitop_api.repository.InvoicesRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class InvoiceService {

        @Autowired
        private InvoicesLinesRepository invoicesLinesRepository;

        @Autowired
        private InvoicesRepository invoicesRepository;

        private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

        public List<AverageBasketDTO> getAverageBaskets(LocalDate startDate, LocalDate endDate) {
                // Récupération des montants totaux par vendeur
                Map<String, Double> totalAmounts = invoicesRepository.calculateTotalAmounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Double) row[1]));

                // Récupération des nombres de factures par vendeur
                Map<String, Long> invoiceCounts = invoicesRepository.calculateInvoiceCounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1]));

                // Ajout des montants P1 montures
                Map<String, Double> p1MonAmounts = invoicesLinesRepository
                                .calculateP1FramesTotalAmounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Double) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                Map<String, Long> p1MonCounts = invoicesLinesRepository.calculateP1FramesCounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                // Ajout des montants P1 verres
                Map<String, Double> p1VerAmounts = invoicesLinesRepository
                                .calculateP1LensesTotalAmounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Double) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                Map<String, Long> p1VerCounts = invoicesLinesRepository.calculateP1LensesCounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                // Ajout des montants P2
                Map<String, Double> p2Amounts = invoicesLinesRepository.calculateP2TotalAmounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Double) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                Map<String, Long> p2Counts = invoicesLinesRepository.calculateP2Counts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                // Création des DTOs avec toutes les données
                return totalAmounts.keySet().stream()
                                .map(sellerRef -> new AverageBasketDTO(
                                                sellerRef,
                                                totalAmounts.get(sellerRef),
                                                invoiceCounts.getOrDefault(sellerRef, 0L),
                                                p1MonAmounts.getOrDefault(sellerRef, 0.0),
                                                p1MonCounts.getOrDefault(sellerRef, 0L),
                                                p1VerAmounts.getOrDefault(sellerRef, 0.0),
                                                p1VerCounts.getOrDefault(sellerRef, 0L),
                                                p2Amounts.getOrDefault(sellerRef, 0.0),
                                                p2Counts.getOrDefault(sellerRef, 0L)))
                                .sorted(Comparator.comparing(AverageBasketDTO::getSellerRef))
                                .collect(Collectors.toList());
        }

        public List<FrameStatsDTO> getFrameStats(LocalDate startDate, LocalDate endDate) {
                Map<String, Long> totalFrames = invoicesLinesRepository.calculateP1FramesCounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1]));

                Map<String, Long> premiumFrames = invoicesLinesRepository
                                .calculatePremiumFramesCount(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1]));

                return totalFrames.keySet().stream()
                                .map(sellerRef -> new FrameStatsDTO(
                                                sellerRef,
                                                totalFrames.getOrDefault(sellerRef, 0L),
                                                premiumFrames.getOrDefault(sellerRef, 0L)))
                                .sorted(Comparator.comparing(FrameStatsDTO::getSellerRef))
                                .collect(Collectors.toList());
        }

        public List<Integer> getDistinctYears() {
                return invoicesLinesRepository.findDistinctYears();
        }

        public Map<Integer, Double> getMonthlyRevenue(int year) {
                return invoicesRepository.calculateMonthlyRevenue(year)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (Integer) row[0],
                                                row -> (Double) row[1],
                                                (v1, v2) -> v1,
                                                () -> new HashMap<>()));
        }

        public Map<String, Object> getPeriodRevenue(LocalDate startDate, LocalDate endDate) {
                Map<String, Object> result = new HashMap<>();

                List<Object[]> data = invoicesRepository.getTotalInvoicesForPeriodAndPreviousYear(startDate,
                                endDate);

                if (!data.isEmpty()) {
                        Object[] firstRow = data.get(0);
                        result.put("currentAmount", firstRow[0]);
                        result.put("previousAmount", firstRow[1]);
                } else {
                        result.put("currentAmount", 0.0);
                        result.put("previousAmount", 0.0);
                }

                return result;
        }

        public List<Map<String, Object>> getSellerRevenueStats(LocalDate startDate, LocalDate endDate) {
                List<Map<String, Object>> result = new ArrayList<>();

                List<Object[]> data = invoicesRepository.getSellerRevenueStats(startDate, endDate);

                for (Object[] row : data) {
                        Map<String, Object> sellerStats = new HashMap<>();
                        sellerStats.put("sellerRef", row[0]);
                        sellerStats.put("amount", row[1]);
                        sellerStats.put("percentage", row[2]);
                        result.add(sellerStats);
                }

                return result;
        }

        public AverageBasketDTO getTotalStats(LocalDate startDate, LocalDate endDate) {
                // Totaux depuis Invoices
                Double totalAmount = invoicesRepository.calculateTotalAmount(startDate, endDate);
                Long invoiceCount = invoicesRepository.calculateTotalInvoiceCount(startDate, endDate);

                // Totaux depuis InvoicesLines
                List<Object[]> p1FramesTotalAmounts = invoicesLinesRepository.calculateP1FramesTotalAmounts(startDate,
                                endDate);

                logger.info("P1 Frames Total Amounts: {}", p1FramesTotalAmounts);
                p1FramesTotalAmounts.forEach(row -> {
                        logger.info("Seller: {}, Amount: {}", row[0], row[1]);
                });

                Double totalAmountP1MON = p1FramesTotalAmounts.stream()
                                .map(row -> (Double) row[1])
                                .reduce(0.0, Double::sum);

                logger.info("Total Amount P1MON: {}", totalAmountP1MON);

                Long countP1MON = invoicesLinesRepository
                                .calculateP1FramesCounts(startDate, endDate)
                                .stream()
                                .map(row -> (Long) row[1])
                                .reduce(0L, Long::sum);

                Double totalAmountP1VER = invoicesLinesRepository
                                .calculateP1LensesTotalAmounts(startDate, endDate)
                                .stream()
                                .map(row -> (Double) row[1])
                                .reduce(0.0, Double::sum);

                Long countP1VER = invoicesLinesRepository
                                .calculateP1LensesCounts(startDate, endDate)
                                .stream()
                                .map(row -> (Long) row[1])
                                .reduce(0L, Long::sum);

                Double totalAmountP2 = invoicesLinesRepository
                                .calculateP2TotalAmounts(startDate, endDate)
                                .stream()
                                .map(row -> (Double) row[1])
                                .reduce(0.0, Double::sum);

                Long countP2 = invoicesLinesRepository
                                .calculateP2Counts(startDate, endDate)
                                .stream()
                                .map(row -> (Long) row[1])
                                .reduce(0L, Long::sum);

                return new AverageBasketDTO(
                                "TOTAL",
                                totalAmount,
                                invoiceCount,
                                totalAmountP1MON,
                                countP1MON,
                                totalAmountP1VER,
                                countP1VER,
                                totalAmountP2,
                                countP2);
        }
}