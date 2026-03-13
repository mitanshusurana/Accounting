package com.erp.inventory.repository;

import com.erp.inventory.domain.Godown;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GodownRepository extends JpaRepository<Godown, String> {
}
