package com.erp.transaction.repository;

import com.erp.transaction.domain.CreditDebitNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditDebitNoteRepository extends JpaRepository<CreditDebitNote, String> {
}
