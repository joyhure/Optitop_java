package com.optitop.optitop_api.controller;

// ===== IMPORTS DTO ET MODÈLES =====
import com.optitop.optitop_api.dto.QuotationDTO;
import com.optitop.optitop_api.dto.QuotationStatsDTO;
import com.optitop.optitop_api.dto.QuotationUpdateDTO;
import com.optitop.optitop_api.dto.SellerStatsDTO;
import com.optitop.optitop_api.model.Quotations;
import com.optitop.optitop_api.model.Quotations.QuotationAction;
import com.optitop.optitop_api.repository.QuotationsRepository;
import com.optitop.optitop_api.service.QuotationService;

// ===== IMPORTS SPRING FRAMEWORK =====
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import io.swagger.v3.oas.annotations.media.ArraySchema;

// ===== IMPORTS UTILITAIRES =====
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour la gestion des devis optiques
 * 
 * Fournit les opérations métier pour la gestion des devis :
 * - Récupération des devis non validés avec filtrage par rôle utilisateur
 * - Mise à jour batch des actions et commentaires
 * - Statistiques globales et par vendeur
 * - Calculs de taux de concrétisation avec comparaisons N-1
 * - Gestion des actions possibles sur les devis
 * 
 * Endpoints disponibles :
 * - GET /api/quotations/unvalidated : Devis non validés avec filtrage
 * - PUT /api/quotations/batch-update : Mise à jour en lot
 * - GET /api/quotations/actions : Actions possibles
 * - GET /api/quotations/stats : Statistiques détaillées
 * - GET /api/quotations/previous-concretization : Taux N-1
 */
@RestController
@RequestMapping("/api/quotations")
@CrossOrigin(origins = { "http://localhost", "http://10.0.2.2", "http://optitop.local" })
@Tag(name = "Devis (QuotationController)", description = "Gestion des devis optiques et de leurs statistiques")
public class QuotationController {

    // ===== INJECTION DES DÉPENDANCES =====

    /**
     * Logger pour tracer les opérations et gérer les erreurs
     */
    private static final Logger logger = LoggerFactory.getLogger(QuotationController.class);

    /**
     * Repository pour l'accès aux données des devis
     * Gère les requêtes sur les quotations et leurs statistiques
     */
    @Autowired
    private QuotationsRepository quotationsRepository;

    /**
     * Service métier pour les opérations complexes sur les devis
     * Gère la logique métier et les opérations batch
     */
    @Autowired
    private QuotationService quotationService;

    // ===== ENDPOINTS DE RÉCUPÉRATION =====

    /**
     * Récupère les devis non validés avec gestion des droits utilisateur
     * 
     * Applique un filtrage selon le rôle :
     * - Manager : accès à tous les devis non validés
     * - Collaborateur : accès uniquement à ses propres devis
     * 
     * @param startDate     Date de début de la période (format YYYY-MM-DD)
     * @param endDate       Date de fin de la période (format YYYY-MM-DD)
     * @param userRole      Rôle de l'utilisateur (manager/collaborator)
     * @param userSellerRef Référence vendeur (obligatoire pour collaborateurs)
     * @return ResponseEntity avec la liste des devis non validés filtrés
     * 
     *         Codes de retour :
     *         - 200 : Devis récupérés avec succès
     *         - 500 : Erreur lors de la récupération
     */
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
            // Conversion des dates string en LocalDate
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            // Récupération des devis selon les droits utilisateur
            List<Quotations> quotations;
            if ("collaborator".equalsIgnoreCase(userRole) && userSellerRef != null) {
                logger.debug("Recherche des devis pour le collaborateur: {}", userSellerRef);
                quotations = quotationsRepository.findUnvalidatedByDateBetweenAndSellerRef(start, end, userSellerRef);
            } else {
                logger.debug("Recherche de tous les devis non validés");
                quotations = quotationsRepository.findUnvalidatedByDateBetween(start, end);
            }

