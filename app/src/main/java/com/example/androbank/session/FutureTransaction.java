package com.example.androbank.session;

import android.icu.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

public class FutureTransaction extends Transaction{
    private Integer futureTransferId;
    private Integer atInterval; // How often transfer is processed in minutes
    private Integer times;
    private Date atTime;


    public FutureTransaction() {

    }

    public FutureTransaction(Integer futureTransferId, String fromAccount, String toAccount, Integer amount, Date atTime, Integer times ) {
        this.futureTransferId = futureTransferId;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.atTime = atTime;
        this.times = times;
    }

    //TODO rivi crashaa futrure transactionit (poistettu this.date.toString(), jotta toimii. Lisäksi getFutureTransactions() responseConatiner.time on null
    // Todo does the previous still hold true?
    public String toString() {
        // Formatting the string
        String pattern = "dd.MM.yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("fi", "FI"));
        String date = simpleDateFormat.format(this.atTime);
        // This is currently only used in TransactionsViewFuture by the spinner.
        String amount = String.format("%.2f", (float) (this.amount / 100) );
        return "From: " +  fromAccount + "\nTo: " + toAccount + "  sum: +" + amount + "€" + "\n" + date.toString();

    }
}
