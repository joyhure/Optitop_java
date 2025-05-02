package com.optitop.optitop_api.controller;

import com.optitop.optitop_api.dto.LoginRequestDTO;
// Modèles et repositories
import com.optitop.optitop_api.model.User;
import com.optitop.optitop_api.repository.UserRepository;
import com.optitop.optitop_api.service.PasswordService;

// Spring Framework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;

// Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost", "http://10.0.2.2", "http://optitop.local" })
@Tag(name = "Authentification (AuthController)", description = "Gestion de l'authentification des utilisateurs (connexion/déconnexion)")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur avec ses identifiants et retourne ses informations si valides")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Identifiants de connexion", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginRequestDTO.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentification réussie", content = @Content(mediaType = "application/json", schema = @Schema(description = "Informations de l'utilisateur connecté", requiredProperties = {
                    "id", "login", "role" }))),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides", content = @Content(mediaType = "application/json", schema = @Schema(description = "Message d'erreur", example = "{\"error\": \"Identifiants incorrects\"}"))),
            @ApiResponse(responseCode = "400", description = "Requête invalide (champs manquants)", content = @Content(mediaType = "application/json", schema = @Schema(description = "Messages de validation", example = "{\"login\": \"L'identifiant est obligatoire\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDTO loginRequest) {
        User user = userRepository.findByLogin(loginRequest.getLogin());

        if (user != null && passwordService.verifyPassword(loginRequest.getPassword(), user.getPassword())) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("firstname", user.getFirstname());
            response.put("role", user.getRole());
            response.put("seller_ref", user.getLogin());

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Identifiants incorrects"));
    }

    @Operation(summary = "Déconnexion utilisateur", description = "Déconnecte l'utilisateur de sa session actuelle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie", content = @Content(mediaType = "application/json", schema = @Schema(description = "Confirmation de déconnexion", example = "{\"success\": true}"))),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la déconnexion", content = @Content(mediaType = "application/json", schema = @Schema(description = "Message d'erreur", example = "{\"error\": \"Erreur lors de la déconnexion\"}")))
    })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Boolean>> logout() {
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}
