package com.optitop.optitop_api.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.optitop.optitop_api.dto.PendingAccountDTO;
import com.optitop.optitop_api.model.PendingAccount;
import com.optitop.optitop_api.model.PendingAccount.RequestType;
import com.optitop.optitop_api.model.User;
import com.optitop.optitop_api.repository.PendingAccountRepository;
import com.optitop.optitop_api.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
@Transactional
public class PendingAccountService {

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
}