package com.optitop.optitop_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.optitop.optitop_api.dto.SellerDTO;
import com.optitop.optitop_api.service.SellerService;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/sellers")
@CrossOrigin(origins = "http://localhost")
@Tag(name = "Vendeurs (SellerController)", description = "Gestion des vendeurs disponibles pour création de compte")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

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