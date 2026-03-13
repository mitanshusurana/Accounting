package com.erp.accounting.repository;

import com.erp.accounting.domain.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, String> {
    List<JournalEntry> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
}
