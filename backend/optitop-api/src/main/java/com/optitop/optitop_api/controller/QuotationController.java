package com.optitop.optitop_api.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.optitop.optitop_api.model.Quotations;
import com.optitop.optitop_api.model.Quotations.QuotationAction;
import com.optitop.optitop_api.repository.QuotationsRepository;
import com.optitop.optitop_api.service.QuotationService;

import dto.QuotationDTO;
import dto.QuotationUpdateDTO;

@RestController
@RequestMapping("/api/quotations")
@CrossOrigin(origins = "http://localhost")
public class QuotationController {

    private static final Logger logger = LoggerFactory.getLogger(QuotationController.class);

    @Autowired
    private QuotationsRepository quotationsRepository;

    @Autowired
    private QuotationService quotationService;

    @GetMapping("/unvalidated")
    public ResponseEntity<List<QuotationDTO>> getUnvalidatedQuotations(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            List<Quotations> quotations = quotationsRepository.findUnvalidatedByDateBetween(start, end);
            List<QuotationDTO> dtos = quotations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (DateTimeParseException e) {
            logger.error("Format de date invalide", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des devis non validés", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/batch-update")
    public ResponseEntity<?> batchUpdate(@RequestBody List<QuotationUpdateDTO> updates) {
        try {
            quotationService.batchUpdate(updates);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour des devis", e);
            return ResponseEntity.internalServerError().body("Erreur lors de la mise à jour des devis");
        }
    }

    @GetMapping("/actions")
    public ResponseEntity<Map<String, String>> getActions() {
        try {
            Map<String, String> actions = Arrays.stream(QuotationAction.values())
                    .collect(Collectors.toMap(
                            Enum::name,
                            QuotationAction::getValue));

            return ResponseEntity.ok(actions);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des actions possibles", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private QuotationDTO convertToDTO(Quotations quotation) {
        QuotationDTO dto = new QuotationDTO(quotation.getId());
        dto.setDate(quotation.getDate());
        dto.setSellerRef(quotation.getSellerRef());
        dto.setClient(quotation.getClient());
        dto.setAction(quotation.getAction() != null ? quotation.getAction().getValue() : null);
        dto.setComment(quotation.getComment());
        return dto;
    }
}