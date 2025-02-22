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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesService.class);

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Transactional
    public void processBatch(List<String> batch) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Set<String> processedInvoiceRefs = new HashSet<>();
        Set<String> processedQuotationRefs = new HashSet<>();

        LocalDate minDate = LocalDate.MAX;
        LocalDate maxDate = LocalDate.MIN;

        for (String line : batch) {
            try {
                String[] columns = line.split(";");

                String type = columns[14].toLowerCase();
                String sellerRef = columns[10];
                LocalDate date = LocalDate.parse(columns[0], formatter);

                // Déterminer la plage de dates
                if (date.isBefore(minDate)) {
                    minDate = date;
                }
                if (date.isAfter(maxDate)) {
                    maxDate = date;
                }

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

                    // Vérifier si l'enregistrement existe déjà
                    Invoice existingInvoice = invoiceRepository.findByInvoiceRef(invoice.getInvoiceRef());
                    if (existingInvoice != null) {
                        // Mettre à jour l'enregistrement existant
                        existingInvoice.setDate(invoice.getDate());
                        existingInvoice.setClientId(invoice.getClientId());
                        existingInvoice.setClient(invoice.getClient());
                        existingInvoice.setFamily(invoice.getFamily());
                        existingInvoice.setQuantity(invoice.getQuantity());
                        existingInvoice.setTotalTtc(invoice.getTotalTtc());
                        existingInvoice.setSellerRef(invoice.getSellerRef());
                        existingInvoice.setTotalInvoice(invoice.getTotalInvoice());
                        existingInvoice.setPair(invoice.getPair());
                        existingInvoice.setStatus(invoice.getStatus());
                        existingInvoice.setDateImport(invoice.getDateImport());

                        invoiceRepository.save(existingInvoice);
                    } else {
                        // Insérer un nouvel enregistrement
                        invoiceRepository.save(invoice);
                    }
                    processedInvoiceRefs.add(invoice.getInvoiceRef());
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

                    // Vérifier si l'enregistrement existe déjà
                    Quotation existingQuotation = quotationRepository.findByQuotationRef(quotation.getQuotationRef());
                    if (existingQuotation != null) {
                        // Mettre à jour l'enregistrement existant
                        existingQuotation.setDate(quotation.getDate());
                        existingQuotation.setClientId(quotation.getClientId());
                        existingQuotation.setClient(quotation.getClient());
                        existingQuotation.setFamily(quotation.getFamily());
                        existingQuotation.setQuantity(quotation.getQuantity());
                        existingQuotation.setTotalTtc(quotation.getTotalTtc());
                        existingQuotation.setSellerRef(quotation.getSellerRef());
                        existingQuotation.setTotalQuotation(quotation.getTotalQuotation());
                        existingQuotation.setPair(quotation.getPair());
                        existingQuotation.setStatus(quotation.getStatus());
                        existingQuotation.setDateImport(quotation.getDateImport());

                        quotationRepository.save(existingQuotation);
                    } else {
                        // Insérer un nouvel enregistrement
                        quotationRepository.save(quotation);
                    }
                    processedQuotationRefs.add(quotation.getQuotationRef());
                }
            } catch (Exception e) {
                logger.error("Erreur lors du traitement de la ligne : " + line, e);
            }
        }

        // Supprimer les enregistrements qui ne sont pas dans le fichier CSV pour la
        // plage de dates
        invoiceRepository.deleteByInvoiceRefNotInAndDateBetween(processedInvoiceRefs, minDate, maxDate);
        quotationRepository.deleteByQuotationRefNotInAndDateBetween(processedQuotationRefs, minDate, maxDate);
    }
}
