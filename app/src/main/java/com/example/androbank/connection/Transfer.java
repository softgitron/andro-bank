package com.example.androbank.connection;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Transfer {
    public static final String API_ADDRESS = "https://hello1254841.com";
    static final Integer CONNECTION_TIMEOUT = 5000;
    static final Integer READ_TIMEOUT = 5000;
    protected static String token;

    enum MethodType {
        POST,
        GET,
        DELETE
    }

    public static void setToken(String newToken) {
        token = newToken;
    }
    public static String getToken() {
        return token;
    }

    protected static Response sendRequest(Object data, MethodType method, Class resultType, boolean authentication) {
        Response response = new Response();
        Thread requestThread = new Thread(() -> {
            // https://github.com/google/gson/blob/master/UserGuide.md
            Gson gson = new Gson();
            String json = gson.toJson(data);
            // https://www.baeldung.com/java-http-request
            // https://www.baeldung.com/httpurlconnection-post
            URL url = null;
            try {
                url = new URL(API_ADDRESS);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);
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
                handleResponse(responseString, connection.getResponseCode(), response, resultType);
            } catch (MalformedURLException e) {
                System.out.println("Malformed url exception should not occur ever.");
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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

    private static void handleResponse(String responseString, Integer statusCode, Response response, Class resultType) {
        if (statusCode > 299) {
            Gson gson = new Gson();
            Object results = gson.fromJson(responseString, resultType);
            response.setValue(statusCode, results, null);
        } else {
            response.setValue(statusCode, null, responseString);
        }
    }
}
