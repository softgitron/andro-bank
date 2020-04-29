package com.example.androbank.connection;

import java.util.Observable;

public class Response extends Observable {
    private Integer httpCode = 0;
    private String token = null;
    private Object response = null;
    private String error = null;
    private boolean cached = false;

    /** Set details of the response
     * @param httpCode Https status code
     * @param response Response object that was received from the server
     * @param error Error string that was received from the server
     * @param cached Was the information received from cache
     * @param token New token received from the server
     * @return void
     */
    public void setValue(Integer httpCode, Object response, String error, boolean cached, String token)
    {
        this.httpCode = httpCode;
        this.response = response;
        this.error = error;
        this.cached = cached;
        this.token = token;

        setChanged();
        notifyObservers();
    }
    public Integer getHttpCode()
    {
        return httpCode;
    }

    public Object getResponse()
    {
        return response;
    }

    public String getError()
    {
        return error;
    }

    public boolean wasCached() { return cached; }

    public String getToken() { return token; }

}
