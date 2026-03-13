package com.erp.tax.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Gstr1JsonSynthesizer {

    public Map<String, Object> synthesizeGstr1Payload(List<InvoiceData> invoices) {
        Map<String, Object> payload = new HashMap<>();

        List<Map<String, Object>> b2bInvoices = new ArrayList<>();

        for (InvoiceData invoice : invoices) {
            Map<String, Object> invData = new HashMap<>();
            invData.put("inum", invoice.getInvoiceNumber());
            invData.put("idt", invoice.getInvoiceDate());
            invData.put("val", invoice.getTotalValue());
            invData.put("pos", invoice.getPlaceOfSupply());

            List<Map<String, Object>> items = new ArrayList<>();
            for (InvoiceLineItem lineItem : invoice.getLineItems()) {
                Map<String, Object> itm = new HashMap<>();
                itm.put("num", lineItem.getLineNumber());

                Map<String, Object> itmDet = new HashMap<>();
                itmDet.put("txval", lineItem.getTaxableValue());
                itmDet.put("rt", lineItem.getTaxRate());
                itmDet.put("iamt", lineItem.getIgstAmount());
                itmDet.put("camt", lineItem.getCgstAmount());
                itmDet.put("samt", lineItem.getSgstAmount());

                itm.put("itm_det", itmDet);
                items.add(itm);
            }
            invData.put("itms", items);

            Map<String, Object> ctinGroup = new HashMap<>();
            ctinGroup.put("ctin", invoice.getCustomerGstin());
            List<Map<String, Object>> invList = new ArrayList<>();
            invList.add(invData);
            ctinGroup.put("inv", invList);

            b2bInvoices.add(ctinGroup);
        }

        payload.put("b2b", b2bInvoices);

        return payload;
    }

    // Helper classes
    public static class InvoiceData {
        private String customerGstin;
        private String invoiceNumber;
        private String invoiceDate;
        private BigDecimal totalValue;
        private String placeOfSupply;
        private List<InvoiceLineItem> lineItems = new ArrayList<>();

        public String getCustomerGstin() { return customerGstin; }
        public void setCustomerGstin(String customerGstin) { this.customerGstin = customerGstin; }
        public String getInvoiceNumber() { return invoiceNumber; }
        public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
        public String getInvoiceDate() { return invoiceDate; }
        public void setInvoiceDate(String invoiceDate) { this.invoiceDate = invoiceDate; }
        public BigDecimal getTotalValue() { return totalValue; }
        public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
        public String getPlaceOfSupply() { return placeOfSupply; }
        public void setPlaceOfSupply(String placeOfSupply) { this.placeOfSupply = placeOfSupply; }
        public List<InvoiceLineItem> getLineItems() { return lineItems; }
        public void setLineItems(List<InvoiceLineItem> lineItems) { this.lineItems = lineItems; }
    }

    public static class InvoiceLineItem {
        private int lineNumber;
        private BigDecimal taxableValue;
        private BigDecimal taxRate;
        private BigDecimal igstAmount;
        private BigDecimal cgstAmount;
        private BigDecimal sgstAmount;

        public int getLineNumber() { return lineNumber; }
        public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
        public BigDecimal getTaxableValue() { return taxableValue; }
        public void setTaxableValue(BigDecimal taxableValue) { this.taxableValue = taxableValue; }
        public BigDecimal getTaxRate() { return taxRate; }
        public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }
        public BigDecimal getIgstAmount() { return igstAmount; }
        public void setIgstAmount(BigDecimal igstAmount) { this.igstAmount = igstAmount; }
        public BigDecimal getCgstAmount() { return cgstAmount; }
        public void setCgstAmount(BigDecimal cgstAmount) { this.cgstAmount = cgstAmount; }
        public BigDecimal getSgstAmount() { return sgstAmount; }
        public void setSgstAmount(BigDecimal sgstAmount) { this.sgstAmount = sgstAmount; }
    }
}
