package com.example.androbank.session;

import com.example.androbank.connection.Response;

public class SessionUtils {
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
