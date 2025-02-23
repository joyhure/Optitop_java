package com.optitop.optitop_api.controller;

import com.optitop.optitop_api.model.User;
import com.optitop.optitop_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;

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
}
