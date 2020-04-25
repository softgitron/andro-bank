package com.example.androbank.session;

import android.icu.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Transaction {
    protected String fromAccount = null;
    protected Integer fromAccountId = null;
    protected String toAccount = null;
    protected Integer amount = 0;
    protected Date date = null;
    protected Integer transferId;

    public Transaction(Integer transferId, String fromAccount, Integer fromAccountId,  String toAccount, Integer amount, Date date) {
        this.fromAccount = fromAccount;
        this.fromAccountId = fromAccountId;
        this.toAccount = toAccount;
        this.amount = amount;
        this.date = date;
        this.transferId = transferId;
    }

    public Transaction() {
    }

    public Integer getTransferId() {return transferId;}
    public Integer getFromAccountId() {return fromAccountId;}
    public String getFromAccount() {return fromAccount;}

    public String getToAccount() {return toAccount;}

    public float getAmount() { return amount;}

    public Date getDate() {return date;}

    public String toString(String selectedAccount) {
        String amount = String.format("%.2f",  ( ( (float) this.amount) / 100) );
        // Formatting the string
        String date = formatDate();

        if (fromAccount == null) {
            return String.format("From: Own Deposit                        %s\nTo:      %s    %s €", date, toAccount, amount);
        } else if (fromAccount.equals(selectedAccount)){
            return String.format("From: %s    %s\nTo:      %s     %s €", fromAccount, date, toAccount, amount);
        } else {
            return String.format("From: %s    %s\nTo:      %s     %s €", fromAccount, date, toAccount, amount);
        }
        //Apr 19, 2020, 11:57:35 AM
    }

    protected String formatDate() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateAndTimeString = simpleDateFormat.format(this.date);

        LocalDateTime dateTime = null;

        System.out.println("FutureTransaction dateAndTimeString:  " + dateAndTimeString);

        // Source: https://stackoverflow.com/questions/39690944/convert-utc-date-to-current-timezone/39692411#39692411
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss");
        DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm:ss");
        dateTime = LocalDateTime.parse(dateAndTimeString, formatter);
        OffsetDateTime odt = dateTime.atOffset( ZoneOffset.UTC );
        ZoneId currentZoneId = ZoneId.systemDefault(); // Or, for example: ZoneId.of( "America/Montreal" )
        ZonedDateTime zdt = odt.atZoneSameInstant( currentZoneId);
        String output = zdt.format(formatterOutput);


        // Todo do we want the outptut string to match users locale? Or force our own format?
        //Locale locale = Locale.getDefault();  // Or, for example: Locale.CANADA_FRENCH
        //DateTimeFormatter f = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.MEDIUM ).withLocale( locale );
        //String output = zdt.format( f );

        System.out.println("FutureTransaction output date string: " + output);
        return output;
    }

}
