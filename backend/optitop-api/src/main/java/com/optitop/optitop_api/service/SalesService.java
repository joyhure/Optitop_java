package com.optitop.optitop_api.service;

// ===== IMPORTS MODÈLES =====
import com.optitop.optitop_api.model.Invoices;
import com.optitop.optitop_api.model.InvoicesLines;
import com.optitop.optitop_api.model.Quotations;
import com.optitop.optitop_api.model.QuotationsLines;
import com.optitop.optitop_api.model.Seller;
import com.optitop.optitop_api.model.User;

// ===== IMPORTS REPOSITORIES =====
import com.optitop.optitop_api.repository.InvoicesLinesRepository;
import com.optitop.optitop_api.repository.InvoicesRepository;
import com.optitop.optitop_api.repository.QuotationsLinesRepository;
import com.optitop.optitop_api.repository.QuotationsRepository;
import com.optitop.optitop_api.repository.SellerRepository;
import com.optitop.optitop_api.repository.UserRepository;

// ===== IMPORTS SPRING FRAMEWORK =====
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ===== IMPORTS JPA =====
import jakarta.persistence.EntityNotFoundException;

// ===== IMPORTS UTILITAIRES =====
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service métier pour l'importation et le traitement des données de vente
 * 
 * Gère l'importation complète des données CSV de vente avec :
 * - Parsing et validation des lignes CSV
 * - Création automatique des vendeurs manquants
 * - Traitement séparé des factures et devis
 * - Agrégation des lignes en entités métier
 * - Traitement par lots pour optimiser les performances
 * - Gestion des périodes avec suppression/recréation
 * 
 * Le service distingue les factures (avec avoir) des devis et crée
 * les entités appropriées avec leurs lignes de détail.
 */
@Service
public class SalesService {

    // ===== CONSTANTES =====

    /**
     * Logger pour tracer les opérations d'import et gérer les erreurs
     */
    private static final Logger logger = LoggerFactory.getLogger(SalesService.class);

    /**
     * Taille des lots pour l'insertion en base de données
     * Optimise les performances en évitant les insertions unitaires
     */
    private static final int BATCH_SIZE = 1000;

    /**
     * Formateur de date pour le parsing des données CSV (format dd/MM/yyyy)
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ===== INJECTION DES DÉPENDANCES =====

    /**
     * Repository pour l'accès aux lignes de factures
     */
    @Autowired
    private InvoicesLinesRepository invoicesLinesRepository;

    /**
     * Repository pour l'accès aux lignes de devis
     */
    @Autowired
    private QuotationsLinesRepository quotationsLinesRepository;

    /**
     * Repository pour l'accès aux factures agrégées
     */
    @Autowired
    private InvoicesRepository invoicesRepository;

    /**
     * Repository pour l'accès aux devis agrégés
     */
    @Autowired
    private QuotationsRepository quotationsRepository;

    /**
     * Repository pour la gestion des vendeurs
     */
    @Autowired
    private SellerRepository sellerRepository;

    /**
     * Repository pour la gestion des utilisateurs
     */
    @Autowired
    private UserRepository userRepository;

    // ===== TRAITEMENT PRINCIPAL =====

    /**
     * Traite un lot de lignes CSV pour l'importation des données de vente
     * 
     * Processus complet d'importation :
     * 1. Suppression de la ligne d'en-tête si présente
     * 2. Calcul de la plage de dates des données
     * 3. Suppression des données existantes sur la période
     * 4. Traitement ligne par ligne avec création des entités
     * 5. Sauvegarde par lots pour optimiser les performances
     * 6. Création des entités agrégées (factures et devis)
     * 
     * @param lines Liste des lignes CSV à traiter
     * @throws RuntimeException si erreur lors du traitement
     */
    @Transactional
    public void processBatch(List<String> lines) {
        // Validation des paramètres d'entrée
        if (lines == null || lines.isEmpty()) {
            logger.warn("Aucune ligne à traiter dans le lot");
            return;
        }

        try {
            // Préparation des données avec suppression de l'en-tête
            List<String> dataLines = prepareDataLines(lines);
            if (dataLines.isEmpty()) {
                logger.warn("Aucune ligne de données après suppression de l'en-tête");
                return;
            }

            // Calcul de la plage de dates pour la suppression/recréation
            LocalDate[] dateRange = calculateDateRange(dataLines);
            LocalDate minDate = dateRange[0];
            LocalDate maxDate = dateRange[1];

            // Suppression des données existantes sur la période
            deleteExistingData(minDate, maxDate);

            // Traitement des lignes avec sauvegarde par lots
            processDataLines(dataLines);

            // Création des entités agrégées
            createAggregatedEntities(minDate, maxDate);

            logger.info("Traitement du lot terminé avec succès pour la période {} - {}", minDate, maxDate);

        } catch (Exception e) {
            logger.error("Erreur lors du traitement du lot", e);
            throw new RuntimeException("Erreur lors du traitement du lot", e);
        }
    }

