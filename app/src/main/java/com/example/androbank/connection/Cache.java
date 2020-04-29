package com.example.androbank.connection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ListIterator;

class Cache {
    private static Calendar calendar = Calendar.getInstance();
    private static ArrayList<CacheEntry> cacheEntries = new ArrayList<>();

    /** Receives cache entries from the memory
     * @param method What kind of http verb was used
     * @param address Address that was used
     * @param data Object that was send earlier.
     * @return Response response that came from the cache
     */
    static Response getCacheEntry(Transfer.MethodType method, String address, String data) {
        ListIterator cacheIterator = cacheEntries.listIterator();
        while (cacheIterator.hasNext()) {
            CacheEntry entry = (CacheEntry) cacheIterator.next();
            if (!isStillValid(entry)) {
                cacheIterator.remove();
                continue;
            }
            if (entry.getMethod().equals(method) && entry.getAddress().equals(address) && entry.getData().equals(data)) {
                return entry.getResponse();
            }
        }
        return null;
    }

    /** Check is cache entry still valid or is it too old
     * @param entry Entry that should be checked
     * @return void
     */
    private static boolean isStillValid(CacheEntry entry) {
        calendar.setTime(entry.getTimestamp());
        calendar.add(Calendar.MINUTE, entry.getValidMinutes());
        calendar.getTime();
        if (calendar.getTime().before(new Date())) {
            return false;
        } else {
            return true;
        }
    }

    /** Add new cache entry to the memory
     * @param method Method that was used for the request
     * @param address Address that was used for the request
     * @param data Data that was used for the request
     * @param response Response that was received based on the parameters
     * @return void
     */
    static void newCacheEntry(Transfer.MethodType method, String address, String data, Response response) {
        Integer validMinutes = validForCaching(address);
        if (validMinutes == null) {
            return;
        }
        cleanupOldEntry(method, address, data, response);
        CacheEntry entry = new CacheEntry(new Date(), validMinutes, method, address, data, response);
        cacheEntries.add(entry);
    }

    static void emptyCache() {
        cacheEntries.clear();
    }

    /** Check entry length based on the address
     * @param address Address where request should be send like /accounts/getBanks
     * @return Integer how long entry should be valid
     */
    private static Integer validForCaching(String address) {
        switch (address) {
            case "/accounts/getAccounts":
                return 1;
            case "/accounts/getBanks":
                return 10;
            case "/cards/getCards":
                return 1;
            case  "/transactions/getFutureTransactions":
                return 1;
            case "/transactions/getTransactions":
                return 1;
            default:
                return null;
        }
    }

    /** Remove all the old similar cache entries
     * @param method Method that was used for the request
     * @param address Address that was used for the request
     * @param data Data that was used for the request
     * @param response Response that was received based on the parameters
     * @return void
     */
    private static void cleanupOldEntry(Transfer.MethodType method, String address, String data, Response response) {
        ListIterator cacheIterator = cacheEntries.listIterator();
        while (cacheIterator.hasNext()) {
            CacheEntry entry = (CacheEntry) cacheIterator.next();
            if (entry.getMethod().equals(method) && entry.getAddress().equals(address) && entry.getData().equals(data) && entry.getResponse().equals(response)) {
                cacheIterator.remove();
            }
        }
    }
}
