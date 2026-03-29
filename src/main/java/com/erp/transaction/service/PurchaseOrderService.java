package com.erp.transaction.service;

import com.erp.transaction.domain.PurchaseOrder;
import com.erp.transaction.domain.PurchaseOrderLine;
import com.erp.transaction.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository poRepository;

    public PurchaseOrderService(PurchaseOrderRepository poRepository) {
        this.poRepository = poRepository;
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return poRepository.findAll();
    }

    public PurchaseOrder getPurchaseOrderById(String id) {
        return poRepository.findById(id).orElse(null);
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrder po) {
        if (po.getId() == null || po.getId().isEmpty()) {
            po.setId(UUID.randomUUID().toString());
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        if (po.getLines() != null) {
            for (PurchaseOrderLine line : po.getLines()) {
                if (line.getId() == null || line.getId().isEmpty()) {
                    line.setId(UUID.randomUUID().toString());
                }
                line.setPurchaseOrder(po);

                // Calculate line totals
                BigDecimal lineTotal = line.getQuantity().multiply(line.getUnitPrice());
                BigDecimal taxAmount = lineTotal.multiply(line.getTaxRate().divide(new BigDecimal("100")));
                line.setTaxAmount(taxAmount);
                line.setTotalAmount(lineTotal.add(taxAmount));

                totalAmount = totalAmount.add(line.getTotalAmount());
            }
        }
        po.setTotalAmount(totalAmount);
        return poRepository.save(po);
    }

    @Transactional
    public PurchaseOrder updateStatus(String id, PurchaseOrder.Status status) {
        PurchaseOrder po = getPurchaseOrderById(id);
        if (po != null) {
            po.setStatus(status);
            return poRepository.save(po);
        }
        return null;
    }
}
