package com.erp.tax.controller;

import com.erp.tax.service.GstReturnService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gst-returns")
@CrossOrigin(origins = "*")
public class GstReturnController {

    private final GstReturnService gstReturnService;

    public GstReturnController(GstReturnService gstReturnService) {
        this.gstReturnService = gstReturnService;
    }

    @GetMapping("/gstr1")
    public ResponseEntity<Map<String, Object>> getGstr1(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(gstReturnService.generateGstr1(startDate, endDate));
    }

    @GetMapping("/gstr3b")
    public ResponseEntity<Map<String, Object>> getGstr3b(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(gstReturnService.generateGstr3b(startDate, endDate));
    }

    @PostMapping("/mismatch-report")
    public ResponseEntity<List<Map<String, Object>>> getGstr2aMismatch(
            @RequestBody List<Map<String, Object>> gstr2aData,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(gstReturnService.generateGstr2aMismatchReport(gstr2aData, startDate, endDate));
    }
}
