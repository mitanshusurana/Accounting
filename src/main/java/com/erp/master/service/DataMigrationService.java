package com.erp.master.service;

import com.erp.master.domain.Party;
import com.erp.master.repository.PartyRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DataMigrationService {

    private final PartyRepository partyRepository;

    public DataMigrationService(PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    public List<Party> importPartiesFromCsv(MultipartFile file) throws Exception {
        List<Party> importedParties = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 2) {
                    Party party = new Party();
                    party.setId(UUID.randomUUID().toString());
                    party.setName(values[0].trim());
                    party.setType(Party.PartyType.valueOf(values[1].trim().toUpperCase()));
                    if (values.length > 2) party.setGstin(values[2].trim());
                    if (values.length > 3) party.setAddress(values[3].trim());
                    if (values.length > 4) party.setPhone(values[4].trim());

                    importedParties.add(party);
                }
            }
        }

        return partyRepository.saveAll(importedParties);
    }
}
