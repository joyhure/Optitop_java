package com.optitop.optitop_api.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.optitop.optitop_api.model.Quotation;
import com.optitop.optitop_api.repository.QuotationRepository;

import dto.QuotationDTO;

@RestController
@RequestMapping("/api/quotations")
@CrossOrigin(origins = "http://localhost")
public class QuotationController {

    @Autowired
    private QuotationRepository quotationRepository;

    @GetMapping("/unvalidated")
    public ResponseEntity<List<QuotationDTO>> getUnvalidatedQuotations(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<Quotation> quotations = quotationRepository.findUnvalidatedQuotations(start, end);
        List<QuotationDTO> dtos = quotations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    private QuotationDTO convertToDTO(Quotation quotation) {
        QuotationDTO dto = new QuotationDTO();
        dto.setDate(quotation.getDate());
        dto.setSellerRef(quotation.getSellerRef());
        dto.setClient(quotation.getClient());
        dto.setAction(quotation.getAction() != null ? quotation.getAction().getValue() : null);
        dto.setComment(quotation.getComment());
        return dto;
    }
}