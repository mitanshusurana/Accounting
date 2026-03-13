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

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
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
}
