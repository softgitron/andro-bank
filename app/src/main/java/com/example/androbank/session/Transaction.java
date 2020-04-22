package com.example.androbank.session;

import android.icu.text.SimpleDateFormat;

import java.text.ParseException;

import java.util.Date;

public class Transaction {
    private String fromAccount = null;
    private String toAccount = null;
    private Integer amount = 0;
    private String date = null;

    public Transaction(String fromAccount, String toAccount, Integer amount, String date) {
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
        String amount = String.format("%.2f", (float) (this.amount / 100) );
        if (fromAccount == null) {
            return "From: Deposit   " + this.date.toString() + "\nTo:   " + toAccount + "  sum: +" + amount + "€";
        } else if (fromAccount.equals(selectedAccount)){
            return "From: " + fromAccount + "   " + this.date.toString() + "\nTo:   " + toAccount + "  sum: -" + amount + "€";
        } else {
            return "From: " + fromAccount + "   " + this.date.toString() + "\nTo:   " + toAccount + "  sum: +" + amount + "€";
        }

        //Apr 19, 2020, 11:57:35 AM


    }
}
