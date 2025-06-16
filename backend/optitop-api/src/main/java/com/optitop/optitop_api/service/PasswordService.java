package com.optitop.optitop_api.service;

// ===== IMPORTS SPRING SECURITY =====
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service de gestion des mots de passe
 * 
 * Fournit les fonctionnalités de hachage et de vérification des mots de passe
 * utilisant l'algorithme BCrypt pour la sécurité
 */
@Service
public class PasswordService {

    // ===== CHAMPS =====

    private final BCryptPasswordEncoder passwordEncoder;

    // ===== CONSTRUCTEUR =====

    public PasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // ===== MÉTHODES PUBLIQUES =====

    /**
     * Hache un mot de passe en clair avec BCrypt
     * 
     * @param plainTextPassword le mot de passe en clair à hacher
     * @return le mot de passe haché avec BCrypt
     */
    public String hashPassword(String plainTextPassword) {
        return passwordEncoder.encode(plainTextPassword);
    }

    /**
     * Vérifie si un mot de passe en clair correspond au mot de passe haché
     * 
     * @param plainTextPassword le mot de passe en clair à vérifier
     * @param hashedPassword    le mot de passe haché stocké en base
     * @return true si les mots de passe correspondent, false sinon
     */
    public boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        return passwordEncoder.matches(plainTextPassword, hashedPassword);
    }
}