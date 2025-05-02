package com.optitop.optitop_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.optitop.optitop_api.dto.AverageBasketDTO;
import com.optitop.optitop_api.dto.FrameStatsDTO;
import com.optitop.optitop_api.service.InvoiceService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost")
@Tag(name = "Factures (InvoiceController)", description = "Gestion des statistiques de vente liées aux factures")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Operation(summary = "Obtenir les paniers moyens par vendeur", description = "Récupère les statistiques détaillées des paniers moyens (P1 montures, P1 verres, P2) pour chaque vendeur sur une période donnée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AverageBasketDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors du calcul des statistiques")
    })
    @GetMapping("/average-baskets")
    public ResponseEntity<List<AverageBasketDTO>> getAverageBaskets(
            @Parameter(description = "Date de début de la période (format: YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin de la période (format: YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<AverageBasketDTO> stats = invoiceService.getAverageBaskets(startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Obtenir les statistiques globales du magasin", description = "Calcule les totaux et moyennes pour l'ensemble du magasin (ligne TOTAL des tableaux)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques globales calculées avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AverageBasketDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur lors du calcul des statistiques globales")
    })
    @GetMapping("/total-stats")
    public ResponseEntity<AverageBasketDTO> getTotalStats(
            @Parameter(description = "Date de début (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            AverageBasketDTO stats = invoiceService.getTotalStats(startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Statistiques des montures primées", description = "Récupère les statistiques de vente des montures (normales et primées) par vendeur")
    @GetMapping("/frame-stats")
    public ResponseEntity<List<FrameStatsDTO>> getFrameStats(
            @Parameter(description = "Date de début (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(invoiceService.getFrameStats(startDate, endDate));
    }

    @Operation(summary = "Années d'activités", description = "Liste toutes les années pour lesquelles des factures existent")
    @GetMapping("/years")
    public ResponseEntity<List<Integer>> getDistinctYears() {
        return ResponseEntity.ok(invoiceService.getDistinctYears());
    }

    @Operation(summary = "Chiffre d'affaires mensuel par année", description = "Calcule le chiffre d'affaires pour chaque mois d'une année donnée")
    @GetMapping("/monthly-revenue/{year}")
    public ResponseEntity<Map<Integer, Double>> getMonthlyRevenue(
            @Parameter(description = "Année (ex: 2024)") @PathVariable int year) {
        return ResponseEntity.ok(invoiceService.getMonthlyRevenue(year));
    }

    @Operation(summary = "Chiffre d'affaires sur période et sa comparaison n-1", description = "Compare le chiffre d'affaires entre une période et la même période l'année précédente")
    @GetMapping("/period-revenue")
    public ResponseEntity<Map<String, Object>> getPeriodRevenue(
            @Parameter(description = "Date de début (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(invoiceService.getPeriodRevenue(startDate, endDate));
    }

    @Operation(summary = "CA par vendeur et pourcentage du CA total", description = "Calcule le chiffre d'affaires et le pourcentage de contribution de chaque vendeur")
    @GetMapping("/seller-stats")
    public ResponseEntity<List<Map<String, Object>>> getSellerRevenueStats(
            @Parameter(description = "Date de début (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(invoiceService.getSellerRevenueStats(startDate, endDate));
    }

}