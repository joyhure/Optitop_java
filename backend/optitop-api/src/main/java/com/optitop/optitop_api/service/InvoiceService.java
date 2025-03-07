package com.optitop.optitop_api.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.optitop.optitop_api.dto.AverageBasketDTO;
import com.optitop.optitop_api.repository.InvoiceRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    public List<AverageBasketDTO> calculateAverageBaskets(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = invoiceRepository.calculateAverageBaskets(startDate, endDate);

        return results.stream()
                .map(row -> new AverageBasketDTO(
                        (String) row[0], // sellerRef
                        (Double) row[1], // averageBasket
                        ((Number) row[2]).longValue(), // invoiceCount
                        (Double) row[3], // averageFramesP1
                        (Double) row[4], // averageLensesP1
                        (Double) row[5] // averageP2
                ))
                .collect(Collectors.toList());
    }
}