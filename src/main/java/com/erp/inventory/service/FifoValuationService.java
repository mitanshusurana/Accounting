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

    public BigDecimal calculateCogs(String productId, BigDecimal quantitySold) {
        List<StockMovement> inMovements = stockMovementRepository.findByProductIdAndDirectionOrderByMovementDateAsc(
                productId, StockMovement.MovementDirection.IN);

        BigDecimal remainingQuantityToSell = quantitySold;
        BigDecimal totalCogs = BigDecimal.ZERO;

        for (StockMovement inMovement : inMovements) {
            if (remainingQuantityToSell.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            // We need a way to track the remaining quantity of an IN movement.
            // For the sake of simplicity in this calculation, we assume
            // a naive calculation that does not persist the "used" amount
            // on the IN movement itself, which would be needed in a real system.
            // Assuming full quantity is available for this simple FIFO implementation.
            BigDecimal availableQuantity = inMovement.getQuantity();

            BigDecimal quantityToUse = availableQuantity.min(remainingQuantityToSell);

            BigDecimal costForThisBatch = quantityToUse.multiply(inMovement.getUnitCost());
            totalCogs = totalCogs.add(costForThisBatch);

            remainingQuantityToSell = remainingQuantityToSell.subtract(quantityToUse);
        }

        if (remainingQuantityToSell.compareTo(BigDecimal.ZERO) > 0) {
            // Not enough stock to fulfill the sale
            throw new RuntimeException("Insufficient stock for FIFO valuation. Missing: " + remainingQuantityToSell);
        }

        return totalCogs;
    }
}
