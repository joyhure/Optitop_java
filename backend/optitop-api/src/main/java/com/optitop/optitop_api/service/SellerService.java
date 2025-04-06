package com.optitop.optitop_api.service;

import com.optitop.optitop_api.dto.SellerDTO;
import com.optitop.optitop_api.repository.SellerRepository;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    /**
     * Trouve tous les vendeurs qui n'ont pas d'utilisateur associé
     * 
     * @return Liste des vendeurs disponibles
     */
    public List<SellerDTO> findAvailableSellers() {
        return sellerRepository.findAllByUserIsNull()
                .stream()
                .map(seller -> new SellerDTO(seller.getSellerRef()))
                .sorted(Comparator.comparing(SellerDTO::sellerRef))
                .toList();
    }
}