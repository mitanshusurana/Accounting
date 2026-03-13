package com.erp.banking.controller;

import com.erp.banking.domain.ParsedTransaction;
import com.erp.banking.service.BankStatementParserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/banking")
@CrossOrigin(origins = "*")
public class BankingController {

    private final BankStatementParserService parserService;

    public BankingController(BankStatementParserService parserService) {
        this.parserService = parserService;
    }

    @PostMapping("/parse-statement")
    public ResponseEntity<?> parseBankStatement(@RequestParam(value = "file", required = false) MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid PDF bank statement.");
        }

        try {
            List<ParsedTransaction> transactions = parserService.parseStatement(file);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error parsing PDF: " + e.getMessage());
        }
    }
}
