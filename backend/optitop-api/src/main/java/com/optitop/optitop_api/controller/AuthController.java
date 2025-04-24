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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost", "http://10.0.2.2", "http://optitop.local" })
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

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

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Boolean>> logout() {
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}
