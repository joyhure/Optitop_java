package com.optitop.optitop_api.service;

import com.optitop.optitop_api.model.Invoice;
import com.optitop.optitop_api.model.QuotationImport;
import com.optitop.optitop_api.model.Quotations;
import com.optitop.optitop_api.model.Seller;
import com.optitop.optitop_api.repository.InvoiceRepository;
import com.optitop.optitop_api.repository.QuotationImportRepository;
import com.optitop.optitop_api.repository.SellerRepository;
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
    private InvoiceRepository invoiceRepository;

    @Autowired
    private QuotationImportRepository quotationImportRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private QuotationsRepository quotationsRepository;

    @Transactional
    public void processBatch(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            logger.warn("Aucune ligne à traiter");
            return;
        }

        try {
            // Déterminer la plage de dates
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate minDate = lines.stream()
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

            LocalDate maxDate = lines.stream()
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
            invoiceRepository.deleteByDateBetween(minDate, maxDate);
            quotationImportRepository.deleteByDateBetween(minDate, maxDate);

            // Traitement par lots pour l'insertion
            List<Invoice> invoiceBatch = new ArrayList<>();
            List<QuotationImport> quotationBatch = new ArrayList<>();

            for (String line : lines) {
                try {
                    String[] columns = line.split(";");

                    String type = columns[14].toLowerCase();
                    String sellerRef = columns[10];
                    LocalDate date = LocalDate.parse(columns[0], formatter);

                    // Vérifier si le seller_ref existe, sinon l'ajouter
                    if (!sellerRepository.existsById(sellerRef)) {
                        Seller newSeller = new Seller();
                        newSeller.setSellerRef(sellerRef);
                        sellerRepository.save(newSeller);
                        logger.info("Nouveau vendeur ajouté avec la référence : " + sellerRef);
                    }

                    if (type.contains("facture") || type.contains("avoir")) {
                        Invoice invoice = new Invoice();
                        invoice.setDate(date);
                        invoice.setClientId(columns[2]);
                        invoice.setClient(columns[3]);
                        invoice.setInvoiceRef(columns[4]);
                        invoice.setFamily(columns[5]);
                        invoice.setQuantity(Integer.parseInt(columns[6]));
                        invoice.setTotalTtc(Double.parseDouble(columns[8].replace(",", ".")));
                        invoice.setSellerRef(sellerRef);
                        invoice.setTotalInvoice(Double.parseDouble(columns[12].replace(",", ".")));
                        invoice.setPair(columns[13].isEmpty() ? null : Integer.parseInt(columns[13]));
                        invoice.setStatus(type);

                        invoiceBatch.add(invoice);
                        if (invoiceBatch.size() >= BATCH_SIZE) {
                            invoiceRepository.saveAll(invoiceBatch);
                            invoiceBatch.clear();
                        }
                    } else if (type.contains("devis")) {
                        QuotationImport quotation = new QuotationImport();
                        quotation.setDate(date);
                        quotation.setClientId(columns[2]);
                        quotation.setClient(columns[3]);
                        quotation.setQuotationRef(columns[4]);
                        quotation.setFamily(columns[5]);
                        quotation.setQuantity(Integer.parseInt(columns[6]));
                        quotation.setTotalTtc(Double.parseDouble(columns[8].replace(",", ".")));
                        quotation.setSellerRef(sellerRef);
                        quotation.setTotalQuotation(Double.parseDouble(columns[12].replace(",", ".")));
                        quotation.setPair(columns[13].isEmpty() ? null : Integer.parseInt(columns[13]));
                        quotation.setStatus(type);

                        quotationBatch.add(quotation);
                        if (quotationBatch.size() >= BATCH_SIZE) {
                            quotationImportRepository.saveAll(quotationBatch);
                            quotationBatch.clear();
                        }
                    }
                } catch (Exception e) {
                    logger.error("Erreur lors du traitement de la ligne : " + line, e);
                }
            }

            // Sauvegarder les derniers lots
            if (!invoiceBatch.isEmpty()) {
                invoiceRepository.saveAll(invoiceBatch);
            }
            if (!quotationBatch.isEmpty()) {
                quotationImportRepository.saveAll(quotationBatch);
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
            List<QuotationImport> imports = quotationImportRepository
                    .findByDateBetweenAndFamilyIn(startDate, endDate, Arrays.asList("VER", "MON"));

            // Grouper par clientId et date
            Map<String, Map<LocalDate, List<QuotationImport>>> groupedQuotations = imports.stream()
                    .collect(Collectors.groupingBy(QuotationImport::getClientId,
                            Collectors.groupingBy(QuotationImport::getDate)));

            // Créer les entrées dans quotations
            List<Quotations> quotationsList = new ArrayList<>();

            groupedQuotations.forEach((clientId, dateMap) -> {
                dateMap.forEach((date, quotationImports) -> {
                    // Vérifier si au moins un devis est validé
                    boolean hasValidatedQuotation = quotationImports.stream()
                            .anyMatch(q -> "devis validé".equals(q.getStatus()));

                    // Créer l'entrée dans quotations
                    Quotations quotation = new Quotations();
                    quotation.setDate(date);
                    quotation.setClientId(clientId);
                    quotation.setClient(quotationImports.get(0).getClient());
                    quotation.setSellerRef(quotationImports.get(0).getSellerRef());
                    quotation.setStatus(hasValidatedQuotation ? "Validé" : "Non validé");
                    quotation.setCreatedAt(LocalDateTime.now());

                    quotationsList.add(quotation);
                });
            });

            // Sauvegarder les nouvelles entrées par lots
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
