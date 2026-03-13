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
}
