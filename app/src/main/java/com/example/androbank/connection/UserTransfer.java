package com.example.androbank.connection;

import com.example.androbank.connection.containers.UserContainer;

public class UserTransfer extends Transfer {
    public static Response createUser(String username, String firstName, String lastName, String email, String phoneNumber, String password) {
        UserContainer createUser = new UserContainer(username, firstName, lastName, email, phoneNumber, password);
        return sendRequest(MethodType.POST, "/users/createUser", createUser, String.class, false);
    }

    public static Response login(String email, String password) {
        UserContainer userDetails = new UserContainer();
        userDetails.email = email;
        userDetails.password = password;
        return sendRequest(MethodType.POST, "/users/login", userDetails, UserContainer.class, false);
    }
}