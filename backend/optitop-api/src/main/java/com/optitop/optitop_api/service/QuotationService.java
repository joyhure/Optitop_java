package com.optitop.optitop_api.service;

// ===== IMPORTS DTO ET MODÈLES =====
import com.optitop.optitop_api.dto.QuotationUpdateDTO;
import com.optitop.optitop_api.model.Quotations;
import com.optitop.optitop_api.model.Quotations.QuotationAction;
import com.optitop.optitop_api.model.Seller;
import com.optitop.optitop_api.repository.QuotationsRepository;
import com.optitop.optitop_api.repository.SellerRepository;

// ===== IMPORTS SPRING FRAMEWORK =====
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// ===== IMPORTS JPA =====
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

// ===== IMPORTS UTILITAIRES =====
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service métier pour la gestion des devis optiques
 * 
 * Fournit les opérations métier complexes pour les devis :
 * - Mise à jour en lot des actions et commentaires
 * - Validation et transformation des données
 * - Gestion transactionnelle des modifications
 * - Affectation des vendeurs aux devis
 * 
 * Utilisé par le contrôleur pour déléguer la logique métier
 * et garantir la cohérence des données lors des opérations batch.
 */
@Service
public class QuotationService {

    // ===== INJECTION DES DÉPENDANCES =====

    /**
     * Logger pour tracer les opérations et gérer les erreurs
     */
    private static final Logger logger = LoggerFactory.getLogger(QuotationService.class);

    /**
     * Repository pour l'accès aux données des devis
     * Gère les opérations CRUD sur les quotations
     */
    @Autowired
    private QuotationsRepository quotationsRepository;

    /**
     * Repository pour l'accès aux données des vendeurs
     * Utilisé pour la validation et l'affectation des vendeurs
     */
    @Autowired
    private SellerRepository sellerRepository;

    // ===== OPÉRATIONS BATCH =====

    /**
     * Met à jour un lot de devis avec les nouvelles actions et commentaires
     * 
     * Traite en une seule transaction toutes les mises à jour demandées.
     * En cas d'erreur sur un devis, toute la transaction est annulée
     * pour garantir la cohérence des données.
     * 
     * @param updates Liste des mises à jour à appliquer
     * @throws RuntimeException         si une erreur survient pendant la mise à
     *                                  jour
     * @throws IllegalArgumentException si la liste est null ou vide
     */
    @Transactional
    public void batchUpdate(List<QuotationUpdateDTO> updates) {
        // Validation des paramètres d'entrée
        if (updates == null || updates.isEmpty()) {
            logger.warn("Aucune mise à jour à effectuer - liste vide ou null");
            return;
        }

        logger.info("Début de la mise à jour batch de {} devis", updates.size());

        // Traitement de chaque mise à jour
        for (QuotationUpdateDTO update : updates) {
            try {
                updateSingleQuotation(update);
            } catch (Exception e) {
                String errorMsg = "Erreur lors de la mise à jour du devis %d".formatted(update.getId());
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        }

        logger.info("Mise à jour batch terminée avec succès pour {} devis", updates.size());
    }

    // ===== OPÉRATIONS INDIVIDUELLES =====

    /**
     * Met à jour un devis individuel avec les nouvelles données
     * 
     * Recherche le devis par son ID et applique les modifications
     * si le devis existe. Ignore silencieusement les devis inexistants
     * pour éviter les erreurs lors des opérations batch.
     * 
     * @param update DTO contenant les informations de mise à jour
     */
    private void updateSingleQuotation(QuotationUpdateDTO update) {
        // Recherche et mise à jour du devis
        quotationsRepository.findById(update.getId())
                .ifPresentOrElse(
                        quotation -> {
                            // Application des modifications
                            updateQuotationAction(quotation, update);
                            quotation.setComment(update.getComment());

                            // Sauvegarde des modifications
                            quotationsRepository.save(quotation);
                            logger.debug("Devis {} mis à jour avec succès", update.getId());
                        },
                        () -> logger.warn("Devis {} non trouvé pour mise à jour", update.getId()));
    }

    /**
     * Met à jour l'action d'un devis si une nouvelle action est fournie
     * 
     * Valide la nouvelle action contre l'énumération QuotationAction
     * avant de l'appliquer. Ignore les actions invalides avec un warning.
     * 
     * @param quotation Devis à mettre à jour
     * @param update    DTO contenant la nouvelle action
     */
    private void updateQuotationAction(Quotations quotation, QuotationUpdateDTO update) {
        // Vérification de la présence d'une nouvelle action
        if (update.getAction() == null || update.getAction().trim().isEmpty()) {
            logger.debug("Aucune action à mettre à jour pour le devis {}", quotation.getId());
            return;
        }

        try {
            // Conversion et validation de l'action
            QuotationAction action = QuotationAction.valueOf(update.getAction().toUpperCase().trim());
            quotation.setAction(action);
            logger.debug("Action du devis {} mise à jour : {}", quotation.getId(), action);
        } catch (IllegalArgumentException e) {
            logger.warn("Action invalide ignorée pour le devis {} : '{}'",
                    quotation.getId(), update.getAction());
        }
    }

    // ===== GESTION DES VENDEURS =====

    /**
     * Met à jour le vendeur associé à un devis
     * 
     * Recherche le vendeur par sa référence et l'associe au devis.
     * Permet de gérer l'affectation null (désassociation du vendeur).
     * 
     * @param quotation Devis à modifier
     * @param sellerRef Référence du vendeur à associer (peut être null)
     * @throws EntityNotFoundException si le vendeur n'existe pas
     */
    public void updateQuotationSeller(Quotations quotation, String sellerRef) {
        // Gestion de la désassociation
        if (sellerRef == null || sellerRef.trim().isEmpty()) {
            quotation.setSeller(null);
            logger.debug("Vendeur désassocié du devis {}", quotation.getId());
            return;
        }

        // Recherche et association du vendeur
        Seller seller = sellerRepository.findBySellerRef(sellerRef.trim())
                .orElseThrow(() -> {
                    String errorMsg = "Vendeur non trouvé : " + sellerRef;
                    logger.error(errorMsg);
                    return new EntityNotFoundException(errorMsg);
                });

        quotation.setSeller(seller);
        logger.debug("Vendeur {} associé au devis {}", sellerRef, quotation.getId());
    }
}