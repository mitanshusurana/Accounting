package com.erp.accounting.controller;

import com.erp.accounting.domain.JournalEntry;
import com.erp.accounting.domain.Posting;
import com.erp.accounting.repository.JournalEntryRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/journal-entries")
@CrossOrigin(origins = "*") // Allow frontend access
public class JournalEntryController {

    private final JournalEntryRepository journalEntryRepository;

    public JournalEntryController(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    @GetMapping
    public List<JournalEntry> getAllJournalEntries() {
        return journalEntryRepository.findAll();
    }

    @PostMapping
    @Transactional
    public JournalEntry createJournalEntry(@RequestBody JournalEntry journalEntry) {
        // Enforce the double-entry bookkeeping rule
        journalEntry.validateBalance();

        // Setup relations
        if(journalEntry.getEntryId() == null) {
             journalEntry.setEntryId(UUID.randomUUID().toString());
        }

        if (journalEntry.getPostings() != null) {
            for (Posting posting : journalEntry.getPostings()) {
                posting.setJournalEntry(journalEntry);
            }
        }

        return journalEntryRepository.save(journalEntry);
    }
}
