package com.example.androbank.session;

import android.icu.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction {
    private String fromAccount = null;
    private String toAccount = null;
    private Integer amount = 0;
    private Date date = null;
    private Integer transferId;

    public Transaction(Integer transferId, String fromAccount, String toAccount, Integer amount, Date date) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.date = date;
        this.transferId = transferId;
    }

    public Integer getTransferId() {return transferId;}
    public String getFromAccount() {return fromAccount;}

    public String getToAccount() {return toAccount;}

    public float getAmount() { return amount;}

    public Date getDate() {return date;}

    public String toString(String selectedAccount) {
        String amount = String.format("%.2f", (float) (this.amount / 100) );
        // Formatting the string
        String pattern = "dd.MM.yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("fi", "FI"));
        String date = simpleDateFormat.format(this.date);

        if (fromAccount == null) {
            return "From: Deposit   " + date + "\nTo:   " + toAccount + "  sum: +" + amount + "€";
        } else if (fromAccount.equals(selectedAccount)){
            return "From: " + fromAccount + "   " + date + "\nTo:   " + toAccount + "  sum: -" + amount + "€";
        } else {
            return "From: " + fromAccount + "   " + date+ "\nTo:   " + toAccount + "  sum: +" + amount + "€";
        }
        //Apr 19, 2020, 11:57:35 AM
    }

    public String toString() {
        // This is currently only used in TransactionsViewFuture by the spinner.
        String amount = String.format("%.2f", (float) (this.amount / 100) );
        return "From: Deposit   " + this.date.toString() + "\nTo:   " + toAccount + "  sum: +" + amount + "€";



    }
}
