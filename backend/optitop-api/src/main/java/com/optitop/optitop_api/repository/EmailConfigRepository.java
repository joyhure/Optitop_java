package com.optitop.optitop_api.repository;

/**
 * Repository pour la gestion de la configuration email
 * 
 * Interface JPA pour l'accès aux données de configuration SMTP
 * stockées en base de données.
 */

import org.springframework.data.jpa.repository.JpaRepository;
import com.optitop.optitop_api.model.EmailConfig;

public interface EmailConfigRepository extends JpaRepository<EmailConfig, Integer> {

    /**
     * Récupère la dernière configuration email créée
     * 
     * @return La configuration email la plus récente
     */
    EmailConfig findFirstByOrderByIdDesc();
}