package com.erp.accounting.domain;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class CostCenterAllocation {

    private String costCenterId;
    private BigDecimal amount;

    public CostCenterAllocation() {}

    public CostCenterAllocation(String costCenterId, BigDecimal amount) {
        this.costCenterId = costCenterId;
        this.amount = amount;
    }

    public String getCostCenterId() {
        return costCenterId;
    }

    public void setCostCenterId(String costCenterId) {
        this.costCenterId = costCenterId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
