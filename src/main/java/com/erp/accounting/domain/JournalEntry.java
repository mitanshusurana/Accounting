package com.erp.accounting.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {

    @Id
    private String entryId;

    private LocalDate transactionDate;

    private String voucherType;

    private String narration;

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Posting> postings = new ArrayList<>();

    public JournalEntry() {}

    public void validateBalance() {
        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (Posting posting : postings) {
            if (posting.getDebitAmount() != null) {
                totalDebits = totalDebits.add(posting.getDebitAmount());
            }
            if (posting.getCreditAmount() != null) {
                totalCredits = totalCredits.add(posting.getCreditAmount());
            }
        }

        if (totalDebits.compareTo(totalCredits) != 0) {
            throw new DomainException("Total debits (" + totalDebits + ") do not equal total credits (" + totalCredits + ")");
        }
    }

    public void addPosting(Posting posting) {
        postings.add(posting);
        posting.setJournalEntry(this);
    }

    public void removePosting(Posting posting) {
        postings.remove(posting);
        posting.setJournalEntry(null);
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public List<Posting> getPostings() {
        return postings;
    }

    public void setPostings(List<Posting> postings) {
        this.postings = postings;
    }
}
