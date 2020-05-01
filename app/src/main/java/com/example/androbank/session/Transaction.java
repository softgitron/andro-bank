package com.example.androbank.session;

import android.icu.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Transaction {
    protected String fromAccount = null;
    protected Integer fromAccountId = null;
    protected String toAccount = null;
    protected Integer amount = 0;
    protected Date date = null;
    protected Integer transferId;
    protected String fromAccountBic;
    protected String toAccountBic;

    public Transaction(Integer transferId, String fromAccount, Integer fromAccountId,  String toAccount, Integer amount, Date date, String fromAccountBic, String toAccountBic) {
        this.fromAccount = fromAccount;
        this.fromAccountId = fromAccountId;
        this.toAccount = toAccount;
        this.amount = amount;
        this.date = date;
        this.transferId = transferId;
        this.fromAccountBic = fromAccountBic;
        this.toAccountBic = toAccountBic;
    }

    public Transaction() {
    }

    public Integer getTransferId() {return transferId;}
    public Integer getFromAccountId() {return fromAccountId;}
    public String getFromAccount() {return fromAccount;}

    public String getToAccount() {return toAccount;}

    public float getAmount() { return amount;}

    public Date getDate() {return date;}

    /**Used for making the correct text of the transaction depending on the transaction attributes.
     * @param selectedAccount Used for setting the amount sign to negative or positive.
     * @return String describing the transaction.
     */
    public String toString(String selectedAccount) {
        String amount = String.format("%.2f",  ( ( (float) this.amount) / 100) );
        // Formatting the string
        String date = formatDate();

        if (fromAccount == null) {
            return String.format("From: Own Deposit                        \nTo:      %s    %s\nDate:  %s          Sum: +%s €", toAccount, toAccountBic, date, amount);
        } else if (fromAccount.equals(selectedAccount)){
            return String.format("From: %s    %s\nTo:      %s    %s\nDate:  %s          Sum: -%s €", fromAccount, fromAccountBic, toAccount, toAccountBic, date, amount);
        } else {
            return String.format("From: %s    %s\nTo:      %s    %s\nDate:  %s          Sum: +%s €", fromAccount, fromAccountBic, toAccount, toAccountBic, date, amount);
        }
    }

    /**Used to format received date from the server to the users time zone and then formatted to our own format.
     * @return The formatted string.
     */
    private String formatDate() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateAndTimeString = simpleDateFormat.format(this.date);
        return dateAndTimeString;

        /*
        LocalDateTime dateTime;
        // Source: https://stackoverflow.com/questions/39690944/convert-utc-date-to-current-timezone/39692411#39692411
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss");
        DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm:ss");
        dateTime = LocalDateTime.parse(dateAndTimeString, formatter);
        OffsetDateTime odt = dateTime.atOffset( ZoneOffset.UTC );
        ZoneId currentZoneId = ZoneId.systemDefault(); // Or, for example: ZoneId.of( "America/Montreal" )
        ZonedDateTime zdt = odt.atZoneSameInstant( currentZoneId);

        //System.out.println("FutureTransaction output date string: " + output);
        return zdt.format(formatterOutput); */
    }

}
