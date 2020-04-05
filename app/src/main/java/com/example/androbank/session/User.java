package com.example.androbank.session;

import androidx.lifecycle.MutableLiveData;

import com.example.androbank.connection.Response;
import com.example.androbank.connection.UserTransfer;
import com.example.androbank.connection.containers.UserContainer;

import java.util.Observable;
import java.util.Observer;

public class User {
    private static User instance = new User();

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Boolean loggedIn = false;

    public static User getUser() {return instance;}
    private User() {}

    public MutableLiveData<User> createUser(String username, String firstName, String lastName, String email, String phoneNumber, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        MutableLiveData<User> statusUser = new MutableLiveData<User>();
        Response response = UserTransfer.createUser(username, firstName, lastName, email, phoneNumber, password);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                loggedIn = true;
                Response response = (Response) o;
                Integer httpCode = response.getHttpCode();
                if (httpCode < 299) {
                    statusUser.postValue(getUser());
                } else {
                    loggedIn = false;
                    Session session = Session.getSession();
                    session.setLastErrorCode(1);
                    session.setLastErrorMessage(response.getError());
                }
            }
        });
        return statusUser;
    }

    public MutableLiveData<User> login(String loginEmail, String loginPassword) {
        MutableLiveData<User> statusUser = new MutableLiveData<User>();
        Response response = UserTransfer.login(loginEmail, loginPassword);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                Integer httpCode = response.getHttpCode();
                if (httpCode < 299) {
                    UserContainer userDetails = (UserContainer) response.getResponse();
                    loggedIn = true;
                    username = userDetails.username;
                    firstName = userDetails.firstName;
                    lastName = userDetails.lastName;
                    email = userDetails.email;
                    phoneNumber = userDetails.phoneNumber;
                    statusUser.postValue(getUser());
                } else {
                    loggedIn = false;
                    Session session = Session.getSession();
                    session.setLastErrorCode(1);
                    session.setLastErrorMessage(response.getError());
                }
            }
        });
        return statusUser;
    }

    public Boolean getLoggedInStatus() { return loggedIn; }
    public String getFirstName() { return firstName; }

    public String getName(){
        String name="";
        return name;
    }

    public String setName(){

    }

    public String getEmail(){

    }

    public String setEmail(){

    }

    public String getPhoneNumber (){

    }

    public String setPhoneNumber(){

    }
}
