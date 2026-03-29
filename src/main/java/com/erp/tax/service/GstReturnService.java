package com.erp.tax.service;

import com.erp.transaction.domain.Invoice;
import com.erp.transaction.domain.InvoiceLine;
import com.erp.transaction.repository.InvoiceRepository;
import com.erp.master.domain.Party;
import com.erp.master.repository.PartyRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class GstReturnService {

    private final InvoiceRepository invoiceRepository;
    private final PartyRepository partyRepository;
    private final Gstr1JsonSynthesizer gstr1JsonSynthesizer;

    public GstReturnService(InvoiceRepository invoiceRepository, PartyRepository partyRepository, Gstr1JsonSynthesizer gstr1JsonSynthesizer) {
        this.invoiceRepository = invoiceRepository;
        this.partyRepository = partyRepository;
        this.gstr1JsonSynthesizer = gstr1JsonSynthesizer;
    }

    public Map<String, Object> generateGstr1(LocalDate startDate, LocalDate endDate) {
        List<Invoice> salesInvoices = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getType() == Invoice.InvoiceType.SALES && inv.getStatus() == Invoice.Status.SUBMITTED)
                .filter(inv -> !inv.getInvoiceDate().isBefore(startDate) && !inv.getInvoiceDate().isAfter(endDate))
                .toList();

        List<Gstr1JsonSynthesizer.InvoiceData> invoiceDataList = new ArrayList<>();

        for (Invoice invoice : salesInvoices) {
            Party customer = partyRepository.findById(invoice.getPartyId()).orElse(null);

            Gstr1JsonSynthesizer.InvoiceData data = new Gstr1JsonSynthesizer.InvoiceData();
            data.setInvoiceNumber(invoice.getInvoiceNumber());
            data.setInvoiceDate(invoice.getInvoiceDate().toString());
            data.setTotalValue(invoice.getTotalAmount());
            data.setCustomerGstin(customer != null && customer.getGstin() != null ? customer.getGstin() : "URD");
            // Simplified Place of Supply
            data.setPlaceOfSupply("27-Maharashtra");

            int lineNum = 1;
            for (InvoiceLine line : invoice.getLines()) {
                Gstr1JsonSynthesizer.InvoiceLineItem item = new Gstr1JsonSynthesizer.InvoiceLineItem();
                item.setLineNumber(lineNum++);

                BigDecimal taxableValue = line.getQuantity().multiply(line.getUnitPrice());
                item.setTaxableValue(taxableValue);
                item.setTaxRate(line.getTaxRate());

                // Assuming Intra-state (CGST + SGST) for simplicity if place of supply is same state.
                // In a real system, derive from GSTIN prefix
                BigDecimal halfTax = line.getTaxAmount().divide(new BigDecimal("2"));
                item.setCgstAmount(halfTax);
                item.setSgstAmount(halfTax);
                item.setIgstAmount(BigDecimal.ZERO);

                data.getLineItems().add(item);
            }
            invoiceDataList.add(data);
        }

        return gstr1JsonSynthesizer.synthesizeGstr1Payload(invoiceDataList);
    }

    public Map<String, Object> generateGstr3b(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> gstr3b = new HashMap<>();

        // Outward Supplies (Sales)
        BigDecimal totalOutwardTaxable = BigDecimal.ZERO;
        BigDecimal totalOutwardTax = BigDecimal.ZERO;

        List<Invoice> salesInvoices = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getType() == Invoice.InvoiceType.SALES && inv.getStatus() == Invoice.Status.SUBMITTED)
                .filter(inv -> !inv.getInvoiceDate().isBefore(startDate) && !inv.getInvoiceDate().isAfter(endDate))
                .toList();

        for (Invoice invoice : salesInvoices) {
            totalOutwardTax = totalOutwardTax.add(invoice.getGstPayable());
            for (InvoiceLine line : invoice.getLines()) {
                totalOutwardTaxable = totalOutwardTaxable.add(line.getQuantity().multiply(line.getUnitPrice()));
            }
        }

        // Eligible ITC (Purchases)
        BigDecimal totalItc = BigDecimal.ZERO;

        List<Invoice> purchaseInvoices = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getType() == Invoice.InvoiceType.PURCHASE && inv.getStatus() == Invoice.Status.SUBMITTED)
                .filter(inv -> !inv.getInvoiceDate().isBefore(startDate) && !inv.getInvoiceDate().isAfter(endDate))
                .toList();

        for (Invoice invoice : purchaseInvoices) {
            totalItc = totalItc.add(invoice.getGstPayable());
        }

        Map<String, Object> outward = new HashMap<>();
        outward.put("taxable_value", totalOutwardTaxable);
        outward.put("tax_amount", totalOutwardTax);
        gstr3b.put("outward_supplies", outward);
        gstr3b.put("eligible_itc", totalItc);

        return gstr3b;
    }

    public List<Map<String, Object>> generateGstr2aMismatchReport(List<Map<String, Object>> gstr2aData, LocalDate startDate, LocalDate endDate) {
        // Simple logic: Compare GSTR2A JSON data with Local Purchase Register
        List<Invoice> localPurchases = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getType() == Invoice.InvoiceType.PURCHASE && inv.getStatus() == Invoice.Status.SUBMITTED)
                .filter(inv -> !inv.getInvoiceDate().isBefore(startDate) && !inv.getInvoiceDate().isAfter(endDate))
                .toList();

        List<Map<String, Object>> mismatches = new ArrayList<>();

        for (Map<String, Object> portalInv : gstr2aData) {
            String invNum = (String) portalInv.get("inum");
            BigDecimal portalTax = new BigDecimal(portalInv.get("tax_amount").toString());

            Invoice matchedLocal = localPurchases.stream()
                    .filter(i -> i.getInvoiceNumber().equalsIgnoreCase(invNum))
                    .findFirst().orElse(null);

            if (matchedLocal == null) {
                mismatches.add(Map.of("invoiceNumber", invNum, "status", "Missing in Local", "portalTax", portalTax, "localTax", 0));
            } else if (matchedLocal.getGstPayable().compareTo(portalTax) != 0) {
                mismatches.add(Map.of("invoiceNumber", invNum, "status", "Tax Mismatch", "portalTax", portalTax, "localTax", matchedLocal.getGstPayable()));
            }
        }

        for (Invoice local : localPurchases) {
             boolean foundOnPortal = gstr2aData.stream().anyMatch(p -> p.get("inum").toString().equalsIgnoreCase(local.getInvoiceNumber()));
             if (!foundOnPortal) {
                 mismatches.add(Map.of("invoiceNumber", local.getInvoiceNumber(), "status", "Missing on Portal (Not uploaded by Supplier)", "portalTax", 0, "localTax", local.getGstPayable()));
             }
        }

        return mismatches;
    }
}
