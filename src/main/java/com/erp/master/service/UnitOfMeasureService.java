package com.erp.master.service;

import com.erp.master.domain.UnitOfMeasure;
import com.erp.master.repository.UnitOfMeasureRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class UnitOfMeasureService {

    private final UnitOfMeasureRepository uomRepository;

    public UnitOfMeasureService(UnitOfMeasureRepository uomRepository) {
        this.uomRepository = uomRepository;
    }

    public List<UnitOfMeasure> getAllUnits() {
        return uomRepository.findAll();
    }

    public UnitOfMeasure saveUnit(UnitOfMeasure uom) {
        if (uom.getId() == null || uom.getId().isEmpty()) {
            uom.setId(UUID.randomUUID().toString());
        }
        return uomRepository.save(uom);
    }
}
