package com.erp.accounting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private String accountId;

    private String name;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(columnDefinition = "ltree")
    private String ltreePath;

    public enum AccountType {
        ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE
    }

    public Account() {}

    public Account(String accountId, String name, AccountType accountType, String ltreePath) {
        this.accountId = accountId;
        this.name = name;
        this.accountType = accountType;
        this.ltreePath = ltreePath;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getLtreePath() {
        return ltreePath;
    }

    public void setLtreePath(String ltreePath) {
        this.ltreePath = ltreePath;
    }
}
