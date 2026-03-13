package com.erp.accounting.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "postings")
public class Posting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String postingId;

    private String accountId;

    private BigDecimal debitAmount = BigDecimal.ZERO;

    private BigDecimal creditAmount = BigDecimal.ZERO;

    @ElementCollection
    @CollectionTable(name = "posting_cost_allocations", joinColumns = @JoinColumn(name = "posting_id"))
    private List<CostCenterAllocation> costAllocations = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "posting_bill_allocations", joinColumns = @JoinColumn(name = "posting_id"))
    private List<BillWiseAllocation> billWiseDetails = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id")
    private JournalEntry journalEntry;

    public Posting() {}

    public String getPostingId() {
        return postingId;
    }

    public void setPostingId(String postingId) {
        this.postingId = postingId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public List<CostCenterAllocation> getCostAllocations() {
        return costAllocations;
    }

    public void setCostAllocations(List<CostCenterAllocation> costAllocations) {
        this.costAllocations = costAllocations;
    }

    public List<BillWiseAllocation> getBillWiseDetails() {
        return billWiseDetails;
    }

    public void setBillWiseDetails(List<BillWiseAllocation> billWiseDetails) {
        this.billWiseDetails = billWiseDetails;
    }

    public JournalEntry getJournalEntry() {
        return journalEntry;
    }

    public void setJournalEntry(JournalEntry journalEntry) {
        this.journalEntry = journalEntry;
    }
}
