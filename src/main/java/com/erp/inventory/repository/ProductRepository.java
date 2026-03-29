package com.erp.inventory.repository;

import com.erp.inventory.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Query("SELECT p FROM Product p WHERE lower(p.barcode) LIKE lower(concat('%', :query, '%')) " +
           "OR lower(p.shortCode) LIKE lower(concat('%', :query, '%')) " +
           "OR lower(p.name) LIKE lower(concat('%', :query, '%'))")
    List<Product> searchByBarcodeOrShortCodeOrDescription(@Param("query") String query);
}
