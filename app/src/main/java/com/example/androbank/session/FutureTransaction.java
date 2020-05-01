package com.example.androbank.session;

import android.icu.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class FutureTransaction extends Transaction{
    private Integer futureTransferId;
    private Integer atInterval; // How often transfer is processed in minutes
    private Integer times;
    private Date atTime;



    public FutureTransaction() {

    }

    public FutureTransaction(Integer futureTransferId, String fromAccount,Integer fromAccountId, String toAccount, Integer amount, Date atTime, Integer times, String fromAccountBic, String toAccountBic ) {
        this.futureTransferId = futureTransferId;
        this.fromAccount = fromAccount;
        this.fromAccountId = fromAccountId;
        this.toAccount = toAccount;
        this.amount = amount;
        this.atTime = atTime;
        if (times == null) { this.times = 1; } else {this.times = times;}
        this.fromAccountBic = fromAccountBic;
        this.toAccountBic = toAccountBic;
    }

    public Integer getFutureTransferId() {return futureTransferId;}


    public String toString() {
        // This is currently only used in TransactionsViewFuture by the spinner.
        String amount = String.format("%.2f",  ( ( (float) this.amount) / 100) );
        return "From: " +  fromAccount + "  " + fromAccountBic +
                "\nTo:      " + toAccount + "  " + toAccountBic +
                "\nSum: +" + amount + "€  Date: " +formatTime() +"  Times: " + times;

    }

    /**Used to format the received date from the backend to the users time zone and then formatted to our own format.
     * @return The formatted string.
     */
    // Todo verify correct format after SoftGitron fix.
    private String formatTime() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateAndTimeString = simpleDateFormat.format(this.atTime);
        return dateAndTimeString;
        /*
        LocalDateTime dateTime = null;

        // Source: https://stackoverflow.com/questions/39690944/convert-utc-date-to-current-timezone/39692411#39692411
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss");
        DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm:ss");
        dateTime = LocalDateTime.parse(dateAndTimeString, formatter);
        OffsetDateTime odt = dateTime.atOffset( ZoneOffset.UTC );
        ZoneId currentZoneId = ZoneId.systemDefault(); // Or, for example: ZoneId.of( "America/Montreal" )
        ZonedDateTime zdt = odt.atZoneSameInstant( currentZoneId);


        //System.out.println("FutureTransaction output date string: " + output);
        return zdt.format(formatterOutput);*/
    }


}
