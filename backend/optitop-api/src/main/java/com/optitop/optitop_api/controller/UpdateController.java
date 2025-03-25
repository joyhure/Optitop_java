package com.optitop.optitop_api.controller;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.optitop.optitop_api.model.InvoicesLines;
import com.optitop.optitop_api.model.QuotationsLines;
import com.optitop.optitop_api.repository.InvoicesLinesRepository;
import com.optitop.optitop_api.repository.QuotationsLinesRepository;

@RestController
@RequestMapping("/api/updates")
@CrossOrigin(origins = "http://localhost")
public class UpdateController {

    @Autowired
    private QuotationsLinesRepository quotationsLinesRepository;

    @Autowired
    private InvoicesLinesRepository invoiceRepository;

    @GetMapping("/last")
    public ResponseEntity<String> getLastUpdate() {
        // Récupérer la dernière date entre quotation et invoice
        LocalDateTime lastQuotationDate = quotationsLinesRepository
                .findTopByOrderByCreatedAtDesc()
                .map(QuotationsLines::getCreatedAt)
                .orElse(null);

        LocalDateTime lastInvoiceDate = invoiceRepository.findTopByOrderByCreatedAtDesc()
                .map(InvoicesLines::getCreatedAt)
                .orElse(null);

        LocalDateTime lastUpdate = Stream.of(lastQuotationDate, lastInvoiceDate)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        if (lastUpdate != null) {
            return ResponseEntity.ok(lastUpdate.toString());
        }

        return ResponseEntity.notFound().build();
    }
}