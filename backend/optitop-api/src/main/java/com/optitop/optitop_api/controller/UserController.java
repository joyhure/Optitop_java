package com.optitop.optitop_api.controller;

import com.optitop.optitop_api.dto.PasswordChangeRequest;
import com.optitop.optitop_api.model.User;
import com.optitop.optitop_api.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost")
public class UserController {

    @Autowired
    private UserRepository userRepository;

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

    @PostMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Integer id,
            @RequestBody PasswordChangeRequest request) {

        // Validation des données
        if (request.getCurrentPassword() == null || request.getNewPassword() == null) {
            return ResponseEntity.badRequest().body("Les mots de passe ne peuvent pas être vides");
        }

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Vérifier le mot de passe actuel
        if (!user.getPassword().equals(request.getCurrentPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Mot de passe actuel incorrect");
        }

        // Validation du nouveau mot de passe
        String newPassword = request.getNewPassword();
        if (!isValidPassword(newPassword)) {
            return ResponseEntity.badRequest()
                    .body("Le nouveau mot de passe ne respecte pas les critères de sécurité");
        }

        // Mettre à jour le mot de passe
        user.setPassword(request.getNewPassword());
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
}
