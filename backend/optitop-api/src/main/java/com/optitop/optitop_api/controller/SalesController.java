package com.optitop.optitop_api.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.optitop.optitop_api.service.SalesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "http://localhost")
@Tag(name = "Import des données de ventes (SalesController)", description = "Import des données de ventes (lignes de factures et de devis) depuis un fichier CSV")
public class SalesController {

    private static final Logger logger = LoggerFactory.getLogger(SalesController.class);

    @Autowired
    private SalesService salesService;

    /**
     * Handles CSV file import for sales data
     * 
     * @param file CSV file to import
     * @return ResponseEntity with success or error message
     */
    @Operation(summary = "Importer un fichier de ventes", description = "Importe un fichier CSV contenant les données de ventes (factures et devis). "
            +
            "Le fichier doit contenir les colonnes suivantes : " +
            "Date, C., Num client, Client, Référence, Famille, Quantité, Total TTC, " +
            "Total facture/devis, Paire, Status, Vendeur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Import réussi", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{\"message\": \"Import réussi\"}"))),
            @ApiResponse(responseCode = "400", description = "Fichier invalide ou mal formaté", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{\"error\": \"Format de fichier invalide\"}"))),
            @ApiResponse(responseCode = "500", description = "Erreur lors de l'import", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{\"error\": \"Erreur lors de l'import: message d'erreur\"}")))
    })
    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ResponseEntity<?> importSales(
            @Parameter(description = "Fichier CSV à importer (encodage UTF-8)", required = true, schema = @Schema(type = "string", format = "binary")) @RequestParam("file") MultipartFile file) {
        try {
            logger.info("Starting import of file: {}", file.getOriginalFilename());

            List<String> batch = new ArrayList<>();

            // Read CSV file with UTF-8 encoding for proper accent handling
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), "UTF-8"))) {

                String line = br.readLine();
                if (line != null) {
                    // Remove BOM if present
                    line = line.replace("\uFEFF", "");
                    batch.add(line);
                }

                while ((line = br.readLine()) != null) {
                    batch.add(line);
                }
            }

            salesService.processBatch(batch);
            logger.info("Successfully imported file: {}", file.getOriginalFilename());
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Error while importing sales file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(500)
                    .body("Error while importing sales file: " + e.getMessage());
        }
    }
}