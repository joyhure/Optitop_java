package com.optitop.optitop_api.controller;

// ===== IMPORTS SPRING FRAMEWORK =====
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// ===== IMPORTS DTOs =====
import com.optitop.optitop_api.dto.PendingAccountDTO;
import com.optitop.optitop_api.dto.PendingAccountDisplayDTO;

// ===== IMPORTS MODÈLES =====
import com.optitop.optitop_api.model.PendingAccount;

// ===== IMPORTS SERVICES =====
import com.optitop.optitop_api.service.PendingAccountService;

// ===== IMPORTS VALIDATION =====
import jakarta.validation.Valid;

// ===== IMPORTS UTILITAIRES =====
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// ===== IMPORTS SWAGGER (DOCUMENTATION API) =====
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

// ===== IMPORTS LOGGING =====
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contrôleur REST pour la gestion des demandes de comptes utilisateurs
 * 
 * Gère le cycle de vie complet des demandes de comptes :
 * - Création de demandes (ajout, modification, suppression)
 * - Validation et rejet des demandes par les administrateurs
 * - Consultation des demandes en attente
 * - Traitement des workflows d'approbation
 * 
 * Sécurité :
 * - Authentification Bearer token obligatoire
 * - Autorisation par rôles (admin requis pour validation/rejet)
 * - Validation des données entrantes avec Bean Validation
 * 
 * Types de demandes supportées :
 * - CREATION : Nouveau compte utilisateur
 * - MODIFICATION : Mise à jour compte existant
 * - SUPPRESSION : Désactivation compte utilisateur
 * 
 * Utilisé par l'interface d'administration pour la gestion
 * centralisée des comptes utilisateurs du système Optitop.
 */
@RestController
@RequestMapping("/api/pending-accounts")
@CrossOrigin(origins = { "http://localhost", "http://10.0.2.2", "http://optitop.local" })
@Tag(name = "Demandes sur les comptes (PendingAccountController)", description = "Gestion des demandes de création, modification et suppression de comptes")
public class PendingAccountController {

    // ===== INJECTION DES DÉPENDANCES =====

    /**
     * Logger pour tracer les opérations de gestion des demandes
     */
    private static final Logger logger = LoggerFactory.getLogger(PendingAccountController.class);

    /**
     * Service métier pour la gestion des demandes de comptes
     * Gère la logique d'approbation et les workflows
     */
    private final PendingAccountService pendingAccountService;

    // ===== CONSTRUCTEUR =====

    /**
     * Constructeur avec injection de dépendance
     * 
     * @param pendingAccountService Service de gestion des demandes de comptes
     */
    public PendingAccountController(PendingAccountService pendingAccountService) {
        this.pendingAccountService = pendingAccountService;
    }

    // ===== ENDPOINTS DE GESTION DES DEMANDES =====

