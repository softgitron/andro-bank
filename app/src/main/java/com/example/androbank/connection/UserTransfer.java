package com.example.androbank.connection;

import java.util.Observable;

public class UserTransfer extends Transfer {
    public static Response createUser(String username, String firstName, String lastName, String email, String phoneNumber, String password) {
        CreateUser createUser = new CreateUser(username, firstName, lastName, email, phoneNumber, password);
        return sendRequest(createUser, MethodType.POST, String.class, false);
    }
}

class CreateUser {
        public String username;
        public String firstName;
        public String lastName;
        public String email;
        public String phoneNumber;
        public String password;
        public CreateUser(String username, String firstName, String lastName, String email, String phoneNumber, String password) {
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.password = password;
        }
}