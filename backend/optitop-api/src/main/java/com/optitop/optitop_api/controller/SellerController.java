package com.optitop.optitop_api.controller;

// ===== IMPORTS SPRING FRAMEWORK =====
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// ===== IMPORTS DTOs =====
import com.optitop.optitop_api.dto.SellerDTO;

// ===== IMPORTS SERVICES =====
import com.optitop.optitop_api.service.SellerService;

// ===== IMPORTS SWAGGER (DOCUMENTATION API) =====
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// ===== IMPORTS UTILITAIRES =====
import java.util.List;

/**
 * Contrôleur REST pour la gestion des vendeurs
 * 
 * Gère les opérations liées aux vendeurs du système Optitop :
 * - Récupération des vendeurs disponibles pour création de compte
 * - Filtrage des vendeurs selon leur statut (avec/sans compte utilisateur)
 * 
 * Utilisé principalement pour :
 * - Interface de création de demandes de comptes
 * - Sélection des vendeurs éligibles à un compte utilisateur
 */
@RestController
@RequestMapping("/api/sellers")
@CrossOrigin(origins = { "http://localhost", "http://10.0.2.2", "http://optitop.local" })
@Tag(name = "Vendeurs (SellerController)", description = "Gestion des vendeurs disponibles pour création de compte")
public class SellerController {

    // ===== INJECTION DES DÉPENDANCES =====

    /**
     * Service de gestion des vendeurs
     */
    private final SellerService sellerService;

    // ===== CONSTRUCTEUR =====

    /**
     * Constructeur avec injection de dépendance
     * 
     * @param sellerService Service de gestion des vendeurs
     */
    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    // ===== ENDPOINTS DE GESTION DES VENDEURS =====

    /**
     * Récupère les vendeurs disponibles pour la création de compte
     * 
     * Retourne la liste des vendeurs qui n'ont pas encore de compte
     * utilisateur et qui n'ont pas de demande en cours.
     * 
     * @return ResponseEntity avec la liste des vendeurs ou 204 si vide
     */
    @Operation(summary = "Récupérer les vendeurs disponibles pour la création de compte", description = "Retourne la liste des vendeurs qui n'ont pas encore de compte utilisateur "
            +
            "et qui n'ont pas de demande en cours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendeurs trouvés avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SellerDTO.class))),
            @ApiResponse(responseCode = "204", description = "Aucun vendeur disponible pour création de compte", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    @GetMapping("/available-sellers")
    public ResponseEntity<List<SellerDTO>> getAvailableSellers() {
        List<SellerDTO> sellers = sellerService.findAvailableSellers();
        return sellers.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(sellers);
    }
}