            // Conversion en DTOs pour l'API
            List<QuotationDTO> dtos = quotations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            // Retour des données de succès
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des devis non validés", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Liste toutes les actions possibles applicables aux devis
     * 
     * @return ResponseEntity avec la map des actions (clé=code, valeur=libellé)
     * 
     *         Codes de retour :
     *         - 200 : Actions récupérées avec succès
     *         - 500 : Erreur lors de la récupération
     */
    @Operation(summary = "Récupérer les actions possibles", description = "Liste toutes les actions possibles pour un devis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des actions récupérée", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"VOIR_OPTICIEN\": \"Voir opticien\", \"NON_VALIDE\": \"Non validé\"}")))
    })
    @GetMapping("/actions")
    public ResponseEntity<Map<String, String>> getActions() {
        try {
            // Transformation de l'enum en Map pour l'API
            Map<String, String> actions = Arrays.stream(QuotationAction.values())
                    .collect(Collectors.toMap(
                            Enum::name,
                            QuotationAction::getValue));

            // Retour des données de succès
            return ResponseEntity.ok(actions);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des actions possibles", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== ENDPOINTS DE MODIFICATION =====

    /**
     * Met à jour un lot de devis en une seule opération
     * 
     * Permet de modifier en batch les actions et commentaires de plusieurs devis
     * Optimise les performances en évitant les appels multiples
     * 
     * @param updates Liste des modifications à appliquer
     * @return ResponseEntity vide si succès, message d'erreur si échec
     * 
     *         Codes de retour :
     *         - 200 : Mise à jour effectuée avec succès
     *         - 500 : Erreur lors de la mise à jour
     */
    @Operation(summary = "Mettre à jour un lot de devis", description = "Met à jour les actions et commentaires d'un lot de devis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mise à jour effectuée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la mise à jour", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Erreur lors de la mise à jour des devis\"}")))
    })
    @PutMapping("/batch-update")
    public ResponseEntity<?> batchUpdate(@RequestBody List<QuotationUpdateDTO> updates) {
        try {
            // Délégation au service métier pour la logique complexe
            quotationService.batchUpdate(updates);

            // Retour de succès sans contenu
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour des devis", e);
            return ResponseEntity.internalServerError().body("Erreur lors de la mise à jour des devis");
        }
    }

    // ===== ENDPOINTS STATISTIQUES =====

    /**
     * Calcule les statistiques complètes des devis sur une période
     * 
     * Fournit :
     * - Statistiques globales : total, validés, non validés
     * - Statistiques par vendeur : répartition des devis par commercial
     * 
     * @param startDate Date de début de la période (format YYYY-MM-DD)
     * @param endDate   Date de fin de la période (format YYYY-MM-DD)
     * @return ResponseEntity avec les statistiques détaillées
     * 
     *         Codes de retour :
     *         - 200 : Statistiques calculées avec succès
     *         - 500 : Erreur lors du calcul
     */
    @Operation(summary = "Statistiques des devis", description = "Récupère les statistiques globales et par vendeur des devis sur une période")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques calculées avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuotationStatsDTO.class)))
    })
    @GetMapping("/stats")
    public ResponseEntity<QuotationStatsDTO> getQuotationStats(
            @Parameter(description = "Date de début (YYYY-MM-DD)") @RequestParam String startDate,
            @Parameter(description = "Date de fin (YYYY-MM-DD)") @RequestParam String endDate) {

        try {
            // Conversion des dates string en LocalDate
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            // Calcul des statistiques globales
            Long total = quotationsRepository.countQuotationsBetween(start, end);
            Long validated = quotationsRepository.countValidatedQuotationsBetween(start, end);
            Long unvalidated = quotationsRepository.countUnvalidatedQuotationsBetween(start, end);

            // Création du DTO de statistiques globales
            QuotationStatsDTO stats = new QuotationStatsDTO(total, validated, unvalidated);

            // Récupération et transformation des statistiques par vendeur
            List<Object[]> sellerStats = quotationsRepository.getSellerStats(start, end);
            List<SellerStatsDTO> sellerStatsDTOs = sellerStats.stream()
                    .map(row -> new SellerStatsDTO(
                            (String) row[0], // sellerRef
                            (Long) row[1], // total
                            (Long) row[2] // unvalidated
                    ))
                    .collect(Collectors.toList());

            // Ajout des statistiques vendeurs au DTO global
            stats.setSellerStats(sellerStatsDTOs);

            // Retour des données de succès
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Erreur lors du calcul des statistiques", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Calcule le taux de concrétisation pour la même période l'année précédente
     * 
     * Permet la comparaison avec l'année N-1 pour analyser les tendances
     * Utilisé pour les indicateurs de performance et benchmarks
     * 
     * @param startDate Date de début de la période actuelle (format YYYY-MM-DD)
     * @param endDate   Date de fin de la période actuelle (format YYYY-MM-DD)
     * @return ResponseEntity avec le taux de concrétisation N-1 en pourcentage
     * 
     *         Codes de retour :
     *         - 200 : Taux calculé avec succès
     *         - 500 : Erreur lors du calcul
     */
    @Operation(summary = "Taux de concrétisation N-1", description = "Calcule le taux de concrétisation pour la même période l'année précédente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Taux calculé avec succès", content = @Content(mediaType = "application/json", schema = @Schema(type = "number", format = "double", example = "75.5")))
    })
    @GetMapping("/previous-concretization")
    public ResponseEntity<Double> getPreviousConcretizationRate(
            @Parameter(description = "Date de début (YYYY-MM-DD)") @RequestParam String startDate,
            @Parameter(description = "Date de fin (YYYY-MM-DD)") @RequestParam String endDate) {

        try {
            // Calcul des dates pour la période N-1
            LocalDate start = LocalDate.parse(startDate).minusYears(1);
            LocalDate end = LocalDate.parse(endDate).minusYears(1);

            // Récupération du taux depuis le repository
            Double rate = quotationsRepository.getPreviousConcretizationRate(start, end);

            // Retour du taux (0.0 si aucune donnée)
            return ResponseEntity.ok(rate != null ? rate : 0.0);
        } catch (Exception e) {
            logger.error("Erreur lors du calcul du taux de concrétisation N-1", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== MÉTHODES UTILITAIRES =====

    /**
     * Convertit une entité Quotations en DTO pour l'API
     * 
     * Gère la transformation des données métier en format API :
     * - Récupération sécurisée du vendeur associé
     * - Transformation de l'enum action en libellé
     * - Gestion des valeurs nulles
     * 
     * @param quotation Entité quotation à convertir
     * @return DTO formaté pour l'API
     */
    private QuotationDTO convertToDTO(Quotations quotation) {
        // Création du DTO avec l'ID
        QuotationDTO dto = new QuotationDTO(quotation.getId());
        dto.setDate(quotation.getDate());

        // Récupération sécurisée du vendeur
        if (quotation.getSeller() != null) {
            dto.setSeller(quotation.getSeller().getSellerRef());
        } else {
            dto.setSeller("Non assigné");
        }

        // Affectation des autres propriétés
        dto.setClient(quotation.getClient());
        dto.setAction(quotation.getAction() != null ? quotation.getAction().getValue() : null);
        dto.setComment(quotation.getComment());

        return dto;
    }
}