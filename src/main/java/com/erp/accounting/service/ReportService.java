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

    public Map<String, Object> getProfitAndLoss(LocalDate startDate, LocalDate endDate) {
        // Simplified P&L based on Trial Balance up to endDate (ignoring startDate for simplicity in this example)
        List<Map<String, Object>> tb = getTrialBalance(endDate);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        List<Map<String, Object>> revenues = new ArrayList<>();
        List<Map<String, Object>> expenses = new ArrayList<>();

        for (Map<String, Object> row : tb) {
            String type = (String) row.get("accountType");
            BigDecimal debit = (BigDecimal) row.get("debitBalance");
            BigDecimal credit = (BigDecimal) row.get("creditBalance");

            if ("REVENUE".equals(type)) {
                // Revenues usually have credit balances
                BigDecimal balance = credit.subtract(debit);
                totalRevenue = totalRevenue.add(balance);
                row.put("balance", balance);
                revenues.add(row);
            } else if ("EXPENSE".equals(type)) {
                // Expenses usually have debit balances
                BigDecimal balance = debit.subtract(credit);
                totalExpenses = totalExpenses.add(balance);
                row.put("balance", balance);
                expenses.add(row);
            }
        }

        Map<String, Object> pnl = new HashMap<>();
        pnl.put("revenues", revenues);
        pnl.put("totalRevenue", totalRevenue);
        pnl.put("expenses", expenses);
        pnl.put("totalExpenses", totalExpenses);
        pnl.put("netProfit", totalRevenue.subtract(totalExpenses));

        return pnl;
    }

    public Map<String, Object> getBalanceSheet(LocalDate asOfDate) {
        List<Map<String, Object>> tb = getTrialBalance(asOfDate);

        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal totalLiabilities = BigDecimal.ZERO;
        BigDecimal totalEquity = BigDecimal.ZERO;

        List<Map<String, Object>> assets = new ArrayList<>();
        List<Map<String, Object>> liabilities = new ArrayList<>();
        List<Map<String, Object>> equity = new ArrayList<>();

        for (Map<String, Object> row : tb) {
            String type = (String) row.get("accountType");
            BigDecimal debit = (BigDecimal) row.get("debitBalance");
            BigDecimal credit = (BigDecimal) row.get("creditBalance");

            if ("ASSET".equals(type)) {
                BigDecimal balance = debit.subtract(credit);
                totalAssets = totalAssets.add(balance);
                row.put("balance", balance);
                assets.add(row);
            } else if ("LIABILITY".equals(type)) {
                BigDecimal balance = credit.subtract(debit);
                totalLiabilities = totalLiabilities.add(balance);
                row.put("balance", balance);
                liabilities.add(row);
            } else if ("EQUITY".equals(type)) {
                BigDecimal balance = credit.subtract(debit);
                totalEquity = totalEquity.add(balance);
                row.put("balance", balance);
                equity.add(row);
            }
        }

        // Add Current Year Profit to Equity
        Map<String, Object> pnl = getProfitAndLoss(LocalDate.of(asOfDate.getYear(), 1, 1), asOfDate); // simplified start date
        BigDecimal netProfit = (BigDecimal) pnl.get("netProfit");
        totalEquity = totalEquity.add(netProfit);

        Map<String, Object> bs = new HashMap<>();
        bs.put("assets", assets);
        bs.put("totalAssets", totalAssets);
        bs.put("liabilities", liabilities);
        bs.put("totalLiabilities", totalLiabilities);
        bs.put("equity", equity);
        bs.put("currentYearProfit", netProfit);
        bs.put("totalEquityAndLiabilities", totalLiabilities.add(totalEquity));

        return bs;
    }
}
