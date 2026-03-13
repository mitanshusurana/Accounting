package com.erp.accounting.repository;

import com.erp.accounting.domain.Posting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PostingRepository extends JpaRepository<Posting, String> {
    List<Posting> findByAccountIdAndJournalEntryTransactionDateLessThanEqual(String accountId, LocalDate asOfDate);

    @Query("SELECT p.accountId AS accountId, a.name AS accountName, a.accountType AS accountType, SUM(p.debitAmount) AS totalDebit, SUM(p.creditAmount) AS totalCredit " +
           "FROM Posting p JOIN Account a ON p.accountId = a.accountId " +
           "WHERE p.journalEntry.transactionDate <= :asOfDate " +
           "GROUP BY p.accountId, a.name, a.accountType")
    List<Object[]> getAggregatedBalances(@Param("asOfDate") LocalDate asOfDate);
}
