package com.optitop.optitop_api.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.optitop.optitop_api.model.Quotations;
import com.optitop.optitop_api.repository.QuotationsRepository;

import dto.QuotationDTO;

@RestController
@RequestMapping("/api/quotations")
@CrossOrigin(origins = "http://localhost")
public class QuotationController {

    @Autowired
    private QuotationsRepository quotationsRepository;

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
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private QuotationDTO convertToDTO(Quotations quotation) {
        QuotationDTO dto = new QuotationDTO();
        try {
            dto.setDate(quotation.getDate());
            dto.setSellerRef(quotation.getSellerRef());
            dto.setClient(quotation.getClient());
            dto.setAction(quotation.getAction() != null ? quotation.getAction().getValue() : null);
            dto.setComment(quotation.getComment());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la conversion en DTO", e);
        }
        return dto;
    }
}