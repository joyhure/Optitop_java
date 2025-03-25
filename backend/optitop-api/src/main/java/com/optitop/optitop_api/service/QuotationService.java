package com.optitop.optitop_api.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optitop.optitop_api.dto.QuotationUpdateDTO;
import com.optitop.optitop_api.model.Quotations;
import com.optitop.optitop_api.model.Quotations.QuotationAction;
import com.optitop.optitop_api.repository.QuotationsRepository;
import com.optitop.optitop_api.repository.SellerRepository;
import com.optitop.optitop_api.model.Seller;

import jakarta.transaction.Transactional;
import jakarta.persistence.EntityNotFoundException;

@Service
public class QuotationService {

    private static final Logger logger = LoggerFactory.getLogger(QuotationService.class);

    @Autowired
    private QuotationsRepository quotationsRepository;

    @Autowired
    private SellerRepository sellerRepository;

    /**
     * Met à jour un lot de devis avec les nouvelles actions et commentaires
     * 
     * @param updates Liste des mises à jour à appliquer
     * @throws RuntimeException si une erreur survient pendant la mise à jour
     */
    @Transactional
    public void batchUpdate(List<QuotationUpdateDTO> updates) {
        if (updates == null || updates.isEmpty()) {
            logger.warn("Aucune mise à jour à effectuer");
            return;
        }

        for (QuotationUpdateDTO update : updates) {
            try {
                updateSingleQuotation(update);
            } catch (Exception e) {
                String errorMsg = String.format("Erreur lors de la mise à jour du devis %d", update.getId());
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        }
    }

    /**
     * Met à jour un devis individuel
     * 
     * @param update DTO contenant les informations de mise à jour
     */
    private void updateSingleQuotation(QuotationUpdateDTO update) {
        quotationsRepository.findById(update.getId())
                .ifPresent(quotation -> {
                    updateQuotationAction(quotation, update);
                    quotation.setComment(update.getComment());
                    quotationsRepository.save(quotation);
                    logger.debug("Devis {} mis à jour avec succès", update.getId());
                });
    }

    /**
     * Met à jour l'action d'un devis si nécessaire
     * 
     * @param quotation Devis à mettre à jour
     * @param update    DTO contenant la nouvelle action
     */
    private void updateQuotationAction(Quotations quotation, QuotationUpdateDTO update) {
        if (update.getAction() != null && !update.getAction().isEmpty()) {
            try {
                QuotationAction action = QuotationAction.valueOf(update.getAction().toUpperCase());
                quotation.setAction(action);
                logger.debug("Action du devis {} mise à jour : {}", quotation.getId(), action);
            } catch (IllegalArgumentException e) {
                logger.warn("Action invalide ignorée pour le devis {} : {}",
                        quotation.getId(), update.getAction());
            }
        }
    }

    public void updateQuotationSeller(Quotations quotation, String sellerRef) {
        if (sellerRef == null) {
            quotation.setSeller(null);
            return;
        }

        Seller seller = sellerRepository.findBySellerRef(sellerRef)
                .orElseThrow(() -> new EntityNotFoundException("Vendeur non trouvé : " + sellerRef));
        quotation.setSeller(seller);
    }
}