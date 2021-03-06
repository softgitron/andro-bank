package com.example.androbank.session;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.androbank.connection.Transfer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Session {
    private static final String DEFAULT_STORAGE_FILE = "Session";
    public Accounts accounts;
    public User user;
    public Banks banks;
    public Transactions transactions;
    public Cards cards;
    private MutableLiveData<Integer> errorCode;
    private MutableLiveData<String> errorMessage;

    private static Session instance = new Session();

    public static Session getSession() {return instance;}
    private Session() {
        initializeSession();
    }

    private void initializeSession() {
        banks = new Banks();
        accounts = new Accounts();
        user = new User();
        transactions = new Transactions();
        cards = new Cards();
        errorCode = new MutableLiveData<Integer>();
        errorMessage = new MutableLiveData<String>();
    }

    void setLastErrorMessage(String errorMessage) {
        this.errorMessage.postValue(errorMessage);
    }

    void setLastErrorCode(Integer errorCode) {
        this.errorCode.postValue(errorCode);
    }

    public MutableLiveData<String>  getLastErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Integer> getLastErrorCode() {
        return errorCode;
    }

    /** Remove all data from session and its sub objects. Reset token from the transfer.
     * @param context Used for private file access in order to delete created private file.
     * @return void
     */
    public void sessionDestroy(Context context) {
        initializeSession();
        Transfer.setToken(null);
        File file = new File(context.getFilesDir(), DEFAULT_STORAGE_FILE);
        file.delete();
    }

    /** Dumps important session information to the local file like user information and session token.
     * @param context Used for private file access in order to access created private file.
     * @return void
     */
    public void sessionDump(Context context) {
        // If the user is clearly not logged in do not save anything.
        if (user.getUsername() == null) {
            return;
        }
        ArrayList<String> sessionDump = new ArrayList<>();
        sessionDump.add(Transfer.getToken());
        sessionDump.add(user.dump());
        sessionDump.add(banks.getCurrentBank().dump());
        Gson gson = new Gson();
        String dump = gson.toJson(sessionDump);
        saveToFile(context, dump);
    }

    /** Loads important session information from the local file like user information and session token.
     * @param context Used for private file access in order to access created private file.
     * @return void
     */
    public Boolean sessionLoad(Context context) {
        String data = loadFile(context);
        if (data == null || data == "") {
            return false;
        }
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<String> sessionLoad = gson.fromJson(data, type);
        Transfer.setToken(sessionLoad.get(0));
        user.load(sessionLoad.get(1));
        banks.setCurrentBank(new Bank().load(sessionLoad.get(2) ) );

        return true;
    }

    /** Save content of the string to the private file.
     * @param context Used for private file access in order to access created private file.
     * @param content String to be saved
     * @return void
     */
    private void saveToFile(Context context, String content) {
        // https://developer.android.com/training/data-storage/app-specific
        try (FileOutputStream fos = context.openFileOutput(DEFAULT_STORAGE_FILE, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Load content of the string from the private file.
     * @param context Used for private file access in order to access created private file.
     * @return String contents of the file
     */
    private String loadFile(Context context) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(DEFAULT_STORAGE_FILE);
        } catch (FileNotFoundException e) {
            return null;
        }
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            return stringBuilder.toString();
        }
    }
}
