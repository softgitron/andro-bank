package com.example.androbank.connection;

import java.util.Date;

class CacheEntry {
    private Date timestamp;
    private Integer validMinutes;
    private Transfer.MethodType method;
    private String address;
    private String data;
    private Response response;

    /** Contructor of the cache entry
     * @param timestamp When entry was created
     * @param validMinutes How long entry should be valid
     * @param method What http verb was used
     * @param address Which address was used
     * @param data What data was send
     * @param response What kind of response was received
     * @return CacheEntry new cache entry
     */
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
