package com.optitop.optitop_api.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.optitop.optitop_api.dto.AverageBasketDTO;
import com.optitop.optitop_api.repository.InvoiceRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

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

        // Création des DTOs avec calcul automatique des moyennes et tri par sellerRef
        return totalAmounts.keySet().stream()
                .map(sellerRef -> new AverageBasketDTO(
                        sellerRef,
                        totalAmounts.get(sellerRef),
                        invoiceCounts.getOrDefault(sellerRef, 0L)))
                .sorted(Comparator.comparing(AverageBasketDTO::getSellerRef))
                .collect(Collectors.toList());
    }
}