    // ===== MÉTHODES DE PRÉPARATION =====

    /**
     * Prépare les lignes de données en supprimant l'en-tête si présent
     * 
     * @param lines Lignes brutes du CSV
     * @return Lignes de données sans en-tête
     */
    private List<String> prepareDataLines(List<String> lines) {
        List<String> dataLines = new ArrayList<>(lines);

        // Suppression de la ligne d'en-tête si détectée
        if (!dataLines.isEmpty() && dataLines.get(0).contains("Date;C.;Num client;Client;")) {
            logger.info("Détection et suppression de la ligne d'en-tête");
            dataLines.remove(0);
        }

        return dataLines;
    }

    /**
     * Calcule la plage de dates min/max des données à traiter
     * 
     * @param dataLines Lignes de données CSV
     * @return Tableau [minDate, maxDate]
     * @throws IllegalArgumentException si aucune date valide trouvée
     */
    private LocalDate[] calculateDateRange(List<String> dataLines) {
        // Extraction et parsing de toutes les dates valides
        List<LocalDate> dates = dataLines.stream()
                .map(this::parseLineDate)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (dates.isEmpty()) {
            throw new IllegalArgumentException("Aucune date valide trouvée dans les données");
        }

        // Calcul des bornes min/max
        LocalDate minDate = dates.stream().min(LocalDate::compareTo)
                .orElseThrow(() -> new IllegalArgumentException("Impossible de calculer la date minimale"));
        LocalDate maxDate = dates.stream().max(LocalDate::compareTo)
                .orElseThrow(() -> new IllegalArgumentException("Impossible de calculer la date maximale"));

        logger.info("Plage de dates calculée : {} à {}", minDate, maxDate);
        return new LocalDate[] { minDate, maxDate };
    }

    /**
     * Parse la date d'une ligne CSV
     * 
     * @param line Ligne CSV à analyser
     * @return Date parsée ou null si erreur
     */
    private LocalDate parseLineDate(String line) {
        try {
            String[] columns = line.split(";");
            return LocalDate.parse(columns[0], DATE_FORMATTER);
        } catch (Exception e) {
            logger.error("Erreur lors du parsing de la date dans la ligne: {}", line, e);
            return null;
        }
    }

    // ===== SUPPRESSION DES DONNÉES EXISTANTES =====

    /**
     * Supprime toutes les données existantes sur la période spécifiée
     * 
     * @param minDate Date de début de la période
     * @param maxDate Date de fin de la période
     */
    private void deleteExistingData(LocalDate minDate, LocalDate maxDate) {
        logger.info("Suppression des données existantes entre {} et {}", minDate, maxDate);
        invoicesLinesRepository.deleteByDateBetween(minDate, maxDate);
        quotationsLinesRepository.deleteByDateBetween(minDate, maxDate);
    }

    // ===== TRAITEMENT DES LIGNES =====

    /**
     * Traite toutes les lignes de données et les sauvegarde par lots
     * 
     * @param dataLines Lignes de données à traiter
     */
    private void processDataLines(List<String> dataLines) {
        List<InvoicesLines> invoiceBatch = new ArrayList<>();
        List<QuotationsLines> quotationBatch = new ArrayList<>();

        // Traitement ligne par ligne
        for (String line : dataLines) {
            try {
                processIndividualLine(line, invoiceBatch, quotationBatch);
            } catch (Exception e) {
                logger.error("Erreur lors du traitement de la ligne : {}", line, e);
                // Continuer le traitement des autres lignes
            }
        }

        // Sauvegarde des derniers lots
        saveFinalBatches(invoiceBatch, quotationBatch);
    }

