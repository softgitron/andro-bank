package com.example.androbank.session;

import com.example.androbank.connection.Response;

import java.util.Calendar;
import java.util.Date;

public class SessionUtils {
    private static final Integer REFRESH_TIME_IN_MINUTES = 1;
    public static Boolean shouldBeUpdated(Date timestamp) {
        // Check whether data is too old for usage
        // https://www.baeldung.com/java-add-hours-date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.add(Calendar.MINUTE, REFRESH_TIME_IN_MINUTES);
        Date shouldBeUpdated = calendar.getTime();
        if (shouldBeUpdated.before(new Date())) {
            return true;
        } else {
            return false;
        }
    }

    // True means errors were handled
    public static Boolean genericErrorHandling(Response response) {
        Integer httpCode = response.getHttpCode();
        if (httpCode < 299) {
            return false;
        } else {
            Session session = Session.getSession();
            session.setLastErrorCode(1);
            session.setLastErrorMessage(response.getError());
            return true;
        }
    }
}
