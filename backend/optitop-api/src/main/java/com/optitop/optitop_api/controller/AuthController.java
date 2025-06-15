package com.optitop.optitop_api.controller;

// ===== IMPORTS DTO ET MODÈLES =====
import com.optitop.optitop_api.dto.LoginRequestDTO;
import com.optitop.optitop_api.model.User;
import com.optitop.optitop_api.repository.UserRepository;
import com.optitop.optitop_api.service.PasswordService;

// ===== IMPORTS SPRING FRAMEWORK =====
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;

// ===== IMPORTS SWAGGER (DOCUMENTATION API) =====
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

// ===== IMPORTS UTILITAIRES =====
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion de l'authentification
 * 
 * Gère les opérations de connexion et déconnexion des utilisateurs
 * Endpoints disponibles :
 * - POST /api/auth/login : Authentification utilisateur
 * - POST /api/auth/logout : Déconnexion utilisateur
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost", "http://10.0.2.2", "http://optitop.local" })
@Tag(name = "Authentification (AuthController)", description = "Gestion de l'authentification des utilisateurs (connexion/déconnexion)")
public class AuthController {

    // ===== INJECTION DES DÉPENDANCES =====

    /**
     * Repository pour les opérations sur les utilisateurs
     * Permet l'accès aux données utilisateur en base
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Service de gestion des mots de passe
     * Gère le hachage et la vérification des mots de passe
     */
    @Autowired
    private PasswordService passwordService;

    // ===== ENDPOINTS D'AUTHENTIFICATION =====

    /**
     * Authentifie un utilisateur avec ses identifiants
     * 
     * @param loginRequest DTO contenant login et mot de passe
     * @return ResponseEntity avec les données utilisateur si succès, erreur si
     *         échec
     * 
     *         Codes de retour :
     *         - 200 : Authentification réussie
     *         - 401 : Identifiants incorrects
     *         - 400 : Requête invalide
     */
    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur avec ses identifiants et retourne ses informations si valides")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentification réussie", content = @Content(mediaType = "application/json", schema = @Schema(description = "Informations de l'utilisateur connecté", requiredProperties = {
                    "id", "login", "role" }))),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides", content = @Content(mediaType = "application/json", schema = @Schema(description = "Message d'erreur", example = "{\"error\": \"Identifiants incorrects\"}"))),
            @ApiResponse(responseCode = "400", description = "Requête invalide (champs manquants)", content = @Content(mediaType = "application/json", schema = @Schema(description = "Messages de validation", example = "{\"login\": \"L'identifiant est obligatoire\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequestDTO loginRequest) {

        // Recherche de l'utilisateur par son login
        User user = userRepository.findByLogin(loginRequest.getLogin());

        // Vérification de l'existence de l'utilisateur et validation du mot de passe
        if (user != null && passwordService.verifyPassword(loginRequest.getPassword(), user.getPassword())) {

            // Construction de la réponse avec les données utilisateur
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("firstname", user.getFirstname());
            response.put("role", user.getRole());
            response.put("seller_ref", user.getLogin());

            // Retour de la réponse de succès
            return ResponseEntity.ok(response);
        }

        // Retour d'erreur si authentification échouée
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Identifiants incorrets"));
    }

    /**
     * Déconnecte l'utilisateur de sa session actuelle
     * 
     * @return ResponseEntity avec confirmation de déconnexion
     * 
     *         Codes de retour :
     *         - 200 : Déconnexion réussie
     *         - 500 : Erreur lors de la déconnexion
     */
    @Operation(summary = "Déconnexion utilisateur", description = "Déconnecte l'utilisateur de sa session actuelle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie", content = @Content(mediaType = "application/json", schema = @Schema(description = "Confirmation de déconnexion", example = "{\"success\": true}"))),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la déconnexion", content = @Content(mediaType = "application/json", schema = @Schema(description = "Message d'erreur", example = "{\"error\": \"Erreur lors de la déconnexion\"}")))
    })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Boolean>> logout() {

        // Construction de la réponse de confirmation
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", true);

        // Retour de la confirmation de déconnexion
        return ResponseEntity.ok(response);
    }

    /**
     * Gère les erreurs de validation des arguments de méthode
     * 
     * @param ex Exception contenant les détails des erreurs de validation
     * @return ResponseEntity avec les messages d'erreur de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }
}
