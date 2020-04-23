package com.example.androbank.connection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ListIterator;

class Cache {
    private static Calendar calendar = Calendar.getInstance();
    private static ArrayList<CacheEntry> cacheEntries = new ArrayList<>();

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
