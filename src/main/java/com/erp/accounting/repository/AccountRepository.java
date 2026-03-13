package com.erp.accounting.repository;

import com.erp.accounting.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {

    @Query(value = "SELECT * FROM accounts WHERE ltree_path <@ cast(:path as ltree)", nativeQuery = true)
    List<Account> findDescendantsByLtreePath(@Param("path") String path);
}
