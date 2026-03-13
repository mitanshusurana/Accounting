package com.erp.inventory.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String movementId;

    private String productId;

    private String godownId;

    private String batchId;

    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    private MovementDirection direction;

    private BigDecimal unitCost;

    private LocalDate movementDate;

    public enum MovementDirection {
        IN, OUT
    }

    public StockMovement() {}

    public StockMovement(String productId, String godownId, String batchId, BigDecimal quantity, MovementDirection direction, BigDecimal unitCost, LocalDate movementDate) {
        this.productId = productId;
        this.godownId = godownId;
        this.batchId = batchId;
        this.quantity = quantity;
        this.direction = direction;
        this.unitCost = unitCost;
        this.movementDate = movementDate;
    }

    public String getMovementId() {
        return movementId;
    }

    public void setMovementId(String movementId) {
        this.movementId = movementId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getGodownId() {
        return godownId;
    }

    public void setGodownId(String godownId) {
        this.godownId = godownId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public MovementDirection getDirection() {
        return direction;
    }

    public void setDirection(MovementDirection direction) {
        this.direction = direction;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public LocalDate getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(LocalDate movementDate) {
        this.movementDate = movementDate;
    }
}
