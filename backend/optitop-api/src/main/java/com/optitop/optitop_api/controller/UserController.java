package com.optitop.optitop_api.controller;

import com.optitop.optitop_api.dto.PasswordChangeRequestDTO;
import com.optitop.optitop_api.dto.UserDisplayDTO;
import com.optitop.optitop_api.model.User;
import com.optitop.optitop_api.repository.UserRepository;
import com.optitop.optitop_api.service.PasswordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    @GetMapping("/login/{login}")
    public ResponseEntity<User> getUserByLogin(@PathVariable String login) {
        User user = userRepository.findByLogin(login);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/lastname")
    public ResponseEntity<String> getUserLastname(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return ResponseEntity.ok(user.getLastname());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/created-at")
    public ResponseEntity<String> getUserCreatedAt(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && user.getCreatedAt() != null) {
            return ResponseEntity.ok(user.getCreatedAt().toString());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/email")
    public ResponseEntity<String> getUserEmail(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return ResponseEntity.ok(user.getEmail());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/login")
    public ResponseEntity<String> getUserLogin(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return ResponseEntity.ok(user.getLogin());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/logins")
    public ResponseEntity<List<String>> getAllLogins() {
        List<String> logins = userRepository.findAllLogins();
        return logins.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(logins);
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Integer id,
            @RequestBody PasswordChangeRequestDTO request) {

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

        // Hashage et mise à jour du mot de passe
        String hashedPassword = passwordService.hashPassword(request.getNewPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        return ResponseEntity.ok("Mot de passe modifié avec succès");
    }

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
}
