package com.erp.master.controller;

import com.erp.master.domain.Party;
import com.erp.master.service.PartyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parties")
@CrossOrigin(origins = "*")
public class PartyController {

    private final PartyService partyService;

    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    @GetMapping
    public List<Party> getAllParties() {
        return partyService.getAllParties();
    }

    @GetMapping("/{id}")
    public Party getPartyById(@PathVariable String id) {
        return partyService.getPartyById(id);
    }

    @PostMapping
    public Party saveParty(@RequestBody Party party) {
        return partyService.saveParty(party);
    }

    @DeleteMapping("/{id}")
    public void deleteParty(@PathVariable String id) {
        partyService.deleteParty(id);
    }
}
