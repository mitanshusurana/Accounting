package com.erp.accounting.service;

import com.erp.accounting.domain.Account;
import com.erp.accounting.domain.JournalEntry;
import com.erp.accounting.repository.AccountRepository;
import com.erp.accounting.repository.JournalEntryRepository;
import com.erp.accounting.repository.PostingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final AccountRepository accountRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final PostingRepository postingRepository;

    public ReportService(AccountRepository accountRepository, JournalEntryRepository journalEntryRepository, PostingRepository postingRepository) {
        this.accountRepository = accountRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.postingRepository = postingRepository;
    }

    public List<Map<String, Object>> getTrialBalance(LocalDate asOfDate) {
        List<Object[]> aggregatedBalances = postingRepository.getAggregatedBalances(asOfDate);
        List<Map<String, Object>> trialBalance = new ArrayList<>();

        for (Object[] row : aggregatedBalances) {
            String accountId = (String) row[0];
            String accountName = (String) row[1];
            Account.AccountType accountType = (Account.AccountType) row[2];
            BigDecimal totalDebit = (BigDecimal) row[3];
            BigDecimal totalCredit = (BigDecimal) row[4];

            if (totalDebit == null) totalDebit = BigDecimal.ZERO;
            if (totalCredit == null) totalCredit = BigDecimal.ZERO;

            BigDecimal balance = totalDebit.subtract(totalCredit);

            Map<String, Object> tbRow = new HashMap<>();
            tbRow.put("accountId", accountId);
            tbRow.put("accountName", accountName);
            tbRow.put("accountType", accountType.name());

            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                tbRow.put("debitBalance", balance);
                tbRow.put("creditBalance", BigDecimal.ZERO);
            } else {
                tbRow.put("debitBalance", BigDecimal.ZERO);
                tbRow.put("creditBalance", balance.abs());
            }

            trialBalance.add(tbRow);
        }

        // Include accounts with zero balance
        List<Account> allAccounts = accountRepository.findAll();
        Map<String, Map<String, Object>> tbMap = trialBalance.stream()
                .collect(Collectors.toMap(r -> (String) r.get("accountId"), r -> r));

        for (Account acc : allAccounts) {
            if (!tbMap.containsKey(acc.getAccountId())) {
                Map<String, Object> tbRow = new HashMap<>();
                tbRow.put("accountId", acc.getAccountId());
                tbRow.put("accountName", acc.getName());
                tbRow.put("accountType", acc.getAccountType().name());
                tbRow.put("debitBalance", BigDecimal.ZERO);
                tbRow.put("creditBalance", BigDecimal.ZERO);
                trialBalance.add(tbRow);
            }
        }

        return trialBalance;
    }

    public List<JournalEntry> getDaybook(LocalDate startDate, LocalDate endDate) {
        return journalEntryRepository.findByTransactionDateBetween(startDate, endDate);
    }
}
