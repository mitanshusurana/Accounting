package com.erp.transaction.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {

    public enum InvoiceType { SALES, PURCHASE }
    public enum Status { DRAFT, SUBMITTED, CANCELLED }

    @Id
    private String id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String partyId;

    @Enumerated(EnumType.STRING)
    private InvoiceType type;

    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String linkedPoId;
    private BigDecimal otherCharges = BigDecimal.ZERO;
    private BigDecimal gstPayable = BigDecimal.ZERO;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceLine> lines = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }
    public String getPartyId() { return partyId; }
    public void setPartyId(String partyId) { this.partyId = partyId; }
    public InvoiceType getType() { return type; }
    public void setType(InvoiceType type) { this.type = type; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getLinkedPoId() { return linkedPoId; }
    public void setLinkedPoId(String linkedPoId) { this.linkedPoId = linkedPoId; }
    public BigDecimal getOtherCharges() { return otherCharges; }
    public void setOtherCharges(BigDecimal otherCharges) { this.otherCharges = otherCharges; }
    public BigDecimal getGstPayable() { return gstPayable; }
    public void setGstPayable(BigDecimal gstPayable) { this.gstPayable = gstPayable; }
    public List<InvoiceLine> getLines() { return lines; }
    public void setLines(List<InvoiceLine> lines) { this.lines = lines; }

    public void addLine(InvoiceLine line) {
        lines.add(line);
        line.setInvoice(this);
    }
}
