package com.erp.master.repository;

import com.erp.master.domain.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialYearRepository extends JpaRepository<FinancialYear, String> {
}
