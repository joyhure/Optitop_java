package com.optitop.optitop_api.service;

// ===== IMPORTS SPRING =====
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// ===== IMPORTS DTOs =====
import com.optitop.optitop_api.dto.PendingAccountDTO;

// ===== IMPORTS MODÈLES =====
import com.optitop.optitop_api.model.PendingAccount;
import com.optitop.optitop_api.model.PendingAccount.RequestType;
import com.optitop.optitop_api.model.Seller;
import com.optitop.optitop_api.model.User;

// ===== IMPORTS REPOSITORIES =====
import com.optitop.optitop_api.repository.PendingAccountRepository;
import com.optitop.optitop_api.repository.UserRepository;
import com.optitop.optitop_api.repository.SellerRepository;

// ===== IMPORTS TRANSACTION =====
import jakarta.transaction.Transactional;

// ===== IMPORTS UTILITAIRES =====
import java.security.SecureRandom;
import java.util.List;

/**
 * Service de gestion des demandes de comptes utilisateurs
 * 
 * Gère le cycle de vie complet des demandes de comptes :
 * - Création des demandes (ajout, modification, suppression)
 * - Validation et traitement des demandes par les administrateurs
 * - Rejet des demandes non conformes
 * - Génération automatique de mots de passe pour les nouveaux comptes
 */
@Service
@Transactional
public class PendingAccountService {

    // ===== INJECTION DES DÉPENDANCES =====

    /**
     * Encodeur de mots de passe pour sécuriser les comptes
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Service d'envoi d'emails pour notifier les nouveaux utilisateurs
     */
    @Autowired
    private EmailService emailService;

    /**
     * Repository pour la gestion des vendeurs
     */
    @Autowired
    private SellerRepository sellerRepository;

    /**
     * Repository pour la gestion des demandes de comptes
     */
    private final PendingAccountRepository pendingAccountRepository;

    /**
     * Repository pour la gestion des utilisateurs
     */
    private final UserRepository userRepository;

    // ===== CONSTRUCTEUR =====

    /**
     * Constructeur avec injection des repositories
     * 
     * @param pendingAccountRepository Repository des demandes de comptes
     * @param userRepository           Repository des utilisateurs
     */
    public PendingAccountService(PendingAccountRepository pendingAccountRepository,
            UserRepository userRepository) {
        this.pendingAccountRepository = pendingAccountRepository;
        this.userRepository = userRepository;
    }

    // ===== MÉTHODES DE GESTION DES DEMANDES =====

    /**
     * Crée une nouvelle demande de compte
     * 
     * @param dto         Données de la demande
     * @param createdById ID de l'utilisateur créant la demande
     * @throws RuntimeException si utilisateur non trouvé ou demande en doublon
     */
    public void createPendingAccount(PendingAccountDTO dto, Integer createdById) {
        User createdBy = userRepository.findById(createdById).get();
        RequestType requestType = RequestType.valueOf(dto.getRequestType().toLowerCase());

        PendingAccount pendingAccount;

        if (requestType == RequestType.suppression) {
            // Récupération des informations de l'utilisateur à supprimer
            User userToDelete = userRepository.findByLogin(dto.getLogin());
            if (userToDelete == null) {
                throw new RuntimeException("Utilisateur non trouvé avec le login : " + dto.getLogin());
            }

            pendingAccount = new PendingAccount(
                    userToDelete.getLastname(),
                    userToDelete.getFirstname(),
                    userToDelete.getEmail(),
                    dto.getLogin(),
                    userToDelete.getRole(),
                    createdBy,
                    requestType);

        } else if (requestType == RequestType.modification) {
            // Récupération de l'utilisateur à modifier
            User userToModify = userRepository.findByLogin(dto.getLogin());
            if (userToModify == null) {
                throw new RuntimeException("Utilisateur non trouvé avec le login : " + dto.getLogin());
            }

            // Utilisation des valeurs saisies si présentes, sinon valeurs existantes
            pendingAccount = new PendingAccount(
                    dto.getLastname() != null ? dto.getLastname() : userToModify.getLastname(),
                    dto.getFirstname() != null ? dto.getFirstname() : userToModify.getFirstname(),
                    dto.getEmail() != null ? dto.getEmail() : userToModify.getEmail(),
                    dto.getLogin(),
                    dto.getRole(),
                    createdBy,
                    requestType);

        } else {
            // Pour ajout tous les champs sont requis
            pendingAccount = new PendingAccount(
                    dto.getLastname(),
                    dto.getFirstname(),
                    dto.getEmail(),
                    dto.getLogin(),
                    dto.getRole(),
                    createdBy,
                    requestType);
        }

        try {
            pendingAccountRepository.save(pendingAccount);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Une demande est déjà en cours pour le login : " + dto.getLogin());
        }
    }

