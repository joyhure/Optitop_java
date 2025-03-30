package com.optitop.optitop_api.utils;

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

    private static final Logger logger = LoggerFactory.getLogger(PasswordMigrationUtil.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    @PostConstruct
    @Transactional
    public void migratePasswords() {
        logger.info("Début de la migration des mots de passe...");
        int count = 0;

        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (!isPasswordHashed(user.getPassword())) {
                String hashedPassword = passwordService.hashPassword(user.getPassword());
                user.setPassword(hashedPassword);
                userRepository.save(user);
                count++;
                logger.debug("Mot de passe migré pour l'utilisateur: {}", user.getLogin());
            }
        }

        logger.info("Migration terminée. {} mots de passe ont été hashés", count);
    }

    private boolean isPasswordHashed(String password) {
        return password != null && (password.startsWith("$2a$") ||
                password.startsWith("$2b$") ||
                password.startsWith("$2y$"));
    }
}
