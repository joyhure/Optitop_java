package com.optitop.optitop_api.controller;

// ===== IMPORTS SPRING FRAMEWORK =====
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// ===== IMPORTS DTOs =====
import com.optitop.optitop_api.dto.PasswordChangeRequestDTO;
import com.optitop.optitop_api.dto.UserDisplayDTO;

// ===== IMPORTS MODÈLES =====
import com.optitop.optitop_api.model.User;

// ===== IMPORTS REPOSITORIES =====
import com.optitop.optitop_api.repository.PendingAccountRepository;
import com.optitop.optitop_api.repository.UserRepository;

// ===== IMPORTS SERVICES =====
import com.optitop.optitop_api.service.PasswordService;

// ===== IMPORTS SWAGGER (DOCUMENTATION API) =====
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// ===== IMPORTS UTILITAIRES =====
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour la gestion des utilisateurs
 * 
 * Gère les opérations liées aux utilisateurs du système Optitop :
 * - Consultation des informations utilisateur
 * - Recherche d'utilisateurs par critères
 * - Modification des mots de passe
 * - Récupération de listes pour les interfaces
 * 
 * Sécurité :
 * - Validation des critères de mot de passe (12 caractères, majuscules,
 * minuscules, chiffres, symboles)
 * - Contrôle d'accès selon les rôles utilisateur
 * - Hachage sécurisé des mots de passe
 * 
 * Utilisé par :
 * - Interface de gestion des comptes (administration)
 * - Profils utilisateur (consultation/modification)
 * - Formulaires de demandes de comptes
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = { "http://localhost", "http://10.0.2.2", "http://optitop.local" })
@Tag(name = "Utilisateurs (UserController)", description = "Gestion des utilisateurs et de leurs informations")
public class UserController {

    // ===== INJECTION DES DÉPENDANCES =====

    /**
     * Repository pour la gestion des utilisateurs
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Service de gestion des mots de passe (hachage sécurisé)
     */
    @Autowired
    private PasswordService passwordService;

    /**
     * Repository pour la gestion des demandes de comptes
     * Utilisé pour vérifier les logins disponibles
     */
    @Autowired
    private PendingAccountRepository pendingAccountRepository;

    // ===== ENDPOINTS DE CONSULTATION UTILISATEUR =====

