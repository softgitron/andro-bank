package com.example.androbank.session;

import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

public class FutureTransaction extends Transaction{
    private Integer futureTransferId;
    private Integer atInterval; // How often transfer is processed in minutes
    private Integer times;
    private Date atTime;


    public FutureTransaction() {

    }

    public FutureTransaction(Integer futureTransferId, String fromAccount,Integer fromAccountId, String toAccount, Integer amount, Date atTime, Integer times ) {
        this.futureTransferId = futureTransferId;
        this.fromAccount = fromAccount;
        this.fromAccountId = fromAccountId;
        this.toAccount = toAccount;
        this.amount = amount;
        this.atTime = atTime;
        this.times = times;
    }

    public Integer getFutureTransferId() {return futureTransferId;};


    public String toString() {
        // This is currently only used in TransactionsViewFuture by the spinner.
        String amount = String.format("%.2f",  ( ( (float) this.amount) / 100) );
        return "From: " +  fromAccount + "\nTo: " + toAccount + "  sum: +" + amount + "€" + "\n" +formatTime() +"   times: " + times;

    }

    private String formatTime() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateAndTimeString = simpleDateFormat.format(this.atTime);

        LocalDateTime dateTime = null;
        Date date = null;

        //System.out.println("FutureTransaction dateAndTimeString:  " + dateAndTimeString);

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
