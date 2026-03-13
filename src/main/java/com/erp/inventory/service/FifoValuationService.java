package com.erp.inventory.service;

import com.erp.inventory.domain.StockMovement;
import com.erp.inventory.repository.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FifoValuationService {

    private final StockMovementRepository stockMovementRepository;

    public FifoValuationService(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    @org.springframework.transaction.annotation.Transactional
    public BigDecimal calculateCogs(String productId, BigDecimal quantitySold) {
        List<StockMovement> inMovements = stockMovementRepository.findAvailableInMovements(productId);

        BigDecimal remainingQuantityToSell = quantitySold;
        BigDecimal totalCogs = BigDecimal.ZERO;

        for (StockMovement inMovement : inMovements) {
            if (remainingQuantityToSell.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal availableQuantity = inMovement.getAvailableQuantity();
            if (availableQuantity == null) {
                availableQuantity = inMovement.getQuantity();
            }

            BigDecimal quantityToUse = availableQuantity.min(remainingQuantityToSell);

            BigDecimal costForThisBatch = quantityToUse.multiply(inMovement.getUnitCost());
            totalCogs = totalCogs.add(costForThisBatch);

            // Persist the consumed stock amount back to the DB to ensure accurate FIFO for subsequent sales
            inMovement.setAvailableQuantity(availableQuantity.subtract(quantityToUse));
            stockMovementRepository.save(inMovement);

            remainingQuantityToSell = remainingQuantityToSell.subtract(quantityToUse);
        }

        if (remainingQuantityToSell.compareTo(BigDecimal.ZERO) > 0) {
            // Not enough stock to fulfill the sale
            throw new RuntimeException("Insufficient stock for FIFO valuation. Missing: " + remainingQuantityToSell);
        }

        return totalCogs;
    }
}
