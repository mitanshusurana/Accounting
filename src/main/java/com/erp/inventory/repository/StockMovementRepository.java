package com.erp.inventory.repository;

import com.erp.inventory.domain.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, String> {
    List<StockMovement> findByProductIdAndDirectionOrderByMovementDateAsc(String productId, StockMovement.MovementDirection direction);
}
