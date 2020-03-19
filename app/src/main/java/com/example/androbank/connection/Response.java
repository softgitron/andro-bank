package com.example.androbank.connection;

import java.util.Observable;

public class Response extends Observable {
    private Integer httpCode = 0;
    private Object response = null;
    private String error = null;

    public void setValue(Integer httpCode, Object response, String error)
    {
        this.httpCode = httpCode;
        this.response = response;
        this.error = error;

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
}