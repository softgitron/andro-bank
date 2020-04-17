package com.example.androbank.session;

public class Account {
    public enum AccountType {
        Savings,
        Credit,
        Normal
    }

    private Integer accountId = null;
    private String iban = null;
    private Integer balance = null;
    private AccountType accountType = null;

    public Account(Integer accountId, String iban, Integer balance, AccountType accountType) {
        this.accountId = accountId;
        this.iban = iban;
        this.balance = balance;
        this.accountType = accountType;
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

    public AccountType getAccountType () { return accountType;}

    public AccountType setAccountType () {
        return accountType;
    }

    public void deposit(float amount) {

    }
}
