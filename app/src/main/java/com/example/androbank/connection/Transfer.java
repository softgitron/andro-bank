package com.example.androbank.connection;

import com.example.androbank.containers.AccountContainer;
import com.example.androbank.containers.BankContainer;
import com.example.androbank.containers.CardContainer;
import com.example.androbank.containers.FutureTransactionContainer;
import com.example.androbank.containers.TransactionContainer;
import com.example.androbank.containers.UserContainer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.HttpsURLConnection;

public class Transfer {
    public static final String API_ADDRESS = "https://qlist.ddns.net";
    static final Integer CONNECTION_TIMEOUT = 5000;
    static final Integer READ_TIMEOUT = 5000;
    protected static String token;
    private static Lock isFetching = new ReentrantLock();

    public enum MethodType {
        POST,
        GET,
        PATCH,
        DELETE
    }

    public static Response sendRequest(MethodType method, String address, Object data, Class resultType, boolean authentication) {
        return doSendRequest(method, address, data, resultType, authentication, true);
    }

    public static Response sendRequest(MethodType method, String address, Object data, Class resultType, boolean authentication, boolean useCache) {
        return doSendRequest(method, address, data, resultType, authentication, useCache);
    }

    private static Response doSendRequest(MethodType method, String address, Object data, Class resultType, boolean authentication, boolean useCache) {
        Response response = new Response();
        Thread requestThread = new Thread(() -> {

            try {
                if (isFetching.tryLock(10, TimeUnit.SECONDS)) {
                    try {
                        executeRequest(method, address, data, resultType, authentication, useCache, response);
                    }
                    finally {
                        isFetching.unlock();
                    }
                } else {
                    setResponse(response, 999, null, "Responses are too slow to happen", false, null);
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                isFetching.unlock();
            }
        });
        requestThread.start();
        return response;
    }

    private static void executeRequest(MethodType method, String address, Object data, Class resultType, boolean authentication, boolean useCache, Response response) {
        // https://github.com/google/gson/blob/master/UserGuide.md
        String json = "{}";
        if (data != null) {
            Gson gson = new Gson();
            json = gson.toJson(data);
        }
        // Check cache before doing request
        if (useCache) {
            // Use cached response if there is one available
            Response cachedResponse = Cache.getCacheEntry(method, address, json);
            if (cachedResponse != null) {
                setResponse(response, cachedResponse.getHttpCode(),
                        cachedResponse.getResponse(), cachedResponse.getError(), true, null);
                return;
            }
        }
        // https://www.baeldung.com/java-http-request
        // https://www.baeldung.com/httpurlconnection-post
        URL url = null;
        try {
            url = new URL(API_ADDRESS + address);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            // Set token if requested
            if (authentication) {
                if (token == null) {
                    System.out.println("Application malfunction! No token available.");
                    System.exit(10);
                }
                connection.setRequestProperty("X-Auth-Token", token);
            }
            sendRequest(connection, method, json);
            String responseString = readResponse(connection);
            String newToken = checkToken(connection);
            handleResponse(responseString, connection.getResponseCode(), response, newToken, resultType);
            // Set new entry to cache if no error
            if (response.getHttpCode() < 299) {
                Cache.newCacheEntry(method, address, json, response);
            }
        } catch (MalformedURLException e) {
            System.out.println("Malformed url exception should not occur ever.");
            System.exit(11);
        } catch (ProtocolException e) {
            setResponse(response, 400, null, "Unknown protocol error", false, null);
        } catch (IOException e) {
            setResponse(response,444, null, "Connection error", false, null);
        }
    }

    private static void sendRequest(HttpsURLConnection connection, MethodType method, String json) throws IOException {
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestMethod(method.name());
        if (method != MethodType.GET) {
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            byte[] input = json.getBytes("utf-8");
            os.write(input, 0, input.length);
            os.flush();
            os.close();
        }
    }

    private static String checkToken(HttpsURLConnection connection) {
        Map<String, List<String>> headers = connection.getHeaderFields();
        List<String> tokens = headers.get("X-Auth-Token");
        String newToken = null;
        if (tokens != null && tokens.size() != 0) {
            newToken = tokens.get(0);
            token = newToken;
        }
        return newToken;
    }

    private static String readResponse(HttpsURLConnection connection) throws IOException {
        int status = connection.getResponseCode();
        InputStreamReader streamReader = null;
        if (status > 299) {
            streamReader = new InputStreamReader(connection.getErrorStream(), "utf-8");
        } else {
            streamReader = new InputStreamReader(connection.getInputStream(), "utf-8");
        }

        StringBuilder responseString = new StringBuilder();
        String responseLine;
        BufferedReader br = new BufferedReader(streamReader);
        while ((responseLine = br.readLine()) != null) {
            responseString.append(responseLine.trim());
        }
        br.close();
        return responseString.toString();
    }

    private static void handleResponse(String responseString, Integer statusCode, Response response, String token, Class resultType) {
        if (statusCode < 299) {
            Type resultTypeChecked = handleListTypes(responseString, resultType);
            Gson gson = new Gson();
            Object results;
            if (resultTypeChecked == null) {
                results = gson.fromJson(responseString, resultType);
            } else {
                results = gson.fromJson(responseString, resultTypeChecked);
            }
            setResponse(response, statusCode, results, null, false, token);
        } else {
            setResponse(response, statusCode, null, responseString, false, null);
        }
    }

    // Handles case when return json is a list.
    // This is again quite hacky but makes calling of the API:s a lot easier
    // Java does not support proper way to do this actually cleanly
    private static Type handleListTypes(String responseString, Class resultType) {
        if (responseString.startsWith("[") && responseString.endsWith("]")) {
            if (resultType == AccountContainer.class) {
                return new TypeToken<ArrayList<AccountContainer>>(){}.getType();
            } else if (resultType == BankContainer.class) {
                return new TypeToken<ArrayList<BankContainer>>(){}.getType();
            } else if (resultType == CardContainer.class) {
                return new TypeToken<ArrayList<CardContainer>>(){}.getType();
            } else if (resultType == FutureTransactionContainer.class) {
                return new TypeToken<ArrayList<FutureTransactionContainer>>(){}.getType();
            } else if (resultType == TransactionContainer.class) {
                return new TypeToken<ArrayList<TransactionContainer>>(){}.getType();
            } else if (resultType == UserContainer.class) {
                return new TypeToken<ArrayList<UserContainer>>(){}.getType();
            } else if (resultType == String.class) {
                return new TypeToken<ArrayList<String>>(){}.getType();
            } else {
                System.out.println("Fatal error occurred during handling of the response.");
                System.out.println("Most likely there is incompatible return type selected.");
                System.out.println("This error should not ever appear,");
                System.exit(1004);
            }
        }
        return null;
    }

    private static void setResponse(Response resp, Integer httpCode, Object response, String error, boolean cached, String token) {
        int count = 0;
        while (resp.countObservers() == 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (count > 100) {
                System.out.println("Observer is too slow to attach. Check your code.");
                System.exit(1050);
            }
            count++;
        }
        resp.setValue(httpCode, response, error, cached, token);
    }

    public static void setToken(String newToken) {
        token = newToken;
    }
    public static String getToken() {
        if (token != null) {
            return token;
        } else {
            return "";
        }
    }

    public static void clearCache() {
        Cache.emptyCache();
    }

    /*public static class userTransfer {
        public userTransfer() {

        }
        public void createUser(String userName, String email, String phoneNumber, String password) {

        }

        public void login(String userName, String password) {

        }

        public void updateDetails(String userName, String email, String phoneNumber) {

        }

        public void updatePassword(String oldPassword, String newPassword) {

        }

    }

    public static class basicTransfer{
        public basicTransfer() {

        }

        public void newTransaction(String fromAccount, String toAccount, float amount) {

        }

        public void newDeposit(String account, float amount) {

        }

        public void newPayment(String account, float amount) {

        }

        public void getTransactions(String account) {

        }
    }

    public static class advacedTransfer{
        public advacedTransfer() {

        }

        public void newFutureTransaction(String fromAccount, String toAccount, float amount, Date date, String requiring) {

        }

        public void removeFuruteTransaction(int transactionId) {

        }
        public void getFutureTransactions (String account) {

        }
    }

    public static class cardTransfer{
        public cardTransfer(){

        }

        public void createCard(String account, String cardNumber) {

        }

        public void removeCard(String cardNumber) {

        }


        public void setLimits(String cardNumber, float withdrawLimit, float paymentLimit, Array allowedCountries) {

        }
    }*/
}
