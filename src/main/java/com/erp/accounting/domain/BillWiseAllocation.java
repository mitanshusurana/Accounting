package com.erp.accounting.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;

@Embeddable
public class BillWiseAllocation {

    private String invoiceRef;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private AllocationType type;

    public enum AllocationType {
        ADVANCE, AGST_REF, NEW_REF, ON_ACCOUNT
    }

    public BillWiseAllocation() {}

    public BillWiseAllocation(String invoiceRef, BigDecimal amount, AllocationType type) {
        this.invoiceRef = invoiceRef;
        this.amount = amount;
        this.type = type;
    }

    public String getInvoiceRef() {
        return invoiceRef;
    }

    public void setInvoiceRef(String invoiceRef) {
        this.invoiceRef = invoiceRef;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public AllocationType getType() {
        return type;
    }

    public void setType(AllocationType type) {
        this.type = type;
    }
}
