package com.example.androbank.connection;

import java.util.Date;

class CacheEntry {
    private Date timestamp;
    private Integer validMinutes;
    private Transfer.MethodType method;
    private String address;
    private String data;
    private Response response;

    CacheEntry(Date timestamp, Integer validMinutes, Transfer.MethodType method, String address, String data, Response response) {
        this.timestamp = timestamp;
        this.validMinutes = validMinutes;
        this.method = method;
        this.address = address;
        this.data = data;
        this.response = response;
    }

    Date getTimestamp() {return timestamp;}
    Integer getValidMinutes() {return validMinutes;}
    Transfer.MethodType getMethod() {return method;}
    String getAddress() {return address;}
    String getData() {return data;}
    Response getResponse() {return response;}
}
