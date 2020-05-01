package com.example.androbank.connection;

import com.example.androbank.containers.AccountContainer;
import com.example.androbank.containers.BankContainer;
import com.example.androbank.containers.CardContainer;
import com.example.androbank.containers.FutureTransactionContainer;
import com.example.androbank.containers.TransactionContainer;
import com.example.androbank.containers.UserContainer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.HttpsURLConnection;

public class Transfer {
    // public static final String API_ADDRESS = "http://10.0.2.2:8080";
    public static final String API_ADDRESS = "https://qlist.ddns.net";
    private static final Integer CONNECTION_TIMEOUT = 5000;
    private static final Integer READ_TIMEOUT = 5000;
    protected static String token;
    private static Lock isFetching = new ReentrantLock();

    public enum MethodType {
        POST,
        GET,
        PATCH,
        DELETE
    }

    // https://stackoverflow.com/questions/6873020/gson-date-format
    private static JsonSerializer<Date> ser = new JsonSerializer<Date>() {
        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                context) {
            return src == null ? null : new JsonPrimitive(src.getTime());
        }
    };

    private static JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
            if (json != null) {
                Date date = new Date(json.getAsLong());
                return date;
            } else {
                return null;
            }
        }
    };

    /** Send new https request to the backend server.
     * @param method What kind of http verb should be send
     * @param address Address where request should be send like /accounts/getBanks
     * @param data Object that should be serialized and send. Container type object should be used
     * @param resultType What kind of object we are expecting back. For example BankContainer.class
     * @param authentication Should we send authentication token to the server
     * @return Response response that came from the server
     */
    public static Response sendRequest(MethodType method, String address, Object data, Class resultType, boolean authentication) {
        return threadSendRequest(method, address, data, resultType, authentication, true);
    }

    /** Send new https request to the backend server.
     * @param method What kind of http verb should be send
     * @param address Address where request should be send like /accounts/getBanks
     * @param data Object that should be serialized and send. Container type object should be used
     * @param resultType What kind of object we are expecting back. For example BankContainer.class
     * @param authentication Should we send authentication token to the server
     * @param useCache Should the http cache be used
     * @return Response response that came from the server
     */
    public static Response sendRequest(MethodType method, String address, Object data, Class resultType, boolean authentication, boolean useCache) {
        return threadSendRequest(method, address, data, resultType, authentication, useCache);
    }

    /** Makes a new thread for asyncronous data communication and checks that there is no other thread running
     * @param method What kind of http verb should be send
     * @param address Address where request should be send like /accounts/getBanks
     * @param data Object that should be serialized and send. Container type object should be used
     * @param resultType What kind of object we are expecting back. For example BankContainer.class
     * @param authentication Should we send authentication token to the server
     * @param useCache Should the http cache be used
     * @return Response response that came from the server
     */
    private static Response threadSendRequest(MethodType method, String address, Object data, Class resultType, boolean authentication, boolean useCache) {
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

    /** Main function for request execution. Checks cache, serializes, sends request, listens and handles request using private functions.
     * @param method What kind of http verb should be send
     * @param address Address where request should be send like /accounts/getBanks
     * @param data Object that should be serialized and send. Container type object should be used
     * @param resultType What kind of object we are expecting back. For example BankContainer.class
     * @param authentication Should we send authentication token to the server
     * @param useCache Should the http cache be used
     * @param response Response object where results should be posted
     * @return void
     */
    private static void executeRequest(MethodType method, String address, Object data, Class resultType, boolean authentication, boolean useCache, Response response) {
        // https://github.com/google/gson/blob/master/UserGuide.md
        String json = "{}";
        if (data != null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, ser)
                    .registerTypeAdapter(Date.class, deser).create();
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
            sendRequestOverHttps(connection, method, json);
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

    /** Sends new request over https.
     * @param connection Connection to the server that should be used
     * @param method What kind of http verb should be send
     * @param json Serialized data that should be send
     * @return void
     */
    private static void sendRequestOverHttps(HttpsURLConnection connection, MethodType method, String json) throws IOException {
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestMethod(method.name());
        if (method != MethodType.GET) {
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.flush();
            os.close();
        }
    }

    /** Checks whether there was new token inside server response. Saves the token to the class.
     * @param connection Connection to the server that should be used
     * @return String new token
     */
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

    /** Read server response from the raw byte stream to string
     * @param connection Connection to the server that should be used
     * @return String Response data
     */
    private static String readResponse(HttpsURLConnection connection) throws IOException {
        int status = connection.getResponseCode();
        InputStreamReader streamReader = null;
        if (status > 299) {
            streamReader = new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8);
        } else {
            streamReader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
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

    /** Parses the response of the server and posts it to the response object
     * @param responseString Content of the server response as string
     * @param statusCode Https status code
     * @param response Response object where results should be posted
     * @param token Token that was returned in the response header
     * @param resultType Class that is used for conversion
     * @return void
     */
    private static void handleResponse(String responseString, Integer statusCode, Response response, String token, Class resultType) {
        if (statusCode < 299) {
            Type resultTypeChecked = handleListTypes(responseString, resultType);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, ser)
                    .registerTypeAdapter(Date.class, deser).create();
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

    /** Checks whether server responded with JSON list and create appropriate GSON list handler
     * @param responseString Content of the server response as string
     * @param resultType Class that is used for conversion
     * @return Type basically GSON deserialization instructions
     */
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

    /** Posts new data to response object
     * @param resp Response object that should be updated
     * @param httpCode Https status code
     * @param response Response object that was received from the server
     * @param error Error string that was received from the server
     * @param cached Was the information received from cache
     * @param token New token received from the server
     * @return void
     */
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

    /** Set new token manually. Normaly received from the file.
     * @param newToken New JWT token
     * @return void
     */
    public static void setToken(String newToken) {
        token = newToken;
    }

    /** Returns current token that is normally saved to file.
     * @return String current token
     */
    public static String getToken() {
        if (token != null) {
            return token;
        } else {
            return "";
        }
    }

    /* Removes all cache entries */
    public static void clearCache() {
        Cache.emptyCache();
    }
}
