package com.example.androbank.session;

import java.util.Date;

public class Transaction {
    private String fromAccount = null;
    private String toAccount = null;
    private float amount = 0;
    private Date date = null;

    public Transaction(String fromAccount, String toAccount, float amount, Date date) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.date = date;
    }

    public String getFromAccount() {return fromAccount;}

    public String getToAccount() {return toAccount;}

    public float getAmount() { return amount;}

    public Date getDate() {return date;}

    public String toString() {
        return "From: " + fromAccount + "\t" + date.toString() + "\nTo:   " + toAccount + "\t" + String.format("%2.f", amount);
    }
}
