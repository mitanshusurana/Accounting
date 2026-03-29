package com.erp.accounting.controller;

import com.erp.accounting.domain.JournalEntry;
import com.erp.accounting.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*") // Allow frontend access
public class ReportController {

    private final ReportService reportService;
    private final com.erp.accounting.service.ExportService exportService;

    public ReportController(ReportService reportService, com.erp.accounting.service.ExportService exportService) {
        this.reportService = reportService;
        this.exportService = exportService;
    }

    @GetMapping("/trial-balance")
    public List<Map<String, Object>> getTrialBalance(
            @RequestParam("asOfDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        return reportService.getTrialBalance(asOfDate);
    }

    @GetMapping("/daybook")
    public List<JournalEntry> getDaybook(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return reportService.getDaybook(startDate, endDate);
    }

    @GetMapping("/profit-loss")
    public Map<String, Object> getProfitAndLoss(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return reportService.getProfitAndLoss(startDate, endDate);
    }

    @GetMapping("/balance-sheet")
    public Map<String, Object> getBalanceSheet(
            @RequestParam("asOfDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        return reportService.getBalanceSheet(asOfDate);
    }

    @GetMapping(value = "/trial-balance/export", produces = "text/csv")
    public org.springframework.http.ResponseEntity<String> exportTrialBalance(
            @RequestParam("asOfDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        List<Map<String, Object>> tb = reportService.getTrialBalance(asOfDate);
        String csv = exportService.exportTrialBalanceToCsv(tb);
        return org.springframework.http.ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=trial_balance.csv")
                .body(csv);
    }

    @GetMapping(value = "/daybook/export", produces = "text/csv")
    public org.springframework.http.ResponseEntity<String> exportDaybook(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<JournalEntry> daybook = reportService.getDaybook(startDate, endDate);
        String csv = exportService.exportDaybookToCsv(daybook);
        return org.springframework.http.ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=daybook.csv")
                .body(csv);
    }
}
