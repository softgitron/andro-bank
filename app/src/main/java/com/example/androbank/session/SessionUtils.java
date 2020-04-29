package com.example.androbank.session;

import com.example.androbank.connection.Response;

public class SessionUtils {

    /** Handles the error received from the server in a generic fashion. Should be only used with rare errors.
     * @param response Server response that should be checked
     * @return Boolean Was there any errors. Yes or no.
     */
    public static Boolean genericErrorHandling(Response response) {
        Integer httpCode = response.getHttpCode();
        if (httpCode < 299) {
            return false;
        } else {
            Session session = Session.getSession();
            session.setLastErrorCode(httpCode);
            session.setLastErrorMessage(response.getError());
            return true;
        }
    }
}
