package com.erp.transaction.controller;

import com.erp.transaction.domain.PurchaseOrder;
import com.erp.transaction.service.PurchaseOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@CrossOrigin(origins = "*")
public class PurchaseOrderController {

    private final PurchaseOrderService poService;

    public PurchaseOrderController(PurchaseOrderService poService) {
        this.poService = poService;
    }

    @GetMapping
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return poService.getAllPurchaseOrders();
    }

    @GetMapping("/{id}")
    public PurchaseOrder getPurchaseOrderById(@PathVariable String id) {
        return poService.getPurchaseOrderById(id);
    }

    @PostMapping
    public PurchaseOrder createPurchaseOrder(@RequestBody PurchaseOrder po) {
        return poService.createPurchaseOrder(po);
    }
}
