package com.optitop.optitop_api.controller;

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

import com.optitop.optitop_api.dto.PendingAccountDTO;
import com.optitop.optitop_api.dto.PendingAccountDisplayDTO;
import com.optitop.optitop_api.model.PendingAccount;
import com.optitop.optitop_api.service.PendingAccountService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/pending-accounts")
@CrossOrigin(origins = "http://localhost")
@Tag(name = "Demandes sur les comptes (PendingAccountController)", description = "Gestion des demandes de création, modification et suppression de comptes")
public class PendingAccountController {

    private final PendingAccountService pendingAccountService;

    public PendingAccountController(PendingAccountService pendingAccountService) {
        this.pendingAccountService = pendingAccountService;
    }

    @Operation(summary = "Créer une demande de compte", description = "Crée une nouvelle demande de création, modification ou suppression de compte")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Demande créée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{\"message\": \"Demande créée avec succès\"}"))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PendingAccountDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping
    public ResponseEntity<?> createPendingAccount(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Détails de la demande", required = true, content = @Content(schema = @Schema(implementation = PendingAccountDTO.class))) @Valid @RequestBody PendingAccountDTO dto,
            @Parameter(description = "Token d'authentification (Bearer {userId})", required = true) @RequestHeader("Authorization") String authHeader) {

        Integer userId = Integer.valueOf(authHeader.replace("Bearer ", ""));

        try {
            pendingAccountService.createPendingAccount(dto, userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Demande créée avec succès");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Erreur lors de la création de la demande");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @Operation(summary = "Valider une demande", description = "Valide une demande sur un compte (création, modification ou suppression). Réservé aux administrateurs.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Demande validée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé"),
            @ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    @PostMapping("/validate/{id}")
    public ResponseEntity<?> validatePendingAccount(
            @Parameter(description = "ID de la demande à valider") @PathVariable Integer id,
            @Parameter(description = "Token d'authentification (Bearer {userId})") @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = Integer.valueOf(authHeader.replace("Bearer ", ""));
            pendingAccountService.validatePendingAccount(id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "Rejeter une demande", description = "Rejette une demande sur un compte. Réservé aux administrateurs.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Demande rejetée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé"),
            @ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectPendingAccount(
            @Parameter(description = "ID de la demande à rejeter") @PathVariable Integer id,
            @Parameter(description = "Token d'authentification (Bearer {userId})") @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = Integer.valueOf(authHeader.replace("Bearer ", ""));
            pendingAccountService.rejectPendingAccount(id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "Lister toutes les demandes", description = "Récupère la liste de toutes les demandes en attente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des demandes récupérée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PendingAccountDisplayDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping
    public ResponseEntity<List<PendingAccountDisplayDTO>> getAllPendingAccounts() {
        try {
            List<PendingAccount> pendingAccounts = pendingAccountService.getAllPendingAccounts();

            List<PendingAccountDisplayDTO> displayDtos = pendingAccounts.stream()
                    .map(account -> new PendingAccountDisplayDTO(
                            account.getId(),
                            account.getLastname(),
                            account.getFirstname(),
                            account.getEmail(),
                            account.getLogin(),
                            account.getRole(),
                            account.getRequestType().name(),
                            account.getCreatedAt(),
                            account.getCreatedBy().getLogin()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(displayDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
}