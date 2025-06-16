package com.optitop.optitop_api.controller;

// ===== IMPORTS DTO ET MODÈLES =====
import com.optitop.optitop_api.dto.AverageBasketDTO;
import com.optitop.optitop_api.dto.FrameStatsDTO;
import com.optitop.optitop_api.service.InvoiceService;

// ===== IMPORTS SPRING FRAMEWORK =====
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// ===== IMPORTS SWAGGER (DOCUMENTATION API) =====
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

// ===== IMPORTS UTILITAIRES =====
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des statistiques de factures
 * 
 * Gère les opérations de calcul et récupération des statistiques de vente
 * Endpoints disponibles :
 * - GET /api/invoices/average-baskets : Statistiques paniers moyens par vendeur
 * - GET /api/invoices/total-stats : Statistiques globales du magasin
 * - GET /api/invoices/frame-stats : Statistiques des montures primées
 * - GET /api/invoices/years : Années d'activité disponibles
 * - GET /api/invoices/monthly-revenue/{year} : CA mensuel par année
 * - GET /api/invoices/period-revenue : CA sur période avec comparaison N-1
 * - GET /api/invoices/seller-stats : CA et pourcentages par vendeur
 */
@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = { "http://localhost", "http://10.0.2.2", "http://optitop.local" })
@Tag(name = "Factures (InvoiceController)", description = "Gestion des statistiques de vente liées aux factures")
public class InvoiceController {

    // ===== INJECTION DES DÉPENDANCES =====

    /**
     * Service pour les opérations sur les factures
     * Gère les calculs statistiques et l'accès aux données de vente
     */
    @Autowired
    private InvoiceService invoiceService;

    // ===== ENDPOINTS STATISTIQUES DÉTAILLÉES =====

    /**
     * Récupère les statistiques détaillées des paniers moyens par vendeur
     * 
     * @param startDate Date de début de la période d'analyse
     * @param endDate   Date de fin de la période d'analyse
     * @return ResponseEntity avec les statistiques par vendeur si succès, erreur si
     *         échec
     * 
     *         Codes de retour :
     *         - 200 : Statistiques calculées avec succès
     *         - 500 : Erreur lors du calcul des statistiques
     */
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
            // Récupération des statistiques via le service
            List<AverageBasketDTO> stats = invoiceService.getAverageBaskets(startDate, endDate);

            // Retour des données de succès
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // Retour d'erreur en cas d'exception
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Calcule les statistiques globales du magasin pour la période donnée
     * 
     * @param startDate Date de début de la période d'analyse
     * @param endDate   Date de fin de la période d'analyse
     * @return ResponseEntity avec les totaux et moyennes globales si succès, erreur
     *         si échec
     * 
     *         Codes de retour :
     *         - 200 : Statistiques globales calculées avec succès
     *         - 500 : Erreur lors du calcul des statistiques globales
     */
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
            // Calcul des statistiques globales via le service
            AverageBasketDTO stats = invoiceService.getTotalStats(startDate, endDate);

