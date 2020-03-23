package com.example.androbank.session;

public class Account {
    private Integer accountId = null;
    private String iban = null;
    private Integer balance = null;

    public Account(Integer accountId, String iban, Integer balance) {
        this.accountId = accountId;
        this.iban = iban;
        this.balance = balance;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public String getIban() {
        return iban;
    }

    public Integer getBalance() {
        return balance;
    }
}