    /**
     * Crée une nouvelle demande de compte utilisateur
     * 
     * Permet aux utilisateurs autorisés de soumettre des demandes de :
     * - Création de nouveaux comptes
     * - Modification de comptes existants
     * - Suppression de comptes
     * 
     * La demande est soumise à un workflow d'approbation nécessitant
     * une validation par un administrateur avant traitement.
     * 
     * @param dto        Données de la demande (type, informations utilisateur)
     * @param authHeader Token d'authentification Bearer
     * @return ResponseEntity avec message de confirmation ou erreur
     * 
     *         Codes de retour :
     *         - 201 : Demande créée avec succès
     *         - 400 : Données invalides (validation échouée)
     *         - 409 : Conflit (demande déjà existante)
     *         - 500 : Erreur serveur lors de la création
     */
    @Operation(summary = "Créer une demande de compte", description = "Crée une nouvelle demande de création, modification ou suppression de compte. "
            +
            "La demande sera soumise à validation par un administrateur.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Demande créée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{\"message\": \"Demande créée avec succès\"}"))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PendingAccountDTO.class))),
            @ApiResponse(responseCode = "409", description = "Demande déjà existante", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{\"message\": \"Une demande pour cet utilisateur est déjà en cours\"}"))),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping
    public ResponseEntity<?> createPendingAccount(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Détails de la demande de compte", required = true, content = @Content(schema = @Schema(implementation = PendingAccountDTO.class))) @Valid @RequestBody PendingAccountDTO dto,
            @Parameter(description = "Token d'authentification (Bearer {userId})", required = true) @RequestHeader("Authorization") String authHeader) {

        try {
            // Extraction de l'ID utilisateur depuis le token
            Integer userId = extractUserIdFromAuthHeader(authHeader);

            logger.info("Création demande de compte par utilisateur {}: type={}, login={}",
                    userId, dto.getRequestType(), dto.getLogin());

            // Création de la demande via le service
            pendingAccountService.createPendingAccount(dto, userId);

            // Réponse de succès
            Map<String, String> response = createSuccessResponse("Demande créée avec succès");
            logger.info("Demande de compte créée avec succès pour l'utilisateur {}", userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            // Erreur de validation ou conflit
            logger.warn("Erreur lors de la création de demande: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("Erreur lors de la création de la demande", e.getMessage()));

        } catch (Exception e) {
            // Erreur serveur inattendue
            logger.error("Erreur serveur lors de la création de demande", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur serveur lors de la création de la demande", e.getMessage()));
        }
    }

    /**
     * Valide une demande de compte en attente
     * 
     * Endpoint réservé aux administrateurs pour approuver les demandes.
     * La validation déclenche le traitement effectif de la demande :
     * - Création du compte utilisateur
     * - Modification des informations
     * - Suppression/désactivation du compte
     * 
     * @param id         Identifiant de la demande à valider
     * @param authHeader Token d'authentification Bearer (admin requis)
     * @return ResponseEntity vide si succès, erreur si échec
     * 
     *         Codes de retour :
     *         - 200 : Demande validée avec succès
     *         - 403 : Accès non autorisé (rôle admin requis)
     *         - 404 : Demande non trouvée
     *         - 500 : Erreur lors du traitement
     */
    @Operation(summary = "Valider une demande", description = "Valide une demande sur un compte (création, modification ou suppression). "
            +
            "Réservé aux administrateurs. Déclenche le traitement effectif de la demande.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Demande validée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé - rôle administrateur requis"),
            @ApiResponse(responseCode = "404", description = "Demande non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur lors du traitement de la demande")
    })
    @PostMapping("/validate/{id}")
    public ResponseEntity<?> validatePendingAccount(
            @Parameter(description = "ID de la demande à valider") @PathVariable Integer id,
            @Parameter(description = "Token d'authentification (Bearer {userId})") @RequestHeader("Authorization") String authHeader) {

        try {
            // Extraction de l'ID utilisateur depuis le token
            Integer userId = extractUserIdFromAuthHeader(authHeader);

            logger.info("Validation demande {} par administrateur {}", id, userId);

            // Validation de la demande via le service
            pendingAccountService.validatePendingAccount(id, userId);

            logger.info("Demande {} validée avec succès par {}", id, userId);
            return ResponseEntity.ok().build();

        } catch (SecurityException e) {
            // Erreur d'autorisation
            logger.warn("Tentative de validation non autorisée pour demande {} par utilisateur {}",
                    id, extractUserIdFromAuthHeader(authHeader));
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Accès non autorisé",
                            "Seuls les administrateurs peuvent valider les demandes"));

        } catch (IllegalArgumentException e) {
            // Demande non trouvée
            logger.warn("Tentative de validation d'une demande inexistante: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Demande non trouvée", e.getMessage()));

        } catch (Exception e) {
            // Erreur serveur
            logger.error("Erreur lors de la validation de la demande {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la validation", e.getMessage()));
        }
    }

    /**
     * Rejette une demande de compte en attente
     * 
     * Endpoint réservé aux administrateurs pour refuser les demandes.
     * Le rejet supprime définitivement la demande sans traitement.
     * 
     * @param id         Identifiant de la demande à rejeter
     * @param authHeader Token d'authentification Bearer (admin requis)
     * @return ResponseEntity vide si succès, erreur si échec
     * 
     *         Codes de retour :
     *         - 200 : Demande rejetée avec succès
     *         - 403 : Accès non autorisé (rôle admin requis)
     *         - 404 : Demande non trouvée
     *         - 500 : Erreur lors du traitement
     */
    @Operation(summary = "Rejeter une demande", description = "Rejette une demande sur un compte. Réservé aux administrateurs. "
            +
            "La demande sera supprimée définitivement.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Demande rejetée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé - rôle administrateur requis"),
            @ApiResponse(responseCode = "404", description = "Demande non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur lors du traitement de la demande")
    })
    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectPendingAccount(
            @Parameter(description = "ID de la demande à rejeter") @PathVariable Integer id,
            @Parameter(description = "Token d'authentification (Bearer {userId})") @RequestHeader("Authorization") String authHeader) {

        try {
            // Extraction de l'ID utilisateur depuis le token
            Integer userId = extractUserIdFromAuthHeader(authHeader);

            logger.info("Rejet demande {} par administrateur {}", id, userId);

            // Rejet de la demande via le service
            pendingAccountService.rejectPendingAccount(id, userId);

            logger.info("Demande {} rejetée avec succès par {}", id, userId);
            return ResponseEntity.ok().build();

        } catch (SecurityException e) {
            // Erreur d'autorisation
            logger.warn("Tentative de rejet non autorisée pour demande {} par utilisateur {}",
                    id, extractUserIdFromAuthHeader(authHeader));
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Accès non autorisé",
                            "Seuls les administrateurs peuvent rejeter les demandes"));

        } catch (IllegalArgumentException e) {
            // Demande non trouvée
            logger.warn("Tentative de rejet d'une demande inexistante: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Demande non trouvée", e.getMessage()));

        } catch (Exception e) {
            // Erreur serveur
            logger.error("Erreur lors du rejet de la demande {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors du rejet", e.getMessage()));
        }
    }

    /**
     * Récupère toutes les demandes de comptes en attente
     * 
     * Retourne la liste complète des demandes non traitées pour
     * affichage dans l'interface d'administration. Les données
     * sont formatées via des DTOs d'affichage optimisés.
     * 
     * @return Liste des demandes en attente ou liste vide si erreur
     * 
     *         Codes de retour :
     *         - 200 : Liste récupérée avec succès
     *         - 500 : Erreur serveur (retourne liste vide)
     */
    @Operation(summary = "Lister toutes les demandes", description = "Récupère la liste de toutes les demandes en attente pour affichage "
            +
            "dans l'interface d'administration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des demandes récupérée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PendingAccountDisplayDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur serveur - retourne liste vide")
    })
    @GetMapping
    public ResponseEntity<List<PendingAccountDisplayDTO>> getAllPendingAccounts() {
        try {
            logger.debug("Récupération de toutes les demandes en attente");

            // Récupération des demandes via le service
            List<PendingAccount> pendingAccounts = pendingAccountService.getAllPendingAccounts();

            // Transformation en DTOs d'affichage
            List<PendingAccountDisplayDTO> displayDtos = pendingAccounts.stream()
                    .map(this::convertToDisplayDTO)
                    .collect(Collectors.toList());

            logger.debug("Récupération réussie: {} demandes trouvées", displayDtos.size());
            return ResponseEntity.ok(displayDtos);

        } catch (Exception e) {
            // En cas d'erreur, retour d'une liste vide pour éviter de casser l'interface
            logger.error("Erreur lors de la récupération des demandes en attente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    // ===== MÉTHODES UTILITAIRES PRIVÉES =====

    /**
     * Extrait l'ID utilisateur depuis l'en-tête d'autorisation
     * 
     * @param authHeader En-tête Authorization au format "Bearer {userId}"
     * @return ID de l'utilisateur
     * @throws IllegalArgumentException si format invalide
     */
    private Integer extractUserIdFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token d'autorisation invalide");
        }

        try {
            return Integer.valueOf(authHeader.replace("Bearer ", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format de token invalide");
        }
    }

    /**
     * Convertit une entité PendingAccount en DTO d'affichage
     * 
     * @param account Entité à convertir
     * @return DTO formaté pour l'affichage
     */
    private PendingAccountDisplayDTO convertToDisplayDTO(PendingAccount account) {
        return new PendingAccountDisplayDTO(
                account.getId(),
                account.getLastname(),
                account.getFirstname(),
                account.getEmail(),
                account.getLogin(),
                account.getRole(),
                account.getRequestType().name(),
                account.getCreatedAt(),
                account.getCreatedBy().getLogin());
    }

    /**
     * Crée une réponse de succès standardisée
     * 
     * @param message Message de succès
     * @return Map contenant le message
     */
    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }

    /**
     * Crée une réponse d'erreur standardisée
     * 
     * @param message Message principal d'erreur
     * @param error   Détail de l'erreur
     * @return Map contenant message et erreur
     */
    private Map<String, String> createErrorResponse(String message, String error) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        response.put("error", error);
        return response;
    }
}