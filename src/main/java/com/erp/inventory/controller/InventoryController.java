package com.erp.inventory.controller;

import com.erp.inventory.domain.Godown;
import com.erp.inventory.domain.Product;
import com.erp.inventory.domain.StockMovement;
import com.erp.inventory.repository.GodownRepository;
import com.erp.inventory.repository.ProductRepository;
import com.erp.inventory.repository.StockMovementRepository;
import com.erp.inventory.service.FifoValuationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*") // Allow frontend access
public class InventoryController {

    private final ProductRepository productRepository;
    private final GodownRepository godownRepository;
    private final StockMovementRepository stockMovementRepository;
    private final FifoValuationService fifoValuationService;

    public InventoryController(ProductRepository productRepository, GodownRepository godownRepository,
                               StockMovementRepository stockMovementRepository, FifoValuationService fifoValuationService) {
        this.productRepository = productRepository;
        this.godownRepository = godownRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.fifoValuationService = fifoValuationService;
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PostMapping("/products")
    public Product createProduct(@RequestBody Product product) {
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
        // Simplified combined save logic for PoC.
        // In a real system, this handles the JournalEntry and loops through StockMovements.
        // It validates Debits = Credits (Sales Account + Tax vs Debtors)

        try {
            List<Map<String, Object>> items = (List<Map<String, Object>>) invoicePayload.get("items");
            String invoiceType = (String) invoicePayload.get("invoiceType");
            String direction = invoiceType.equalsIgnoreCase("Sales") ? "OUT" : "IN";

            for (Map<String, Object> item : items) {
                StockMovement movement = new StockMovement();
                movement.setProductId((String) item.get("productId"));
                movement.setGodownId((String) item.get("godownId"));
                movement.setQuantity(new BigDecimal(item.get("quantity").toString()));
                movement.setDirection(StockMovement.MovementDirection.valueOf(direction));
                movement.setUnitCost(new BigDecimal(item.get("rate").toString()));
                movement.setAvailableQuantity(direction.equals("IN") ? movement.getQuantity() : BigDecimal.ZERO);
                stockMovementRepository.save(movement);
            }

            return ResponseEntity.ok(Map.of("status", "success", "message", "Invoice and Stock saved."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
