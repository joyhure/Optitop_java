package com.optitop.optitop_api.service;

import com.optitop.optitop_api.model.Invoice;
import com.optitop.optitop_api.model.Quotation;
import com.optitop.optitop_api.model.Seller;
import com.optitop.optitop_api.repository.InvoiceRepository;
import com.optitop.optitop_api.repository.QuotationRepository;
import com.optitop.optitop_api.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesService.class);
    private static final int BATCH_SIZE = 1000;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Transactional
    public void processBatch(List<String> lines) {
        if (lines.isEmpty())
            return;

        // Déterminer la plage de dates du fichier
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate minDate = LocalDate.MAX;
        LocalDate maxDate = LocalDate.MIN;

        // Première passe pour déterminer la plage de dates
        for (String line : lines) {
            try {
                String[] columns = line.split(";");
                LocalDate date = LocalDate.parse(columns[0], formatter);
                if (date.isBefore(minDate))
                    minDate = date;
                if (date.isAfter(maxDate))
                    maxDate = date;
            } catch (Exception e) {
                logger.warn("Erreur lors de la lecture de la date : " + line);
            }
        }

        // Supprimer toutes les données de la période
        logger.info("Suppression des données entre " + minDate + " et " + maxDate);
        invoiceRepository.deleteByDateBetween(minDate, maxDate);
        quotationRepository.deleteByDateBetween(minDate, maxDate);

        // Traitement par lots pour l'insertion
        List<Invoice> invoiceBatch = new ArrayList<>();
        List<Quotation> quotationBatch = new ArrayList<>();

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
                    invoice.setDateImport(LocalDate.now());

                    invoiceBatch.add(invoice);
                    if (invoiceBatch.size() >= BATCH_SIZE) {
                        invoiceRepository.saveAll(invoiceBatch);
                        invoiceBatch.clear();
                    }
                } else if (type.contains("devis")) {
                    Quotation quotation = new Quotation();
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
                    quotation.setDateImport(LocalDate.now());

                    quotationBatch.add(quotation);
                    if (quotationBatch.size() >= BATCH_SIZE) {
                        quotationRepository.saveAll(quotationBatch);
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
            quotationRepository.saveAll(quotationBatch);
        }
    }
}
