package com.erp.master.controller;

import com.erp.master.domain.UnitOfMeasure;
import com.erp.master.service.UnitOfMeasureService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/uom")
@CrossOrigin(origins = "*")
public class UnitOfMeasureController {

    private final UnitOfMeasureService uomService;

    public UnitOfMeasureController(UnitOfMeasureService uomService) {
        this.uomService = uomService;
    }

    @GetMapping
    public List<UnitOfMeasure> getAllUoms() {
        return uomService.getAllUnits();
    }

    @PostMapping
    public UnitOfMeasure saveUom(@RequestBody UnitOfMeasure uom) {
        return uomService.saveUnit(uom);
    }
}
