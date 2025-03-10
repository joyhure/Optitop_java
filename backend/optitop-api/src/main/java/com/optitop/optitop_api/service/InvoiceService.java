package com.optitop.optitop_api.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.optitop.optitop_api.dto.AverageBasketDTO;
import com.optitop.optitop_api.dto.FrameStatsDTO;
import com.optitop.optitop_api.repository.InvoiceRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashMap;

@Service
public class InvoiceService {

        @Autowired
        private InvoiceRepository invoiceRepository;

        public List<AverageBasketDTO> getAverageBaskets(LocalDate startDate, LocalDate endDate) {
                // Récupération des montants totaux par vendeur
                Map<String, Double> totalAmounts = invoiceRepository.calculateTotalAmounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Double) row[1]));

                // Récupération des nombres de factures par vendeur
                Map<String, Long> invoiceCounts = invoiceRepository.calculateInvoiceCounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1]));

                // Ajout des montants P1 montures
                Map<String, Double> p1MonAmounts = invoiceRepository.calculateP1FramesTotalAmounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Double) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                Map<String, Long> p1MonCounts = invoiceRepository.calculateP1FramesCounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                // Ajout des montants P1 verres
                Map<String, Double> p1VerAmounts = invoiceRepository.calculateP1LensesTotalAmounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Double) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                Map<String, Long> p1VerCounts = invoiceRepository.calculateP1LensesCounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                // Ajout des montants P2
                Map<String, Double> p2Amounts = invoiceRepository.calculateP2TotalAmounts(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Double) row[1],
                                                (v1, v2) -> v1,
                                                HashMap::new));

                Map<String, Long> p2Counts = invoiceRepository.calculateP2Counts(startDate, endDate)
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
                Map<String, Long> totalFrames = invoiceRepository.calculateTotalFramesCount(startDate, endDate)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (String) row[0],
                                                row -> (Long) row[1]));

                Map<String, Long> premiumFrames = invoiceRepository.calculatePremiumFramesCount(startDate, endDate)
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
                return invoiceRepository.findDistinctYears();
        }
}