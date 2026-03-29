package com.erp.accounting.service;

import com.erp.accounting.domain.JournalEntry;
import com.erp.accounting.domain.Posting;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExportService {

    // A simplified export to CSV formatted string for Reports.
    // Real systems would use Apache POI for Excel and iText/OpenPDF for PDF.

    public String exportTrialBalanceToCsv(List<java.util.Map<String, Object>> trialBalance) {
        StringBuilder csv = new StringBuilder();
        csv.append("Account ID,Account Name,Account Type,Debit Balance,Credit Balance\n");
        for (java.util.Map<String, Object> row : trialBalance) {
            csv.append(row.get("accountId")).append(",")
               .append(row.get("accountName")).append(",")
               .append(row.get("accountType")).append(",")
               .append(row.get("debitBalance")).append(",")
               .append(row.get("creditBalance")).append("\n");
        }
        return csv.toString();
    }

    public String exportDaybookToCsv(List<JournalEntry> daybook) {
        StringBuilder csv = new StringBuilder();
        csv.append("Entry ID,Date,Voucher Type,Narration,Account,Debit,Credit\n");
        for (JournalEntry je : daybook) {
            for (Posting p : je.getPostings()) {
                csv.append(je.getEntryId()).append(",")
                   .append(je.getTransactionDate()).append(",")
                   .append(je.getVoucherType()).append(",")
                   .append("\"").append(je.getNarration()).append("\",")
                   .append(p.getAccountId()).append(",")
                   .append(p.getDebitAmount() != null ? p.getDebitAmount() : 0).append(",")
                   .append(p.getCreditAmount() != null ? p.getCreditAmount() : 0).append("\n");
            }
        }
        return csv.toString();
    }
}
