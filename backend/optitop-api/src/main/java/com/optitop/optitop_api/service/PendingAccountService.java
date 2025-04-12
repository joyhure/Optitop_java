package com.optitop.optitop_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.optitop.optitop_api.dto.PendingAccountDTO;
import com.optitop.optitop_api.model.PendingAccount;
import com.optitop.optitop_api.model.PendingAccount.RequestType;
import com.optitop.optitop_api.model.Seller;
import com.optitop.optitop_api.model.User;
import com.optitop.optitop_api.repository.PendingAccountRepository;
import com.optitop.optitop_api.repository.UserRepository;
import com.optitop.optitop_api.repository.SellerRepository;

import jakarta.transaction.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@Transactional
public class PendingAccountService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SellerRepository sellerRepository;

    private final PendingAccountRepository pendingAccountRepository;
    private final UserRepository userRepository;

    public PendingAccountService(PendingAccountRepository pendingAccountRepository,
            UserRepository userRepository) {
        this.pendingAccountRepository = pendingAccountRepository;
        this.userRepository = userRepository;
    }

    public void createPendingAccount(PendingAccountDTO dto, Integer createdById) {
        User createdBy = userRepository.findById(createdById).get();
        RequestType requestType = RequestType.valueOf(dto.requestType().toLowerCase());

        PendingAccount pendingAccount;
        if (requestType == RequestType.suppression) {
            // Récupération des informations de l'utilisateur à supprimer
            User userToDelete = userRepository.findByLogin(dto.login());
            if (userToDelete == null) {
                throw new RuntimeException("Utilisateur non trouvé avec le login : " + dto.login());
            }

            pendingAccount = new PendingAccount(
                    userToDelete.getLastname(),
                    userToDelete.getFirstname(),
                    userToDelete.getEmail(),
                    dto.login(),
                    userToDelete.getRole(),
                    createdBy,
                    requestType);
        } else if (requestType == RequestType.modification) {
            // Récupération de l'utilisateur à modifier
            User userToModify = userRepository.findByLogin(dto.login());
            if (userToModify == null) {
                throw new RuntimeException("Utilisateur non trouvé avec le login : " + dto.login());
            }

            // Utilisation des valeurs saisies si présentes, sinon valeurs existantes
            pendingAccount = new PendingAccount(
                    dto.lastname() != null ? dto.lastname() : userToModify.getLastname(),
                    dto.firstname() != null ? dto.firstname() : userToModify.getFirstname(),
                    dto.email() != null ? dto.email() : userToModify.getEmail(),
                    dto.login(),
                    dto.role(),
                    createdBy,
                    requestType);
        } else {
            // Pour ajout tous les champs sont requis
            pendingAccount = new PendingAccount(
                    dto.lastname(),
                    dto.firstname(),
                    dto.email(),
                    dto.login(),
                    dto.role(),
                    createdBy,
                    requestType);
        }

        try {
            pendingAccountRepository.save(pendingAccount);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Une demande est déjà en cours pour le login : " + dto.login());
        }
    }

    public List<PendingAccount> getAllPendingAccounts() {
        return pendingAccountRepository.findAllByOrderByCreatedAtDesc();
    }

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
                User userToDelete = userRepository.findByLogin(pending.getLogin());
                if (userToDelete == null) {
                    throw new RuntimeException("Utilisateur à supprimer non trouvé");
                }
                userRepository.delete(userToDelete);
                break;
        }

        // Suppression de la demande
        pendingAccountRepository.delete(pending);
    }

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

        // Suppression de la demande
        pendingAccountRepository.delete(pending);
    }

    private String generateSecurePassword() {
        // Génération d'un mot de passe fort de 12 caractères
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*()_+-=[]|";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }
}