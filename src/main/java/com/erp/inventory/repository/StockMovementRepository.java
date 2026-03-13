package com.erp.inventory.repository;

import com.erp.inventory.domain.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockMovementRepository extends JpaRepository<StockMovement, String> {

    @Query("SELECT sm FROM StockMovement sm WHERE sm.productId = :productId AND sm.direction = 'IN' AND sm.availableQuantity > 0 ORDER BY sm.movementDate ASC")
    List<StockMovement> findAvailableInMovements(@Param("productId") String productId);
}