    /**
     * Récupère toutes les demandes de comptes en attente
     * 
     * @return Liste des demandes triées par date de création décroissante
     */
    public List<PendingAccount> getAllPendingAccounts() {
        return pendingAccountRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Valide une demande de compte et effectue l'action correspondante
     * 
     * @param pendingAccountId ID de la demande à valider
     * @param validatorId      ID de l'administrateur validant
     * @throws RuntimeException si demande non trouvée ou validateur non autorisé
     */
    @Transactional
    public void validatePendingAccount(Integer pendingAccountId, Integer validatorId) {
        // Récupération de la demande en attente
        PendingAccount pending = pendingAccountRepository.findById(pendingAccountId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        // Vérification du validateur
        User validator = userRepository.findById(validatorId).get();
        if (validator == null || validator.getRole() != User.Role.admin) {
            throw new RuntimeException("Action non autorisée");
        }

        switch (pending.getRequestType()) {
            case ajout:
                // Création d'un nouveau compte utilisateur
                User newUser = new User();
                newUser.setLastname(pending.getLastname());
                newUser.setFirstname(pending.getFirstname());
                newUser.setEmail(pending.getEmail());
                newUser.setLogin(pending.getLogin());
                newUser.setRole(pending.getRole());

                // Génération et hachage du mot de passe
                String rawPassword = generateSecurePassword();
                newUser.setPassword(passwordEncoder.encode(rawPassword));

                try {
                    // Sauvegarde de l'utilisateur et récupération de l'ID généré
                    User savedUser = userRepository.save(newUser);

                    // Si c'est un collaborator ou manager, mettre à jour le seller
                    if (savedUser.getRole() == User.Role.collaborator ||
                            savedUser.getRole() == User.Role.manager) {
                        // Recherche du seller par login
                        Seller seller = sellerRepository.findBySellerRef(savedUser.getLogin())
                                .orElseThrow(() -> new RuntimeException(
                                        "Seller non trouvé pour le login : " + savedUser.getLogin()));

                        // Mise à jour de l'user_id
                        seller.setUser(savedUser);
                        sellerRepository.save(seller);
                    }

                    emailService.sendPasswordEmail(newUser.getEmail(), newUser.getLogin(), rawPassword);
                } catch (Exception e) {
                    throw new RuntimeException("Erreur lors de l'envoi de l'email : " + e.getMessage());
                }
                break;

            case modification:
                // Modification d'un compte existant
                User userToModify = userRepository.findByLogin(pending.getLogin());
                if (userToModify == null) {
                    throw new RuntimeException("Utilisateur à modifier non trouvé");
                }
                userToModify.setLastname(pending.getLastname());
                userToModify.setFirstname(pending.getFirstname());
                userToModify.setEmail(pending.getEmail());
                userToModify.setRole(pending.getRole());
                userRepository.save(userToModify);
                break;

            case suppression:
                // Suppression d'un compte existant
                User userToDelete = userRepository.findByLogin(pending.getLogin());
                if (userToDelete == null) {
                    throw new RuntimeException("Utilisateur à supprimer non trouvé");
                }
                userRepository.delete(userToDelete);
                break;
        }

        // Suppression de la demande après traitement
        pendingAccountRepository.delete(pending);
    }

    /**
     * Rejette une demande de compte
     * 
     * @param pendingAccountId ID de la demande à rejeter
     * @param validatorId      ID de l'administrateur rejetant
     * @throws RuntimeException si demande non trouvée ou validateur non autorisé
     */
    @Transactional
    public void rejectPendingAccount(Integer pendingAccountId, Integer validatorId) {
        // Récupération de la demande en attente
        PendingAccount pending = pendingAccountRepository.findById(pendingAccountId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        // Vérification du validateur
        User validator = userRepository.findById(validatorId)
                .orElseThrow(() -> new RuntimeException("Validateur non trouvé"));
        if (validator.getRole() != User.Role.admin) {
            throw new RuntimeException("Action non autorisée");
        }

        // Suppression de la demande sans traitement
        pendingAccountRepository.delete(pending);
    }

    // ===== MÉTHODES UTILITAIRES PRIVÉES =====

    /**
     * Génère un mot de passe sécurisé aléatoire
     * 
     * @return Mot de passe de 12 caractères avec majuscules, minuscules, chiffres
     *         et symboles
     */
    private String generateSecurePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*()_+-=[]|";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }
}