            // Retour des données de succès
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // Retour d'erreur en cas d'exception
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupère les statistiques de vente des montures par vendeur
     * 
     * @param startDate Date de début de la période d'analyse
     * @param endDate   Date de fin de la période d'analyse
     * @return ResponseEntity avec les statistiques de montures (normales et
     *         primées) par vendeur
     * 
     *         Codes de retour :
     *         - 200 : Statistiques des montures récupérées avec succès
     *         - 500 : Erreur lors du calcul des statistiques
     */
    @Operation(summary = "Statistiques des montures primées", description = "Récupère les statistiques de vente des montures (normales et primées) par vendeur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques des montures récupérées avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FrameStatsDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur lors du calcul des statistiques des montures")
    })
    @GetMapping("/frame-stats")
    public ResponseEntity<List<FrameStatsDTO>> getFrameStats(
            @Parameter(description = "Date de début (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Récupération des statistiques de montures via le service
        List<FrameStatsDTO> stats = invoiceService.getFrameStats(startDate, endDate);

        // Retour des données de succès
        return ResponseEntity.ok(stats);
    }

    // ===== ENDPOINTS DONNÉES RÉFÉRENTIELLES =====

    /**
     * Liste toutes les années pour lesquelles des factures existent en base
     * 
     * @return ResponseEntity avec la liste des années d'activité
     * 
     *         Codes de retour :
     *         - 200 : Liste des années récupérée avec succès
     *         - 500 : Erreur lors de la récupération des années
     */
    @Operation(summary = "Années d'activités", description = "Liste toutes les années pour lesquelles des factures existent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des années récupérée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la récupération des années")
    })
    @GetMapping("/years")
    public ResponseEntity<List<Integer>> getDistinctYears() {

        // Récupération des années d'activité via le service
        List<Integer> years = invoiceService.getDistinctYears();

        // Retour des données de succès
        return ResponseEntity.ok(years);
    }

    // ===== ENDPOINTS ANALYSES TEMPORELLES =====

    /**
     * Calcule le chiffre d'affaires mensuel pour une année donnée
     * 
     * @param year Année pour laquelle calculer le CA mensuel
     * @return ResponseEntity avec les montants de CA par mois
     * 
     *         Codes de retour :
     *         - 200 : CA mensuel calculé avec succès
     *         - 500 : Erreur lors du calcul du CA mensuel
     */
    @Operation(summary = "Chiffre d'affaires mensuel par année", description = "Calcule le chiffre d'affaires pour chaque mois d'une année donnée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CA mensuel calculé avec succès", content = @Content(mediaType = "application/json", schema = @Schema(description = "Map avec numéro de mois et montant CA", example = "{\"1\": 125000.50, \"2\": 130000.75}"))),
            @ApiResponse(responseCode = "500", description = "Erreur lors du calcul du CA mensuel")
    })
    @GetMapping("/monthly-revenue/{year}")
    public ResponseEntity<Map<Integer, Double>> getMonthlyRevenue(
            @Parameter(description = "Année (ex: 2024)") @PathVariable int year) {

        // Calcul du CA mensuel via le service
        Map<Integer, Double> monthlyRevenue = invoiceService.getMonthlyRevenue(year);

        // Retour des données de succès
        return ResponseEntity.ok(monthlyRevenue);
    }

    /**
     * Compare le chiffre d'affaires d'une période avec la même période l'année
     * précédente
     * 
     * @param startDate Date de début de la période d'analyse
     * @param endDate   Date de fin de la période d'analyse
     * @return ResponseEntity avec le CA actuel, CA N-1 et évolution
     * 
     *         Codes de retour :
     *         - 200 : Comparaison de CA calculée avec succès
     *         - 500 : Erreur lors du calcul de la comparaison
     */
    @Operation(summary = "Chiffre d'affaires sur période et sa comparaison n-1", description = "Compare le chiffre d'affaires entre une période et la même période l'année précédente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comparaison de CA calculée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(description = "Objet avec CA actuel, CA N-1 et évolution", example = "{\"currentAmount\": 150000, \"previousAmount\": 140000, \"evolution\": 7.14}"))),
            @ApiResponse(responseCode = "500", description = "Erreur lors du calcul de la comparaison")
    })
    @GetMapping("/period-revenue")
    public ResponseEntity<Map<String, Object>> getPeriodRevenue(
            @Parameter(description = "Date de début (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Calcul de la comparaison de CA via le service
        Map<String, Object> periodRevenue = invoiceService.getPeriodRevenue(startDate, endDate);

        // Retour des données de succès
        return ResponseEntity.ok(periodRevenue);
    }

    // ===== ENDPOINTS ANALYSES PAR VENDEUR =====

    /**
     * Calcule le chiffre d'affaires et le pourcentage de contribution de chaque
     * vendeur
     * 
     * @param startDate Date de début de la période d'analyse
     * @param endDate   Date de fin de la période d'analyse
     * @return ResponseEntity avec les statistiques de CA par vendeur
     * 
     *         Codes de retour :
     *         - 200 : Statistiques vendeurs calculées avec succès
     *         - 500 : Erreur lors du calcul des statistiques vendeurs
     */
    @Operation(summary = "CA par vendeur et pourcentage du CA total", description = "Calcule le chiffre d'affaires et le pourcentage de contribution de chaque vendeur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques vendeurs calculées avec succès", content = @Content(mediaType = "application/json", schema = @Schema(description = "Liste des vendeurs avec CA et pourcentage", example = "[{\"sellerRef\": \"SELL001\", \"amount\": 50000, \"percentage\": 25.5}]"))),
            @ApiResponse(responseCode = "500", description = "Erreur lors du calcul des statistiques vendeurs")
    })
    @GetMapping("/seller-stats")
    public ResponseEntity<List<Map<String, Object>>> getSellerRevenueStats(
            @Parameter(description = "Date de début (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Calcul des statistiques vendeurs via le service
        List<Map<String, Object>> sellerStats = invoiceService.getSellerRevenueStats(startDate, endDate);

        // Retour des données de succès
        return ResponseEntity.ok(sellerStats);
    }
}