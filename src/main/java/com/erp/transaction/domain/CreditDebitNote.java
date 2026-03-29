package com.erp.transaction.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "credit_debit_notes")
public class CreditDebitNote {

    public enum NoteType { CREDIT, DEBIT, REPLACEMENT }

    @Id
    private String id;
    private String noteNumber;
    private LocalDate noteDate;
    private String invoiceId;

    @Enumerated(EnumType.STRING)
    private NoteType type;

    private BigDecimal amount;
    private String reason;
    private String status = "PENDING";

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNoteNumber() { return noteNumber; }
    public void setNoteNumber(String noteNumber) { this.noteNumber = noteNumber; }
    public LocalDate getNoteDate() { return noteDate; }
    public void setNoteDate(LocalDate noteDate) { this.noteDate = noteDate; }
    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    public NoteType getType() { return type; }
    public void setType(NoteType type) { this.type = type; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
