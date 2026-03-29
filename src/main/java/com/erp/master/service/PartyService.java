package com.erp.master.service;

import com.erp.master.domain.Party;
import com.erp.master.repository.PartyRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class PartyService {

    private final PartyRepository partyRepository;

    public PartyService(PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    public List<Party> getAllParties() {
        return partyRepository.findAll();
    }

    public Party getPartyById(String id) {
        return partyRepository.findById(id).orElse(null);
    }

    public Party saveParty(Party party) {
        if (party.getId() == null || party.getId().isEmpty()) {
            party.setId(UUID.randomUUID().toString());
        }
        return partyRepository.save(party);
    }

    public void deleteParty(String id) {
        partyRepository.deleteById(id);
    }
}