    /**
     * Traite une ligne individuelle et l'ajoute au lot approprié
     * 
     * @param line           Ligne CSV à traiter
     * @param invoiceBatch   Lot de factures en cours
     * @param quotationBatch Lot de devis en cours
     */
    private void processIndividualLine(String line, List<InvoicesLines> invoiceBatch,
            List<QuotationsLines> quotationBatch) {
        String[] columns = line.split(";");

        String type = columns[14].toLowerCase();
        String sellerRef = columns[10];
        LocalDate date = LocalDate.parse(columns[0], DATE_FORMATTER);

        // Vérifier si le seller_ref existe, sinon l'ajouter
        if (!sellerRepository.existsBySellerRef(sellerRef)) {
            Seller newSeller = new Seller();
            newSeller.setSellerRef(sellerRef);

            // Chercher un utilisateur avec le même login
            User matchingUser = userRepository.findByLogin(sellerRef);
            if (matchingUser != null) {
                newSeller.setUser(matchingUser);
                logger.info(
                        "Associé le vendeur " + sellerRef + " à l'utilisateur " + matchingUser.getLogin());
            }

            newSeller.setCreatedAt(LocalDateTime.now());
            sellerRepository.save(newSeller);
            logger.info("Nouveau vendeur ajouté avec la référence : " + sellerRef);
        }

        // Factures
        if (type.contains("facture") || type.contains("avoir")) {
            InvoicesLines invoice = new InvoicesLines();
            invoice.setDate(date);
            invoice.setClientId(columns[2]);
            invoice.setClient(columns[3]);
            invoice.setInvoiceRef(columns[4]);
            invoice.setFamily(columns[5]);
            invoice.setQuantity(Integer.parseInt(columns[6]));
            invoice.setTotalTtc(Double.parseDouble(columns[8].replace(",", ".")));
            invoice.setTotalInvoice(Double.parseDouble(columns[12].replace(",", ".")));
            invoice.setPair(columns[13].isEmpty() ? null : Integer.parseInt(columns[13]));
            invoice.setStatus(type);
            invoice.setCreatedAt(LocalDateTime.now());

            Seller seller = sellerRepository.findBySellerRef(sellerRef)
                    .orElseThrow(() -> new EntityNotFoundException("Vendeur non trouvé: " + sellerRef));
            invoice.setSeller(seller);

            invoiceBatch.add(invoice);
            if (invoiceBatch.size() >= BATCH_SIZE) {
                invoicesLinesRepository.saveAll(invoiceBatch);
                invoiceBatch.clear();
            }
        } else if (type.contains("devis")) {
            QuotationsLines quotation = new QuotationsLines();
            quotation.setDate(date);
            quotation.setClientId(columns[2]);
            quotation.setClient(columns[3]);
            quotation.setQuotationRef(columns[4]);
            quotation.setFamily(columns[5]);
            quotation.setQuantity(Integer.parseInt(columns[6]));
            quotation.setTotalTtc(Double.parseDouble(columns[8].replace(",", ".")));
            quotation.setTotalQuotation(Double.parseDouble(columns[12].replace(",", ".")));
            quotation.setPair(columns[13].isEmpty() ? null : Integer.parseInt(columns[13]));
            quotation.setStatus(type);
            quotation.setCreatedAt(LocalDateTime.now());

            Seller seller = sellerRepository.findBySellerRef(sellerRef)
                    .orElseThrow(() -> new EntityNotFoundException("Vendeur non trouvé: " + sellerRef));
            quotation.setSeller(seller);

            quotationBatch.add(quotation);
            if (quotationBatch.size() >= BATCH_SIZE) {
                quotationsLinesRepository.saveAll(quotationBatch);
                quotationBatch.clear();
            }
        }
    }

    /**
     * Sauvegarde les derniers lots de factures et devis s'il y en a
     * 
     * @param invoiceBatch   Lot de factures à sauvegarder
     * @param quotationBatch Lot de devis à sauvegarder
     */
    private void saveFinalBatches(List<InvoicesLines> invoiceBatch, List<QuotationsLines> quotationBatch) {
        // Sauvegarder les derniers lots
        if (!invoiceBatch.isEmpty()) {
            invoicesLinesRepository.saveAll(invoiceBatch);
        }
        if (!quotationBatch.isEmpty()) {
            quotationsLinesRepository.saveAll(quotationBatch);
        }
    }

    // ===== CRÉATION DES ENTRÉES AGRÉGÉES =====

    /**
     * Crée les entrées agrégées de devis et factures pour la période spécifiée
     * 
     * @param startDate Date de début de la période
     * @param endDate   Date de fin de la période
     */
    private void createAggregatedEntities(LocalDate startDate, LocalDate endDate) {
        createQuotationsEntries(startDate, endDate);
        createInvoicesEntries(startDate, endDate);
    }

