package com.erp.tax.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class TaxCalculationService {

    /**
     * Calculates TDS based on the 2025 rule: "earlier of credit or payment".
     */
    public BigDecimal calculateTds(BigDecimal amount, BigDecimal tdsRate, LocalDate creditDate, LocalDate paymentDate) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (tdsRate == null || tdsRate.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        LocalDate applicableDate;
        if (creditDate != null && paymentDate != null) {
            applicableDate = creditDate.isBefore(paymentDate) ? creditDate : paymentDate;
        } else if (creditDate != null) {
            applicableDate = creditDate;
        } else if (paymentDate != null) {
            applicableDate = paymentDate;
        } else {
            throw new IllegalArgumentException("Either creditDate or paymentDate must be provided");
        }

        // The actual calculation logic might depend on the applicableDate,
        // e.g., fetching the effective TDS rate for that date from a rule engine.
        // For simplicity, we just apply the provided rate.

        return amount.multiply(tdsRate).divide(BigDecimal.valueOf(100));
    }
}
