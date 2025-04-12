package com.optitop.optitop_api.service;

import com.optitop.optitop_api.dto.SellerDTO;
import com.optitop.optitop_api.repository.SellerRepository;
import com.optitop.optitop_api.repository.PendingAccountRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final PendingAccountRepository pendingAccountRepository;

    public SellerService(SellerRepository sellerRepository,
            PendingAccountRepository pendingAccountRepository) {
        this.sellerRepository = sellerRepository;
        this.pendingAccountRepository = pendingAccountRepository;
    }

    /**
     * Trouve tous les vendeurs qui n'ont pas d'utilisateur associé
     * 
     * @return Liste des vendeurs disponibles
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