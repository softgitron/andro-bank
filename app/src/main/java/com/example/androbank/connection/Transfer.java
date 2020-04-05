package com.example.androbank.connection;

import android.content.res.Resources;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Transfer {
    public static final String API_ADDRESS = "http://10.0.2.2:8080";
    static final Integer CONNECTION_TIMEOUT = 5000;
    static final Integer READ_TIMEOUT = 5000;
    protected static String token;
    private static Boolean isFetching = false;

    enum MethodType {
        POST,
        GET,
        DELETE
    }

    protected static Response sendRequest(MethodType method, String address, Object data, Class resultType, boolean authentication) {
        Response response = new Response();
        if (isFetching) {
            Thread errorThread = new Thread(() -> {
                // This is very hacky should be changed in the future to avoid sleep usage.
                try { Thread.sleep(100); } catch (Exception e) {}
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
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
                switch (method) {
                    case GET:
                        connection.setRequestMethod("GET");
                        break;
                    case POST:
                        sendPostRequest(connection, json);
                        break;
                    case DELETE:
                        connection.setRequestMethod("DELETE");
                        break;
                }
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

    private static void sendPostRequest(HttpURLConnection connection, String json) throws IOException {
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        byte[] input = json.getBytes("utf-8");
        os.write(input, 0, input.length);
        os.flush();
        os.close();
    }

    private static String checkToken(HttpURLConnection connection) {
        Map<String, List<String>> headers = connection.getHeaderFields();
        List<String> tokens = headers.get("X-Auth-Token");
        String newToken = null;
        if (tokens != null && tokens.size() != 0) {
            newToken = tokens.get(0);
            token = newToken;
        }
        return newToken;
    }

    private static String readResponse(HttpURLConnection connection) throws IOException {
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
            Gson gson = new Gson();
            Object results = gson.fromJson(responseString, resultType);
            response.setValue(statusCode, results, null, token);
        } else {
            response.setValue(statusCode, null, responseString);
        }
    }

    public static void setToken(String newToken) {
        token = newToken;
    }
    public static String getToken() {
        return token;
    }
    public static Boolean getIsFetching() { return isFetching; }
    public static void setIsFetching(Boolean value) { isFetching = value; }


    public static class userTransfer {
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
    }
}
