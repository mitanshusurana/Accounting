package com.erp.accounting.controller;

import com.erp.accounting.domain.Account;
import com.erp.accounting.repository.AccountRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*") // Allow frontend access
public class AccountController {

    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountRepository.save(account);
    }
}
