package com.optitop.optitop_api.repository;

// ===== IMPORTS PROJET =====
import com.optitop.optitop_api.model.User;

// ===== IMPORTS SPRING =====
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

// ===== IMPORTS JAVA =====
import java.util.List;

/**
 * Repository pour la gestion des utilisateurs
 * 
 * Fournit les opérations CRUD de base et les requêtes personnalisées
 * pour l'entité User via Spring Data JPA
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Trouve un utilisateur par son identifiant de connexion
     * 
     * @param login l'identifiant de connexion unique
     * @return l'utilisateur correspondant ou null si non trouvé
     */
    User findByLogin(String login);

    /**
     * Récupère tous les identifiants de connexion triés par ordre alphabétique
     * 
     * @return liste des identifiants triés de A à Z
     */
    @Query("SELECT u.login FROM User u ORDER BY u.login ASC")
    List<String> findAllLogins();
}