    /**
     * Crée les entrées agrégées de devis pour la période spécifiée
     * 
     * @param startDate Date de début de la période
     * @param endDate   Date de fin de la période
     */
    private void createQuotationsEntries(LocalDate startDate, LocalDate endDate) {
        try {
            List<QuotationsLines> imports = quotationsLinesRepository
                    .findByDateBetweenAndFamilyInFetchSeller(startDate, endDate, Arrays.asList("VER"));

            // log
            logger.info("Premier seller chargé: {}",
                    !imports.isEmpty() && imports.get(0).getSeller() != null
                            ? imports.get(0).getSeller().getSellerRef()
                            : "NULL");

            if (!imports.isEmpty()) {
                QuotationsLines firstLine = imports.get(0);
                logger.info("Première ligne: quotationRef={}, seller={}, sellerObject={}",
                        firstLine.getQuotationRef(),
                        firstLine.getSeller() != null ? firstLine.getSeller().getSellerRef() : "NULL",
                        firstLine.getSeller());
            }

            // Grouper les imports par clientId et date
            Map<String, Map<LocalDate, List<QuotationsLines>>> groupedQuotations = imports.stream()
                    .collect(Collectors.groupingBy(QuotationsLines::getClientId,
                            Collectors.groupingBy(QuotationsLines::getDate)));

            List<Quotations> quotationsList = new ArrayList<>();

            groupedQuotations.forEach((clientId, dateMap) -> {
                dateMap.forEach((date, quotationImports) -> {
                    List<Quotations> existingQuotations = quotationsRepository.findByClientIdAndDate(clientId, date);

                    if (!existingQuotations.isEmpty()) {
                        // Mise à jour des devis existants si nécessaire
                        boolean hasValidatedQuotation = quotationImports.stream()
                                .anyMatch(q -> "devis validé".equalsIgnoreCase(q.getStatus()));

                        existingQuotations.forEach(quotation -> {
                            quotation.setIsValidated(hasValidatedQuotation);
                            quotationsRepository.save(quotation);
                        });
                    } else {
                        // Création d'un nouveau devis
                        Quotations quotation = new Quotations();
                        quotation.setDate(date);
                        quotation.setClientId(clientId);
                        quotation.setClient(quotationImports.get(0).getClient());
                        quotation.setSeller(quotationImports.get(0).getSeller());

                        boolean hasValidatedQuotation = quotationImports.stream()
                                .anyMatch(q -> "devis validé".equalsIgnoreCase(q.getStatus()));
                        quotation.setIsValidated(hasValidatedQuotation);

                        quotation.setCreatedAt(LocalDateTime.now());
                        quotationsList.add(quotation);
                    }
                });
            });

            // Sauvegarder les nouveaux devis par lots
            if (!quotationsList.isEmpty()) {
                for (int i = 0; i < quotationsList.size(); i += BATCH_SIZE) {
                    int end = Math.min(i + BATCH_SIZE, quotationsList.size());
                    quotationsRepository.saveAll(quotationsList.subList(i, end));
                }
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la création des entrées de devis", e);
            throw new RuntimeException("Erreur lors de la création des entrées de devis", e);
        }
    }

    /**
     * Crée les entrées agrégées de factures pour la période spécifiée
     * 
     * @param startDate Date de début de la période
     * @param endDate   Date de fin de la période
     */
    private void createInvoicesEntries(LocalDate startDate, LocalDate endDate) {
        try {
            logger.info("Création des entrées de factures entre {} et {}", startDate, endDate);

            // Récupérer toutes les lignes de factures avec les vendeurs pour la période
            List<InvoicesLines> imports = invoicesLinesRepository
                    .findByDateBetweenWithSeller(startDate, endDate);

            logger.info("Nombre de lignes de factures récupérées: {}", imports.size());

            // Supprimer d'abord toutes les factures existantes de la période
            invoicesRepository.deleteByDateBetween(startDate, endDate);

            // Grouper les imports par référence de facture
            Map<String, List<InvoicesLines>> groupedInvoices = imports.stream()
                    .collect(Collectors.groupingBy(InvoicesLines::getInvoiceRef));

            // Créer une liste pour stocker les nouvelles factures
            List<Invoices> invoicesList = new ArrayList<>();

            // Traiter chaque groupe
            groupedInvoices.forEach((invoiceRef, invoiceLines) -> {
                // Prendre la première ligne pour l'information commune
                InvoicesLines firstLine = invoiceLines.get(0);

                // Vérifier si au moins une ligne concerne des verres (famille VER)
                boolean isOptical = invoiceLines.stream()
                        .anyMatch(line -> "VER".equalsIgnoreCase(line.getFamily()));

                // Créer une nouvelle facture
                Invoices invoice = new Invoices();
                invoice.setDate(firstLine.getDate());
                invoice.setClientId(firstLine.getClientId());
                invoice.setClient(firstLine.getClient());
                invoice.setInvoiceRef(invoiceRef);
                invoice.setSeller(firstLine.getSeller());
                invoice.setTotalInvoice(firstLine.getTotalInvoice());
                invoice.setStatus(firstLine.getStatus());
                invoice.setIsOptical(isOptical);
                invoice.setCreatedAt(LocalDateTime.now());

                invoicesList.add(invoice);
            });

            // Sauvegarder par lots
            if (!invoicesList.isEmpty()) {
                logger.info("Création de {} factures", invoicesList.size());
                for (int i = 0; i < invoicesList.size(); i += BATCH_SIZE) {
                    int end = Math.min(i + BATCH_SIZE, invoicesList.size());
                    invoicesRepository.saveAll(invoicesList.subList(i, end));
                }
            }

        } catch (Exception e) {
            logger.error("Erreur lors de la création des entrées de factures", e);
            throw new RuntimeException("Erreur lors de la création des entrées de factures", e);
        }
    }
}
