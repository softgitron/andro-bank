package com.example.androbank.connection.containers;

public class UserContainer {
    public Integer userId;
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String phoneNumber;
    public String password;
    public Integer bankId;
    public UserContainer(String username, String firstName, String lastName, String email, String phoneNumber, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
    public UserContainer(){}
}
