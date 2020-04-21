package com.example.androbank.session;

import androidx.lifecycle.MutableLiveData;

import com.example.androbank.connection.Response;
import com.example.androbank.connection.Transfer;
import com.example.androbank.containers.*;
import com.google.gson.Gson;

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

    public User() {instance = this;}

    public MutableLiveData<User> createUser(String bankName, String username, String firstName, String lastName, String email, String phoneNumber, String password) {
        Session session = Session.getSession();
        UserContainer createUser = new UserContainer();
        createUser.bankId = session.banks.getBankIdByName(bankName);
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
                session.banks.setCurrentBank(userDetails.bankId);
                statusUser.postValue(instance);
            }
        });
        return statusUser;
    }

    public MutableLiveData<User> login(Integer loginBankId, String loginEmail, String loginPassword) {
        Session session = Session.getSession();
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
                System.out.println("Error message HTTP: " + response.getHttpCode());
                if (response.getHttpCode() == 401) {
                    session.setLastErrorMessage("Password was not found.");
                return;
                } else if (genericErrorHandling(response)){
                    return;
                }
                UserContainer userDetails = (UserContainer) response.getResponse();
                unpackUserContainer(userDetails);
                session.banks.setCurrentBank(userDetails.bankId);
                statusUser.postValue(instance);
            }
        });
        return statusUser;
    }

    /**
     * Sends the updated user info to the server and directly modifies current session
     * user details with the values given back by the server.
     * @param username New username for current user.
     * @param firstName New firstName for current user.
     * @param lastName New lastName for current user.
     * @param email New email for current user.
     * @param phoneNumber New phoneNumber for current user.
     * @return User status for callback.
     */
    public MutableLiveData<User> updateUser(String username, String firstName, String lastName, String email, String phoneNumber) {

        Session session = Session.getSession();
        UserContainer updateUser = new UserContainer();
        updateUser.username = username;
        updateUser.firstName = firstName;
        updateUser.lastName = lastName;
        updateUser.email = email;
        updateUser.phoneNumber = phoneNumber;

        MutableLiveData<User> statusUser = new MutableLiveData<User>();
        Response response = sendRequest(Transfer.MethodType.PATCH, "/users/updateUserDetails", updateUser, UserContainer.class, true);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;

                if (genericErrorHandling(response)) {return;};
                UserContainer userDetails = (UserContainer) response.getResponse();
                System.out.println(userDetails.email);
                unpackUserContainer(userDetails);
                session.banks.setCurrentBank(userDetails.bankId);
                statusUser.postValue(instance);
            }
        });
        return statusUser;
    }

    public MutableLiveData<User> changePassword(String newPassword) {
        Session session = Session.getSession();
        UserContainer updateUser = new UserContainer();
        updateUser.password = newPassword;

        MutableLiveData<User> statusUser = new MutableLiveData<User>();
        Response response = sendRequest(Transfer.MethodType.PATCH, "/users/updateUserDetails", updateUser, UserContainer.class, true);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;

                if (genericErrorHandling(response)) {return;};
                UserContainer userDetails = (UserContainer) response.getResponse();
                unpackUserContainer(userDetails);
                //session.banks.setCurrentBank(userDetails.bankId);
                statusUser.postValue(instance);
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

    private UserContainer packUserContainer() {
        UserContainer container = new UserContainer();
        container.username = username;
        container.firstName = firstName;
        container.lastName = lastName;
        container.email = email;
        container.phoneNumber = phoneNumber;
        return container;
    }

    public String getUsername() {
        return username;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }


    String dump() {
        Gson gson = new Gson();
        return gson.toJson(packUserContainer());
    }

    void load(String data) {
        Gson gson = new Gson();
        unpackUserContainer(gson.fromJson(data, UserContainer.class));
    }
}
