package com.optitop.optitop_api.controller;

// ===== IMPORTS SPRING FRAMEWORK =====
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

// ===== IMPORTS SERVICES =====
import com.optitop.optitop_api.service.SalesService;

// ===== IMPORTS SWAGGER (DOCUMENTATION API) =====
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

// ===== IMPORTS UTILITAIRES =====
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur REST pour l'importation des données de vente
 * 
 * Gère l'importation de fichiers CSV contenant les données de vente :
 * - Réception et validation des fichiers CSV
 * - Traitement de l'encodage UTF-8 avec gestion BOM
 * - Lecture par lots des données de vente
 * - Délégation du traitement métier au service
 * 
 * Format de fichier attendu :
 * Date, C., Num client, Client, Référence, Famille, Quantité, Total TTC,
 * Total facture/devis, Paire, Status, Vendeur
 * 
 * Utilisé pour alimenter le système avec les données de vente
 * provenant des logiciels de caisse ou ERP externes.
 */
@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = { "http://localhost", "http://10.0.2.2", "http://optitop.local" })
@Tag(name = "Import des données de ventes (SalesController)", description = "Import des données de ventes (lignes de factures et de devis) depuis un fichier CSV")
public class SalesController {

    // ===== INJECTION DES DÉPENDANCES =====

    /**
     * Logger pour tracer les opérations d'import et gérer les erreurs
     */
    private static final Logger logger = LoggerFactory.getLogger(SalesController.class);

    /**
     * Service métier pour le traitement des données de vente
     * Gère la logique d'import et la persistence en base
     */
    @Autowired
    private SalesService salesService;

    // ===== ENDPOINTS D'IMPORTATION =====

    /**
     * Importe un fichier CSV contenant les données de vente
     * 
     * Traite les fichiers CSV avec gestion complète de l'encodage UTF-8
     * et suppression automatique du BOM (Byte Order Mark).
     * Le fichier est lu ligne par ligne et traité par lots pour optimiser
     * les performances et la mémoire.
     * 
     * @param file Fichier CSV à importer (format multipart/form-data)
     * @return ResponseEntity vide si succès, message d'erreur si échec
     * 
     *         Format attendu du CSV :
     *         - Encodage : UTF-8 (avec ou sans BOM)
     *         - Séparateur : virgule (,)
     *         - Première ligne : en-têtes de colonnes
     *         - Colonnes requises : Date, C., Num client, Client, Référence,
     *         Famille, Quantité, Total TTC, Total facture/devis, Paire, Status,
     *         Vendeur
     * 
     *         Codes de retour :
     *         - 200 : Import réussi
     *         - 400 : Fichier invalide ou mal formaté
     *         - 500 : Erreur serveur lors de l'import
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
            @Parameter(description = "Fichier CSV à importer (encodage UTF-8)", required = true, schema = @Schema(type = "string", format = "binary")) @RequestParam MultipartFile file) {

        try {
            // Début du traitement avec logging
            logger.info("Début de l'import du fichier: {}", file.getOriginalFilename());

            // Lecture et traitement du fichier CSV
            List<String> batch = readCsvFile(file);

            // Délégation du traitement métier au service
            salesService.processBatch(batch);

            // Succès de l'import
            logger.info("Import réussi pour le fichier: {}", file.getOriginalFilename());
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            // Gestion d'erreur avec logging détaillé
            logger.error("Erreur lors de l'import du fichier: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(500)
                    .body("Erreur lors de l'import du fichier: " + e.getMessage());
        }
    }

    // ===== MÉTHODES UTILITAIRES =====

    /**
     * Lit un fichier CSV avec gestion de l'encodage UTF-8 et du BOM
     * 
     * Traite le fichier ligne par ligne en gérant :
     * - L'encodage UTF-8 pour les caractères accentués
     * - La suppression du BOM (Byte Order Mark) si présent
     * - La lecture complète du fichier en mémoire
     * 
     * @param file Fichier CSV à lire
     * @return Liste des lignes du fichier
     * @throws Exception si erreur de lecture
     */
    private List<String> readCsvFile(MultipartFile file) throws Exception {
        List<String> batch = new ArrayList<>();

        // Lecture du fichier avec encodage UTF-8
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), "UTF-8"))) {

            // Lecture de la première ligne avec suppression du BOM
            String line = br.readLine();
            if (line != null) {
                // Suppression du BOM (Byte Order Mark) si présent
                line = line.replace("\uFEFF", "");
                batch.add(line);
            }

            // Lecture des lignes suivantes
            while ((line = br.readLine()) != null) {
                batch.add(line);
            }
        }

        logger.debug("Fichier CSV lu avec succès: {} lignes", batch.size());
        return batch;
    }
}