package com.optitop.optitop_api.service;

import com.optitop.optitop_api.model.InvoicesLines;
import com.optitop.optitop_api.model.QuotationsLines;
import com.optitop.optitop_api.model.Quotations;
import com.optitop.optitop_api.model.Seller;
import com.optitop.optitop_api.model.User;
import com.optitop.optitop_api.repository.InvoicesLinesRepository;
import com.optitop.optitop_api.repository.QuotationsLinesRepository;
import com.optitop.optitop_api.repository.SellerRepository;
import com.optitop.optitop_api.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import com.optitop.optitop_api.repository.QuotationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
public class SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesService.class);
    private static final int BATCH_SIZE = 1000;

    @Autowired
    private InvoicesLinesRepository invoicesLinesRepository;

    @Autowired
    private QuotationsLinesRepository quotationsLinesRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private QuotationsRepository quotationsRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void processBatch(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            logger.warn("Aucune ligne à traiter");
            return;
        }

        try {
            // Ignorer la ligne d'en-tête si présente
            List<String> dataLines = new ArrayList<>(lines);
            if (!dataLines.isEmpty() && dataLines.get(0).contains("Date;C.;Num client;Client;")) {
                logger.info("Détection d'une ligne d'en-tête, suppression");
                dataLines.remove(0);
            }

            if (dataLines.isEmpty()) {
                logger.warn("Aucune ligne de données après suppression de l'en-tête");
                return;
            }

            // Déterminer la plage de dates
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate minDate = dataLines.stream()
                    .map(line -> {
                        try {
                            String[] columns = line.split(";");
                            return LocalDate.parse(columns[0], formatter);
                        } catch (Exception e) {
                            logger.error("Erreur lors du parsing de la date: " + line, e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .min(LocalDate::compareTo)
                    .orElseThrow(() -> new IllegalArgumentException("Aucune date valide trouvée"));

            LocalDate maxDate = dataLines.stream()
                    .map(line -> {
                        try {
                            String[] columns = line.split(";");
                            return LocalDate.parse(columns[0], formatter);
                        } catch (Exception e) {
                            logger.error("Erreur lors du parsing de la date: " + line, e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .max(LocalDate::compareTo)
                    .orElseThrow(() -> new IllegalArgumentException("Aucune date valide trouvée"));

            // Supprimer toutes les données de la période
            logger.info("Suppression des données entre " + minDate + " et " + maxDate);
            invoicesLinesRepository.deleteByDateBetween(minDate, maxDate);
            quotationsLinesRepository.deleteByDateBetween(minDate, maxDate);

            // Traitement par lots pour l'insertion
            List<InvoicesLines> invoiceBatch = new ArrayList<>();
            List<QuotationsLines> quotationBatch = new ArrayList<>();

            for (String line : dataLines) {
                try {
                    String[] columns = line.split(";");

                    String type = columns[14].toLowerCase();
                    String sellerRef = columns[10];
                    LocalDate date = LocalDate.parse(columns[0], formatter);

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
                } catch (Exception e) {
                    logger.error("Erreur lors du traitement de la ligne : " + line, e);
                }
            }

            // Sauvegarder les derniers lots
            if (!invoiceBatch.isEmpty()) {
                invoicesLinesRepository.saveAll(invoiceBatch);
            }
            if (!quotationBatch.isEmpty()) {
                quotationsLinesRepository.saveAll(quotationBatch);
            }

            // Après le traitement de toutes les lignes, créer les entrées dans quotations
            createQuotationsEntries(minDate, maxDate);
        } catch (Exception e) {
            logger.error("Erreur lors du traitement du lot", e);
            throw new RuntimeException("Erreur lors du traitement du lot", e);
        }
    }

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
}
