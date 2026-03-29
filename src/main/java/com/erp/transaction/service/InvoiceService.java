package com.erp.transaction.service;

import com.erp.transaction.domain.Invoice;
import com.erp.transaction.domain.InvoiceLine;
import com.erp.transaction.repository.InvoiceRepository;
import com.erp.transaction.domain.PurchaseOrder;
import com.erp.inventory.domain.StockMovement;
import com.erp.inventory.repository.StockMovementRepository;
import com.erp.inventory.service.FifoValuationService;
import com.erp.accounting.domain.JournalEntry;
import com.erp.accounting.domain.Posting;
import com.erp.accounting.repository.JournalEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PurchaseOrderService poService;
    private final StockMovementRepository stockMovementRepository;
    private final FifoValuationService fifoValuationService;
    private final JournalEntryRepository journalEntryRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, PurchaseOrderService poService,
                          StockMovementRepository stockMovementRepository, FifoValuationService fifoValuationService,
                          JournalEntryRepository journalEntryRepository) {
        this.invoiceRepository = invoiceRepository;
        this.poService = poService;
        this.stockMovementRepository = stockMovementRepository;
        this.fifoValuationService = fifoValuationService;
        this.journalEntryRepository = journalEntryRepository;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(String id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    @Transactional
    public Invoice saveInvoice(Invoice invoice) {
        if (invoice.getId() == null || invoice.getId().isEmpty()) {
            invoice.setId(UUID.randomUUID().toString());
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalGoodsValue = BigDecimal.ZERO;
        BigDecimal totalCogs = BigDecimal.ZERO;

        String direction = invoice.getType() == Invoice.InvoiceType.SALES ? "OUT" : "IN";

        if (invoice.getLines() != null) {
            for (InvoiceLine line : invoice.getLines()) {
                if (line.getId() == null || line.getId().isEmpty()) {
                    line.setId(UUID.randomUUID().toString());
                }
                line.setInvoice(invoice);

                // Calculate line totals
                BigDecimal lineGoodsValue = line.getQuantity().multiply(line.getUnitPrice());
                BigDecimal taxAmount = lineGoodsValue.multiply(line.getTaxRate().divide(new BigDecimal("100")));
                line.setTaxAmount(taxAmount);
                line.setTotalAmount(lineGoodsValue.add(taxAmount));

                totalGoodsValue = totalGoodsValue.add(lineGoodsValue);
                totalTax = totalTax.add(taxAmount);
                totalAmount = totalAmount.add(line.getTotalAmount());

                // Inventory Movement (Automated Update)
                if (invoice.getStatus() == Invoice.Status.SUBMITTED) {
                    StockMovement movement = new StockMovement();
                    movement.setProductId(line.getProductId());
                    movement.setGodownId("DEFAULT"); // Could be passed in line item
                    movement.setQuantity(line.getQuantity());
                    movement.setDirection(StockMovement.MovementDirection.valueOf(direction));
                    movement.setUnitCost(line.getUnitPrice());
                    movement.setAvailableQuantity(direction.equals("IN") ? movement.getQuantity() : BigDecimal.ZERO);
                    movement.setMovementDate(invoice.getInvoiceDate());
                    stockMovementRepository.save(movement);

                    if (direction.equals("OUT")) {
                        BigDecimal cogs = fifoValuationService.calculateCogs(line.getProductId(), line.getQuantity());
                        totalCogs = totalCogs.add(cogs);
                    }
                }
            }
        }

        invoice.setGstPayable(totalTax);
        totalAmount = totalAmount.add(invoice.getOtherCharges() != null ? invoice.getOtherCharges() : BigDecimal.ZERO);
        invoice.setTotalAmount(totalAmount);

        // Linking PO
        if (invoice.getLinkedPoId() != null && invoice.getStatus() == Invoice.Status.SUBMITTED) {
            poService.updateStatus(invoice.getLinkedPoId(), PurchaseOrder.Status.COMPLETED);
        }

        invoice = invoiceRepository.save(invoice);

        // Automated Accounting
        if (invoice.getStatus() == Invoice.Status.SUBMITTED) {
            generateAccountingEntry(invoice, totalGoodsValue, totalTax, totalCogs, totalAmount);
        }

        return invoice;
    }

    @Transactional
    public Invoice cancelInvoice(String id) {
        Invoice invoice = getInvoiceById(id);
        if (invoice != null && invoice.getStatus() != Invoice.Status.CANCELLED) {
            invoice.setStatus(Invoice.Status.CANCELLED);
            // Reverse Stock Movements? (Simplified: Just mark cancelled for now)
            // Reverse Accounting? (Simplified: Just mark cancelled for now)
            return invoiceRepository.save(invoice);
        }
        return null;
    }

    private void generateAccountingEntry(Invoice invoice, BigDecimal totalGoodsValue, BigDecimal totalTax, BigDecimal totalCogs, BigDecimal totalAmount) {
        JournalEntry je = new JournalEntry();
        je.setEntryId(UUID.randomUUID().toString());
        je.setTransactionDate(invoice.getInvoiceDate());
        je.setVoucherType(invoice.getType() + " Invoice");
        je.setNarration("Auto-generated for Invoice: " + invoice.getInvoiceNumber());

        String partyAccount = "PARTY." + invoice.getPartyId(); // Mock party account mapping

        if (invoice.getType() == Invoice.InvoiceType.SALES) {
            // Debtor UP
            Posting debtor = new Posting(); debtor.setAccountId(partyAccount); debtor.setDebitAmount(totalAmount); je.addPosting(debtor);
            // Sales Revenue UP
            Posting sales = new Posting(); sales.setAccountId("REVENUE.Sales"); sales.setCreditAmount(totalGoodsValue); je.addPosting(sales);
            // Tax Liability UP
            Posting tax = new Posting(); tax.setAccountId("LIABILITY.Tax.GST_Output"); tax.setCreditAmount(totalTax); je.addPosting(tax);

            if (invoice.getOtherCharges() != null && invoice.getOtherCharges().compareTo(BigDecimal.ZERO) > 0) {
                 Posting charges = new Posting(); charges.setAccountId("REVENUE.OtherCharges"); charges.setCreditAmount(invoice.getOtherCharges()); je.addPosting(charges);
            }

            // COGS UP, Inventory DOWN
            if (totalCogs.compareTo(BigDecimal.ZERO) > 0) {
                Posting cogsExpense = new Posting(); cogsExpense.setAccountId("EXPENSE.COGS"); cogsExpense.setDebitAmount(totalCogs); je.addPosting(cogsExpense);
                Posting inventoryAsset = new Posting(); inventoryAsset.setAccountId("ASSET.Inventory"); inventoryAsset.setCreditAmount(totalCogs); je.addPosting(inventoryAsset);
            }
        } else {
            // Creditor UP
            Posting creditor = new Posting(); creditor.setAccountId(partyAccount); creditor.setCreditAmount(totalAmount); je.addPosting(creditor);
            // Purchases UP
            Posting purchase = new Posting(); purchase.setAccountId("EXPENSE.Purchases"); purchase.setDebitAmount(totalGoodsValue); je.addPosting(purchase);
            // ITC UP
            Posting tax = new Posting(); tax.setAccountId("ASSET.Tax.GST_Input"); tax.setDebitAmount(totalTax); je.addPosting(tax);

            if (invoice.getOtherCharges() != null && invoice.getOtherCharges().compareTo(BigDecimal.ZERO) > 0) {
                 Posting charges = new Posting(); charges.setAccountId("EXPENSE.OtherCharges"); charges.setDebitAmount(invoice.getOtherCharges()); je.addPosting(charges);
            }

            // Inventory UP
            if (totalGoodsValue.compareTo(BigDecimal.ZERO) > 0) {
                 Posting inventoryAsset = new Posting(); inventoryAsset.setAccountId("ASSET.Inventory"); inventoryAsset.setDebitAmount(totalGoodsValue); je.addPosting(inventoryAsset);
            }
        }

        je.validateBalance();
        journalEntryRepository.save(je);
    }
}