    /**
     * Récupère un utilisateur par son login
     * 
     * @param login Identifiant de connexion de l'utilisateur
     * @return ResponseEntity avec l'utilisateur ou 404 si non trouvé
     */
    @Operation(summary = "Récupérer un utilisateur par login", description = "Retourne les informations d'un utilisateur à partir de son login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @GetMapping("/login/{login}")
    public ResponseEntity<User> getUserByLogin(
            @Parameter(description = "Login de l'utilisateur") @PathVariable String login) {
        User user = userRepository.findByLogin(login);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Récupère le nom de famille d'un utilisateur
     * 
     * @param id Identifiant de l'utilisateur
     * @return ResponseEntity avec le nom de famille ou 404 si non trouvé
     */
    @Operation(summary = "Récupérer le nom de famille", description = "Retourne le nom de famille d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nom trouvé", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @GetMapping("/{id}/lastname")
    public ResponseEntity<String> getUserLastname(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return ResponseEntity.ok(user.getLastname());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Récupère la date de création du compte utilisateur
     * 
     * @param id Identifiant de l'utilisateur
     * @return ResponseEntity avec la date de création ou 404 si non trouvé
     */
    @Operation(summary = "Récupérer la date de création du compte utilisateur", description = "Retourne la date de création du compte utilisateur")
    @GetMapping("/{id}/created-at")
    public ResponseEntity<String> getUserCreatedAt(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && user.getCreatedAt() != null) {
            return ResponseEntity.ok(user.getCreatedAt().toString());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Récupère l'adresse email d'un utilisateur
     * 
     * @param id Identifiant de l'utilisateur
     * @return ResponseEntity avec l'email ou 404 si non trouvé
     */
    @Operation(summary = "Récupérer l'email", description = "Retourne l'adresse email d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email trouvé", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "user@optitop.fr"))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @GetMapping("/{id}/email")
    public ResponseEntity<String> getUserEmail(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return ResponseEntity.ok(user.getEmail());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Récupère le login d'un utilisateur
     * 
     * @param id Identifiant de l'utilisateur
     * @return ResponseEntity avec le login ou 404 si non trouvé
     */
    @Operation(summary = "Récupérer le login", description = "Retourne l'identifiant de connexion d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login trouvé", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "jdupont"))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @GetMapping("/{id}/login")
    public ResponseEntity<String> getUserLogin(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return ResponseEntity.ok(user.getLogin());
        }
        return ResponseEntity.notFound().build();
    }

    // ===== ENDPOINTS DE LISTES ET DONNÉES AUXILIAIRES =====

    /**
     * Récupère la liste des logins disponibles pour création de demandes
     * 
     * Filtre les logins qui n'ont pas de demande de compte en cours
     * pour éviter les doublons dans le système de demandes.
     * 
     * @return ResponseEntity avec la liste des logins ou 204 si vide
     */
    @Operation(summary = "Liste des logins disponibles à création de demande sur le compte", description = "Récupère la liste des logins qui n'ont pas de demande en cours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des logins récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(type = "string", example = "jdupont")))),
            @ApiResponse(responseCode = "204", description = "Aucun login disponible")
    })
    @GetMapping("/logins")
    public ResponseEntity<List<String>> getAllLogins() {
        // Récupérer les logins qui ont une demande en cours
        List<String> pendingLogins = pendingAccountRepository.findAllLogins();

        // Récupérer tous les logins et filtrer
        List<String> availableLogins = userRepository.findAllLogins()
                .stream()
                .filter(login -> !pendingLogins.contains(login))
                .collect(Collectors.toList());

        return availableLogins.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(availableLogins);
    }

    /**
     * Récupère la liste de tous les utilisateurs
     * 
     * Retourne les informations de base de tous les utilisateurs
     * sous forme de DTOs optimisés pour l'affichage.
     * 
     * @return ResponseEntity avec la liste des utilisateurs ou erreur 500
     */
    @Operation(summary = "Liste des utilisateurs", description = "Récupère la liste de tous les utilisateurs avec leurs informations de base")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDisplayDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération")
    })
    @GetMapping("/all")
    public ResponseEntity<List<UserDisplayDTO>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            List<UserDisplayDTO> userDtos = users.stream()
                    .map(user -> new UserDisplayDTO(
                            user.getId(),
                            user.getLogin(),
                            user.getRole(),
                            user.getLastname(),
                            user.getFirstname(),
                            user.getEmail(),
                            user.getCreatedAt()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===== ENDPOINTS DE MODIFICATION =====

    /**
     * Change le mot de passe d'un utilisateur
     * 
     * Valide les critères de sécurité et met à jour le mot de passe
     * avec un hachage sécurisé.
     * 
     * Critères de sécurité :
     * - Au moins 12 caractères
     * - Au moins une majuscule
     * - Au moins une minuscule
     * - Au moins un chiffre
     * - Au moins un caractère spécial (@$!%*?&)
     * 
     * @param id      Identifiant de l'utilisateur
     * @param request Objet contenant l'ancien et le nouveau mot de passe
     * @return ResponseEntity avec message de succès ou erreur
     */
    @Operation(summary = "Changer le mot de passe", description = "Permet à un utilisateur de modifier son mot de passe en respectant les critères de sécurité")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mot de passe modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Critères de sécurité non respectés", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Le nouveau mot de passe ne respecte pas les critères de sécurité"))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @PostMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Ancien et nouveau mot de passe", required = true, content = @Content(schema = @Schema(implementation = PasswordChangeRequestDTO.class))) @RequestBody PasswordChangeRequestDTO request) {

        // Validation des données
        if (request.getCurrentPassword() == null || request.getNewPassword() == null) {
            return ResponseEntity.badRequest().body("Les mots de passe ne peuvent pas être vides");
        }

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Validation du nouveau mot de passe
        if (!isValidPassword(request.getNewPassword())) {
            return ResponseEntity.badRequest()
                    .body("Le nouveau mot de passe ne respecte pas les critères de sécurité");
        }

        // Hachage et mise à jour du mot de passe
        String hashedPassword = passwordService.hashPassword(request.getNewPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        return ResponseEntity.ok("Mot de passe modifié avec succès");
    }

    // ===== MÉTHODES UTILITAIRES PRIVÉES =====

    /**
     * Valide les critères de sécurité d'un mot de passe
     * 
     * @param password Mot de passe à valider
     * @return true si le mot de passe respecte tous les critères, false sinon
     */
    private boolean isValidPassword(String password) {
        // Au moins 12 caractères
        if (password.length() < 12)
            return false;

        // Au moins une majuscule
        if (!password.matches(".*[A-Z].*"))
            return false;

        // Au moins une minuscule
        if (!password.matches(".*[a-z].*"))
            return false;

        // Au moins un chiffre
        if (!password.matches(".*\\d.*"))
            return false;

        // Au moins un caractère spécial
        if (!password.matches(".*[@$!%*?&].*"))
            return false;

        return true;
    }
}
