package com.erp.inventory.controller;

import com.erp.inventory.domain.Godown;
import com.erp.inventory.domain.Product;
import com.erp.inventory.domain.StockMovement;
import com.erp.inventory.repository.GodownRepository;
import com.erp.inventory.repository.ProductRepository;
import com.erp.inventory.repository.StockMovementRepository;
import com.erp.inventory.service.FifoValuationService;
import com.erp.accounting.domain.JournalEntry;
import com.erp.accounting.domain.Posting;
import com.erp.accounting.repository.JournalEntryRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*") // Allow frontend access
public class InventoryController {

    private final ProductRepository productRepository;
    private final GodownRepository godownRepository;
    private final StockMovementRepository stockMovementRepository;
    private final FifoValuationService fifoValuationService;
    private final JournalEntryRepository journalEntryRepository;

    public InventoryController(ProductRepository productRepository, GodownRepository godownRepository,
                               StockMovementRepository stockMovementRepository, FifoValuationService fifoValuationService,
                               JournalEntryRepository journalEntryRepository) {
        this.productRepository = productRepository;
        this.godownRepository = godownRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.fifoValuationService = fifoValuationService;
        this.journalEntryRepository = journalEntryRepository;
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/products/search")
    public List<Product> searchProducts(@RequestParam String query) {
        return productRepository.searchByBarcodeOrShortCodeOrDescription(query);
    }

    @PostMapping("/products")
    public Product createProduct(@RequestBody Product product) {
        if(product.getProductId() == null || product.getProductId().isEmpty()) {
            product.setProductId(UUID.randomUUID().toString());
        }
        return productRepository.save(product);
    }

    @GetMapping("/godowns")
    public List<Godown> getAllGodowns() {
        return godownRepository.findAll();
    }

    @PostMapping("/godowns")
    public Godown createGodown(@RequestBody Godown godown) {
        return godownRepository.save(godown);
    }

    @PostMapping("/stock-movements")
    @Transactional
    public StockMovement createStockMovement(@RequestBody StockMovement movement) {
        return stockMovementRepository.save(movement);
    }

    @GetMapping("/valuation/{productId}")
    public Map<String, Object> getCogsValuation(@PathVariable String productId, @RequestParam BigDecimal quantity) {
        BigDecimal cogs = fifoValuationService.calculateCogs(productId, quantity);
        Map<String, Object> response = new HashMap<>();
        response.put("productId", productId);
        response.put("quantitySold", quantity);
        response.put("calculatedCogs", cogs);
        return response;
    }

    @PostMapping("/invoice")
    @Transactional
    public ResponseEntity<?> createInvoice(@RequestBody Map<String, Object> invoicePayload) {
        try {
            List<Map<String, Object>> items = (List<Map<String, Object>>) invoicePayload.get("items");
            String invoiceType = (String) invoicePayload.get("invoiceType");
            String partyAccountId = (String) invoicePayload.get("partyAccountId");
            String transactionDateStr = (String) invoicePayload.get("transactionDate");
            String narration = (String) invoicePayload.get("narration");

            String direction = invoiceType.equalsIgnoreCase("Sales") ? "OUT" : "IN";

            BigDecimal totalGoodsValue = BigDecimal.ZERO;
            BigDecimal totalTax = BigDecimal.ZERO;
            BigDecimal totalCogs = BigDecimal.ZERO;

            for (Map<String, Object> item : items) {
                String productId = (String) item.get("productId");
                BigDecimal qty = new BigDecimal(item.get("quantity").toString());
                BigDecimal rate = new BigDecimal(item.get("rate").toString());

                // Inventory Movement
                StockMovement movement = new StockMovement();
                movement.setProductId(productId);
                movement.setGodownId((String) item.get("godownId"));
                movement.setQuantity(qty);
                movement.setDirection(StockMovement.MovementDirection.valueOf(direction));
                movement.setUnitCost(rate);
                movement.setAvailableQuantity(direction.equals("IN") ? movement.getQuantity() : BigDecimal.ZERO);
                movement.setMovementDate(LocalDate.parse(transactionDateStr));
                stockMovementRepository.save(movement);

                // Calculations
                BigDecimal itemValue = qty.multiply(rate);
                totalGoodsValue = totalGoodsValue.add(itemValue);

                // Mock Tax Calculation (18% GST based on HSN)
                BigDecimal taxAmount = itemValue.multiply(new BigDecimal("0.18"));
                totalTax = totalTax.add(taxAmount);

                // COGS Calculation for Sales
                if (direction.equals("OUT")) {
                    BigDecimal cogs = fifoValuationService.calculateCogs(productId, qty);
                    totalCogs = totalCogs.add(cogs);
                }
            }

            // Accounting Journal Entry Automation
            JournalEntry je = new JournalEntry();
            je.setEntryId(UUID.randomUUID().toString());
            je.setTransactionDate(LocalDate.parse(transactionDateStr));
            je.setVoucherType(invoiceType + " Invoice");
            je.setNarration(narration + " | Auto-generated via Invoice");

            if (invoiceType.equalsIgnoreCase("Sales")) {
                // Debtor (Asset) goes UP
                Posting debtor = new Posting();
                debtor.setAccountId(partyAccountId);
                debtor.setDebitAmount(totalGoodsValue.add(totalTax));
                je.addPosting(debtor);

                // Sales Revenue goes UP
                Posting sales = new Posting();
                sales.setAccountId("REVENUE.Sales");
                sales.setCreditAmount(totalGoodsValue);
                je.addPosting(sales);

                // Tax Liability goes UP
                Posting tax = new Posting();
                tax.setAccountId("LIABILITY.Tax.GST_Output");
                tax.setCreditAmount(totalTax);
                je.addPosting(tax);

                // COGS Expense goes UP
                Posting cogsExpense = new Posting();
                cogsExpense.setAccountId("EXPENSE.COGS");
                cogsExpense.setDebitAmount(totalCogs);
                je.addPosting(cogsExpense);

                // Inventory Asset goes DOWN
                Posting inventoryAsset = new Posting();
                inventoryAsset.setAccountId("ASSET.Inventory");
                inventoryAsset.setCreditAmount(totalCogs);
                je.addPosting(inventoryAsset);

            } else {
                // Purchase logic
                Posting creditor = new Posting();
                creditor.setAccountId(partyAccountId);
                creditor.setCreditAmount(totalGoodsValue.add(totalTax));
                je.addPosting(creditor);

                Posting purchase = new Posting();
                purchase.setAccountId("EXPENSE.Purchases");
                purchase.setDebitAmount(totalGoodsValue);
                je.addPosting(purchase);

                Posting tax = new Posting();
                tax.setAccountId("ASSET.Tax.GST_Input"); // Input Tax Credit
                tax.setDebitAmount(totalTax);
                je.addPosting(tax);

                // Inventory Asset goes UP
                Posting inventoryAsset = new Posting();
                inventoryAsset.setAccountId("ASSET.Inventory");
                inventoryAsset.setDebitAmount(totalGoodsValue);
                je.addPosting(inventoryAsset);
            }

            je.validateBalance(); // Ensure Debits == Credits strictly
            journalEntryRepository.save(je);

            return ResponseEntity.ok(Map.of("status", "success", "message", "Invoice, Tax, and Stock saved automatically."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
