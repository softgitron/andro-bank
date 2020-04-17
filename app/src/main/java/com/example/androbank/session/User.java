package com.example.androbank.session;

import androidx.lifecycle.MutableLiveData;

import com.example.androbank.connection.Response;
import com.example.androbank.connection.Transfer;
import com.example.androbank.containers.*;

import java.util.Observable;
import java.util.Observer;

import static com.example.androbank.connection.Transfer.sendRequest;
import static com.example.androbank.session.SessionUtils.genericErrorHandling;

public class User {
    private static User instance = new User();

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    public static User getUser() {
        return instance;
    }

    private User() {
    }

    public MutableLiveData<User> createUser(Integer bankId, String username, String firstName, String lastName, String email, String phoneNumber, String password) {
        UserContainer createUser = new UserContainer();
        createUser.bankId = bankId;
        createUser.username = username;
        createUser.firstName = firstName;
        createUser.lastName = lastName;
        createUser.email = email;
        createUser.phoneNumber = phoneNumber;
        createUser.password = password;

        MutableLiveData<User> statusUser = new MutableLiveData<User>();
        Response response = sendRequest(Transfer.MethodType.POST, "/users/createUser", createUser, UserContainer.class, false);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                if (genericErrorHandling(response)) {return;};
                // Save user details to session
                UserContainer userDetails = (UserContainer) response.getResponse();
                unpackUserContainer(userDetails);
                statusUser.postValue(getUser());
            }
        });
        return statusUser;
    }

    public MutableLiveData<User> login(Integer loginBankId, String loginEmail, String loginPassword) {
        UserContainer loginUser = new UserContainer();
        loginUser.email = loginEmail;
        loginUser.password = loginPassword;
        loginUser.bankId = loginBankId;

        MutableLiveData<User> statusUser = new MutableLiveData<User>();
        Response response = sendRequest(Transfer.MethodType.POST, "/users/login", loginUser, UserContainer.class, false);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                if (genericErrorHandling(response)) {return;};
                UserContainer userDetails = (UserContainer) response.getResponse();
                unpackUserContainer(userDetails);
                statusUser.postValue(getUser());
            }
        });
        return statusUser;
    }

    private void unpackUserContainer(UserContainer userDetails) {
        username = userDetails.username;
        firstName = userDetails.firstName;
        lastName = userDetails.lastName;
        email = userDetails.email;
        phoneNumber = userDetails.phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getName() {
        String name = "";
        return name;
    }

    public String setName() {
        return null;
    }

    public String getEmail() {
        return null;
    }

    public String setEmail() {
        return null;
    }

    public String getPhoneNumber() {
        return null;
    }

    public String setPhoneNumber() {
        return null;
    }
}
