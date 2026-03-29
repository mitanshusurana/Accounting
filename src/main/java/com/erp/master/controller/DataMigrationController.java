package com.erp.master.controller;

import com.erp.master.domain.Party;
import com.erp.master.service.DataMigrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/migration")
@CrossOrigin(origins = "*")
public class DataMigrationController {

    private final DataMigrationService migrationService;

    public DataMigrationController(DataMigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @PostMapping("/import-parties")
    public ResponseEntity<?> importParties(@RequestParam("file") MultipartFile file) {
        try {
            List<Party> parties = migrationService.importPartiesFromCsv(file);
            return ResponseEntity.ok(Map.of(
                "message", "Successfully imported " + parties.size() + " parties.",
                "importedCount", parties.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Import failed: " + e.getMessage()));
        }
    }
}
