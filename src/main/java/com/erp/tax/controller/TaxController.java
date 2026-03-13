package com.erp.tax.controller;

import com.erp.tax.service.Gstr1JsonSynthesizer;
import com.erp.tax.service.Gstr1JsonSynthesizer.InvoiceData;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tax")
@CrossOrigin(origins = "*")
public class TaxController {

    private final Gstr1JsonSynthesizer gstr1JsonSynthesizer;

    public TaxController(Gstr1JsonSynthesizer gstr1JsonSynthesizer) {
        this.gstr1JsonSynthesizer = gstr1JsonSynthesizer;
    }

    @PostMapping("/gstr1/generate")
    public Map<String, Object> generateGstr1(@RequestBody(required = false) List<InvoiceData> invoices) {
        // In a complete implementation, this would fetch invoices from the DB
        // based on a date range instead of accepting them in the request body.
        if (invoices == null || invoices.isEmpty()) {
            // Mocking a response so the UI button works without a complex setup payload
            InvoiceData mockInv = new InvoiceData();
            mockInv.setInvoiceNumber("INV-1001");
            mockInv.setCustomerGstin("27AADCB2230M1Z2");
            mockInv.setTotalValue(new java.math.BigDecimal("1180.00"));

            Gstr1JsonSynthesizer.InvoiceLineItem item = new Gstr1JsonSynthesizer.InvoiceLineItem();
            item.setTaxableValue(new java.math.BigDecimal("1000.00"));
            item.setTaxRate(new java.math.BigDecimal("18.0"));
            mockInv.getLineItems().add(item);

            invoices = List.of(mockInv);
        }
        return gstr1JsonSynthesizer.synthesizeGstr1Payload(invoices);
    }
}
