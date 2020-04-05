package com.example.androbank.session;

public class Account {
    private Integer accountId = null;
    private String iban = null;
    private Integer balance = null;
    private String accountType = null;
    private boolean canMakePayments;

    public Account(Integer accountId, String iban, Integer balance, String accountType, boolean canMakePayments) {
        this.accountId = accountId;
        this.iban = iban;
        this.balance = balance;
        this.accountType = accountType;
        this.canMakePayments = canMakePayments;
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

    public String getAccountType () { return accountType;}

    public String setAccountType () {
        return accountType;
    }
    public boolean getCanMakePayments() {return canMakePayments;}

    public boolean setCanMakePayments() { return canMakePayments;}

    public void deposit(float amount) {

    }
}
