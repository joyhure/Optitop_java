package com.optitop.optitop_api.service;

// ===== IMPORTS DTOs =====
import com.optitop.optitop_api.dto.SellerDTO;

// ===== IMPORTS REPOSITORIES =====
import com.optitop.optitop_api.repository.PendingAccountRepository;
import com.optitop.optitop_api.repository.SellerRepository;

// ===== IMPORTS SPRING =====
import org.springframework.stereotype.Service;

// ===== IMPORTS UTILITAIRES =====
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des vendeurs
 * 
 * Gère les opérations liées aux vendeurs du système Optitop :
 * - Récupération des vendeurs disponibles pour création de compte
 * - Filtrage selon les demandes en cours et comptes existants
 * 
 * @author Joy Huré
 * @version 1.0
 */
@Service
public class SellerService {

    // ===== INJECTION DES DÉPENDANCES =====

    /**
     * Repository pour la gestion des vendeurs
     */
    private final SellerRepository sellerRepository;

    /**
     * Repository pour la gestion des demandes de comptes
     */
    private final PendingAccountRepository pendingAccountRepository;

    // ===== CONSTRUCTEUR =====

    /**
     * Constructeur avec injection des repositories
     * 
     * @param sellerRepository         Repository des vendeurs
     * @param pendingAccountRepository Repository des demandes de comptes
     */
    public SellerService(SellerRepository sellerRepository,
            PendingAccountRepository pendingAccountRepository) {
        this.sellerRepository = sellerRepository;
        this.pendingAccountRepository = pendingAccountRepository;
    }

    // ===== MÉTHODES DE GESTION DES VENDEURS =====

    /**
     * Trouve tous les vendeurs qui n'ont pas d'utilisateur associé
     * 
     * Récupère les vendeurs qui n'ont pas encore de compte utilisateur
     * et qui n'ont pas de demande de compte en cours de traitement.
     * 
     * @return Liste des vendeurs disponibles pour création de compte
     */
    public List<SellerDTO> findAvailableSellers() {
        // Récupérer tous les logins avec des demandes en cours
        List<String> pendingLogins = pendingAccountRepository.findAllLogins();

        // Récupérer tous les sellers et filtrer ceux qui n'ont pas de demande en cours
        return sellerRepository.findAllByUserIsNull().stream()
                .filter(seller -> !pendingLogins.contains(seller.getSellerRef()))
                .map(seller -> new SellerDTO(seller.getSellerRef()))
                .collect(Collectors.toList());
    }
}