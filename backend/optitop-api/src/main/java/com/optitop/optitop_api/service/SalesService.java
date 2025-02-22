package com.optitop.optitop_api.service;

import com.optitop.optitop_api.model.Invoice;
import com.optitop.optitop_api.model.Quotation;
import com.optitop.optitop_api.model.Seller;
import com.optitop.optitop_api.repository.InvoiceRepository;
import com.optitop.optitop_api.repository.QuotationRepository;
import com.optitop.optitop_api.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesService.class);

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private SellerRepository sellerRepository;

    public void processBatch(List<String> batch) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (String line : batch) {
            try {
                String[] columns = line.split(";");

                String type = columns[14].toLowerCase();
                String sellerRef = columns[10];

                // Vérifier si le seller_ref existe, sinon l'ajouter
                if (!sellerRepository.existsById(sellerRef)) {
                    Seller newSeller = new Seller();
                    newSeller.setSellerRef(sellerRef);
                    sellerRepository.save(newSeller);
                    logger.info("Nouveau vendeur ajouté avec la référence : " + sellerRef);
                }

                if (type.contains("facture") || type.contains("avoir")) {
                    Invoice invoice = new Invoice();
                    invoice.setDate(LocalDate.parse(columns[0], formatter));
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

                    invoiceRepository.save(invoice);
                } else if (type.contains("devis")) {
                    Quotation quotation = new Quotation();
                    quotation.setDate(LocalDate.parse(columns[0], formatter));
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

                    quotationRepository.save(quotation);
                }
            } catch (Exception e) {
                logger.error("Erreur lors du traitement de la ligne : " + line, e);
            }
        }
    }
}
