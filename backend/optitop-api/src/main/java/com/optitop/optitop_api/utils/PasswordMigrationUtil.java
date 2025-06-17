package com.optitop.optitop_api.utils;

/**
 * Utilitaire de migration des mots de passe utilisateurs
 * 
 * Classe responsable de la migration automatique des mots de passe en clair
 * vers des mots de passe hashés avec BCrypt lors du démarrage de l'application.
 */

import com.optitop.optitop_api.model.User;
import com.optitop.optitop_api.repository.UserRepository;
import com.optitop.optitop_api.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
public class PasswordMigrationUtil {

    // ===== LOGGER =====
    private static final Logger logger = LoggerFactory.getLogger(PasswordMigrationUtil.class);

    // ===== DÉPENDANCES =====
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    // ===== MIGRATION =====

    @PostConstruct
    @Transactional
    public void migratePasswords() {
        logger.info("Début de la migration des mots de passe");

        int migratedCount = 0;
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getPassword() != null && !isPasswordHashed(user.getPassword())) {
                String hashedPassword = passwordService.hashPassword(user.getPassword());
                user.setPassword(hashedPassword);
                userRepository.save(user);
                migratedCount++;
                logger.debug("Mot de passe migré pour l'utilisateur : {}", user.getLogin());
            }
        }

        logger.info("Migration terminée. {} mots de passe migrés sur {} utilisateurs",
                migratedCount, users.size());
    }

    // ===== MÉTHODES UTILITAIRES =====

    private boolean isPasswordHashed(String password) {
        return password.startsWith("$2a$") ||
                password.startsWith("$2b$") ||
                password.startsWith("$2y$");
    }
}
