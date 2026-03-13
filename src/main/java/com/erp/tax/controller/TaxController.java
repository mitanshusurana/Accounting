package com.erp.tax.controller;

import com.erp.tax.service.Gstr1JsonSynthesizer;
import com.erp.tax.service.Gstr1JsonSynthesizer.InvoiceData;
import com.erp.accounting.repository.JournalEntryRepository;
import com.erp.accounting.domain.JournalEntry;
import com.erp.accounting.domain.Posting;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tax")
@CrossOrigin(origins = "*")
public class TaxController {

    private final Gstr1JsonSynthesizer gstr1JsonSynthesizer;
    private final JournalEntryRepository journalEntryRepository;

    public TaxController(Gstr1JsonSynthesizer gstr1JsonSynthesizer, JournalEntryRepository journalEntryRepository) {
        this.gstr1JsonSynthesizer = gstr1JsonSynthesizer;
        this.journalEntryRepository = journalEntryRepository;
    }

    @GetMapping("/gstr1/generate")
    public Map<String, Object> generateGstr1(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Fetch LIVE Sales Invoices from the core accounting engine
        List<JournalEntry> salesEntries = journalEntryRepository.findByVoucherTypeAndTransactionDateBetween("Sales Invoice", startDate, endDate);
        List<InvoiceData> invoices = new ArrayList<>();

        for (JournalEntry entry : salesEntries) {
            InvoiceData inv = new InvoiceData();
            inv.setInvoiceNumber(entry.getEntryId().substring(0, 8)); // Shortened UUID for PoC UI
            inv.setInvoiceDate(entry.getTransactionDate().toString());
            inv.setCustomerGstin("UNREGISTERED"); // Placeholder for Customer Master GSTIN

            BigDecimal totalValue = BigDecimal.ZERO;
            BigDecimal taxableValue = BigDecimal.ZERO;
            BigDecimal taxAmount = BigDecimal.ZERO;

            for (Posting p : entry.getPostings()) {
                if (p.getAccountId().equals("REVENUE.Sales")) {
                    taxableValue = taxableValue.add(p.getCreditAmount());
                } else if (p.getAccountId().equals("LIABILITY.Tax.GST_Output")) {
                    taxAmount = taxAmount.add(p.getCreditAmount());
                }
            }

            totalValue = taxableValue.add(taxAmount);
            inv.setTotalValue(totalValue);

            Gstr1JsonSynthesizer.InvoiceLineItem line = new Gstr1JsonSynthesizer.InvoiceLineItem();
            line.setLineNumber(1);
            line.setTaxableValue(taxableValue);
            line.setTaxRate(new BigDecimal("18.0")); // Currently fixed in invoice generator
            // Simple logic assuming intrastate for PoC
            line.setCgstAmount(taxAmount.divide(new BigDecimal("2")));
            line.setSgstAmount(taxAmount.divide(new BigDecimal("2")));
            line.setIgstAmount(BigDecimal.ZERO);

            inv.getLineItems().add(line);
            invoices.add(inv);
        }

        return gstr1JsonSynthesizer.synthesizeGstr1Payload(invoices);
    }
}
