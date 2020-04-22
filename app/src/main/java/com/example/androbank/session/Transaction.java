package com.example.androbank.session;

import java.util.Date;

public class Transaction {
    private String fromAccount = null;
    private String toAccount = null;
    private float amount = 0;
    private String date = null;

    public Transaction(String fromAccount, String toAccount, float amount, String date) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.date = date;
    }

    public String getFromAccount() {return fromAccount;}

    public String getToAccount() {return toAccount;}

    public float getAmount() { return amount;}

    public String getDate() {return date;}

    public String toString(String selectedAccount) {
        String amount = String.format("%.2f", this.amount);
        if (fromAccount == null) {
            return "From: Deposit   " + date.toString() + "\nTo:   " + toAccount + "  sum: +" + amount + "€";
        } else if (fromAccount.equals(selectedAccount)){
            return "From: " + fromAccount + "   " + date.toString() + "\nTo:   " + toAccount + "  sum: -" + amount + "€";
        } else {
            return "From: " + fromAccount + "   " + date.toString() + "\nTo:   " + toAccount + "  sum: +" + amount + "€";
        }

    }
}
