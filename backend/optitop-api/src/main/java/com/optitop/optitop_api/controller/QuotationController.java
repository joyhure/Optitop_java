package com.optitop.optitop_api.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.optitop.optitop_api.dto.QuotationDTO;
import com.optitop.optitop_api.dto.QuotationStatsDTO;
import com.optitop.optitop_api.dto.QuotationUpdateDTO;
import com.optitop.optitop_api.dto.SellerStatsDTO;
import com.optitop.optitop_api.model.Quotations;
import com.optitop.optitop_api.model.Quotations.QuotationAction;
import com.optitop.optitop_api.repository.QuotationsRepository;
import com.optitop.optitop_api.service.QuotationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;

@RestController
@RequestMapping("/api/quotations")
@CrossOrigin(origins = "http://localhost")
@Tag(name = "Devis (QuotationController)", description = "Gestion des devis et de leurs statistiques")
public class QuotationController {

    private static final Logger logger = LoggerFactory.getLogger(QuotationController.class);

    @Autowired
    private QuotationsRepository quotationsRepository;

    @Autowired
    private QuotationService quotationService;

    @Operation(summary = "Récupérer les devis non validés", description = "Retourne la liste des devis non validés pour une période donnée, filtré par vendeur si l'utilisateur est un collaborateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des devis récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = QuotationDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération")
    })
    @GetMapping("/unvalidated")
    public ResponseEntity<List<QuotationDTO>> getUnvalidatedQuotations(
            @Parameter(description = "Date de début (YYYY-MM-DD)") @RequestParam String startDate,
            @Parameter(description = "Date de fin (YYYY-MM-DD)") @RequestParam String endDate,
            @Parameter(description = "Rôle de l'utilisateur") @RequestParam(required = false) String userRole,
            @Parameter(description = "Référence du vendeur") @RequestParam(required = false) String userSellerRef) {

        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            List<Quotations> quotations;
            if ("collaborator".equalsIgnoreCase(userRole) && userSellerRef != null) {
                logger.debug("Recherche des devis pour le collaborateur: {}", userSellerRef);
                quotations = quotationsRepository.findUnvalidatedByDateBetweenAndSellerRef(
                        start, end, userSellerRef);
            } else {
                logger.debug("Recherche de tous les devis non validés");
                quotations = quotationsRepository.findUnvalidatedByDateBetween(start, end);
            }

            List<QuotationDTO> dtos = quotations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des devis non validés", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Mettre à jour un lot de devis", description = "Met à jour les actions et commentaires d'un lot de devis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mise à jour effectuée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la mise à jour", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Erreur lors de la mise à jour des devis\"}")))
    })
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

    @Operation(summary = "Récupérer les actions possibles", description = "Liste toutes les actions possibles pour un devis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des actions récupérée", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"VOIR_OPTICIEN\": \"Voir opticien\", \"NON_VALIDE\": \"Non validé\"}")))
    })
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

    @Operation(summary = "Statistiques des devis", description = "Récupère les statistiques globales et par vendeur des devis sur une période")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques calculées avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuotationStatsDTO.class)))
    })
    @GetMapping("/stats")
    public ResponseEntity<QuotationStatsDTO> getQuotationStats(
            @Parameter(description = "Date de début (YYYY-MM-DD)") @RequestParam String startDate,
            @Parameter(description = "Date de fin (YYYY-MM-DD)") @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            // Statistiques globales
            Long total = quotationsRepository.countQuotationsBetween(start, end);
            Long validated = quotationsRepository.countValidatedQuotationsBetween(start, end);
            Long unvalidated = quotationsRepository.countUnvalidatedQuotationsBetween(start, end);

            QuotationStatsDTO stats = new QuotationStatsDTO(total, validated, unvalidated);

            // Récupération des statistiques par vendeur
            List<Object[]> sellerStats = quotationsRepository.getSellerStats(start, end);
            List<SellerStatsDTO> sellerStatsDTOs = sellerStats.stream()
                    .map(row -> new SellerStatsDTO(
                            (String) row[0], // sellerRef
                            (Long) row[1], // total
                            (Long) row[2] // unvalidated
                    ))
                    .collect(Collectors.toList());

            stats.setSellerStats(sellerStatsDTOs);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Erreur lors du calcul des statistiques", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Taux de concrétisation N-1", description = "Calcule le taux de concrétisation pour la même période l'année précédente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Taux calculé avec succès", content = @Content(mediaType = "application/json", schema = @Schema(type = "number", format = "double", example = "75.5")))
    })
    @GetMapping("/previous-concretization")
    public ResponseEntity<Double> getPreviousConcretizationRate(
            @Parameter(description = "Date de début (YYYY-MM-DD)") @RequestParam String startDate,
            @Parameter(description = "Date de fin (YYYY-MM-DD)") @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate).minusYears(1);
            LocalDate end = LocalDate.parse(endDate).minusYears(1);

            Double rate = quotationsRepository.getPreviousConcretizationRate(start, end);
            return ResponseEntity.ok(rate != null ? rate : 0.0);
        } catch (Exception e) {
            logger.error("Erreur lors du calcul du taux de concrétisation N-1", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private QuotationDTO convertToDTO(Quotations quotation) {
        QuotationDTO dto = new QuotationDTO(quotation.getId());
        dto.setDate(quotation.getDate());

        // Récupération du seller depuis la quotation
        if (quotation.getSeller() != null) {
            dto.setSeller(quotation.getSeller().getSellerRef());
        } else {
            dto.setSeller("Non assigné");
        }

        dto.setClient(quotation.getClient());
        dto.setAction(quotation.getAction() != null ? quotation.getAction().getValue() : null);
        dto.setComment(quotation.getComment());
        return dto;
    }
}