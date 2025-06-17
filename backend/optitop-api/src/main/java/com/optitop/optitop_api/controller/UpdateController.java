package com.optitop.optitop_api.controller;

/**
 * Contrôleur de gestion des mises à jour
 * 
 * Fournit l'API REST pour récupérer la date de dernière mise à jour
 * des données (devis et factures) du système.
 */

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/updates")
@CrossOrigin(origins = "http://localhost")
@Tag(name = "Date de dernière mise à jour (UpdateController)", description = "Gestion de la date de dernière MàJ des données")
public class UpdateController {

        // ===== DÉPENDANCES =====

        @Autowired
        private QuotationsLinesRepository quotationsLinesRepository;

        @Autowired
        private InvoicesLinesRepository invoiceRepository;

        // ===== ENDPOINTS =====

        /**
         * Récupère la date de dernière mise à jour des données
         * 
         * @return Date de la dernière mise à jour entre factures et devis
         */
        @Operation(summary = "Dernière mise à jour", description = "Récupère la date de la dernière mise à jour entre les factures et les devis")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Date de dernière mise à jour trouvée", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", description = "Date au format ISO-8601", example = "2024-05-02T14:30:00"))),
                        @ApiResponse(responseCode = "404", description = "Aucune donnée disponible", content = @Content)
        })
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