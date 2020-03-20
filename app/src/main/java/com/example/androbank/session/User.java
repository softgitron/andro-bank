package com.example.androbank.session;

import androidx.lifecycle.MutableLiveData;

import com.example.androbank.connection.Response;
import com.example.androbank.connection.UserTransfer;

import java.util.Observable;
import java.util.Observer;

public class User {
    private static User instance = new User();

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    public enum LoginStatus {
        LOGGED_IN,
        LOGIN_ERROR,
        LOGGED_OUT
    }
    private MutableLiveData<LoginStatus> loginStatus;

    public static User getUser() {return instance;}
    private User() {
        loginStatus = new MutableLiveData<>();

        loginStatus.setValue(LoginStatus.LOGGED_OUT);
    }

    public MutableLiveData<LoginStatus> createUser(String username, String firstName, String lastName, String email, String phoneNumber, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        Response response = UserTransfer.createUser(username, firstName, lastName, email, phoneNumber, password);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                Integer httpCode = response.getHttpCode();
                if (httpCode < 299) {
                    loginStatus.postValue(LoginStatus.LOGGED_IN);
                } else {
                    Session session = Session.getSession();
                    session.setLastErrorCode(1);
                    session.setLastErrorMessage(response.getError());
                    loginStatus.postValue(LoginStatus.LOGIN_ERROR);
                }
            }
        });
        return loginStatus;
    }

    public MutableLiveData<LoginStatus> getLoginStatus() {
        return loginStatus;
    }
}
