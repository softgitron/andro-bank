package com.example.androbank.connection;

import android.content.res.Resources;

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
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Transfer {
    public static final String API_ADDRESS = "https://qlist.ddns.net";
    static final Integer CONNECTION_TIMEOUT = 5000;
    static final Integer READ_TIMEOUT = 5000;
    protected static String token;
    private static Boolean isFetching = false;

    public enum MethodType {
        POST,
        GET,
        PATCH,
        DELETE
    }

    public static Response sendRequest(MethodType method, String address, Object data, Class resultType, boolean authentication) {
        Response response = new Response();
        if (isFetching) {
            Thread errorThread = new Thread(() -> {
                // This is very hacky should be changed in the future to avoid sleep usage.
                try { Thread.sleep(50); } catch (Exception e) {}
                response.setValue(999, null, "Fetching was already ongoing.");
            });
            return response;
        }
        isFetching = true;
        Thread requestThread = new Thread(() -> {
            // https://github.com/google/gson/blob/master/UserGuide.md
            String json = "{}";
            if (data != null) {
                Gson gson = new Gson();
                json = gson.toJson(data);
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
            } catch (MalformedURLException e) {
                System.out.println("Malformed url exception should not occur ever.");
            } catch (ProtocolException e) {
                response.setValue(400, null, "Unknown protocol error");
            } catch (IOException e) {
                response.setValue(444, null, "Connection error");
            } finally {
                isFetching = false;
            }
        });
        requestThread.start();
        return response;
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
            response.setValue(statusCode, results, null, token);
        } else {
            response.setValue(statusCode, null, responseString);
        }
    }

    // Handles case when return json is a list.
    // This is again quite hacky but makes calling of the API:s a lot easier
    // Java does not support proper way to do this actually cleanly
    private static Type handleListTypes(String responseString, Class resultType) {
        if (responseString.startsWith("[") && responseString.endsWith("]")) {
            if (resultType == AccountContainer.class) {

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

    public static void setToken(String newToken) {
        token = newToken;
    }
    public static String getToken() {
        return token;
    }
    public static Boolean getIsFetching() { return isFetching; }
    public static void setIsFetching(Boolean value) { isFetching = value; }


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
