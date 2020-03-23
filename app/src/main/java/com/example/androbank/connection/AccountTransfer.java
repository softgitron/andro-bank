package com.example.androbank.connection;

public class AccountTransfer extends Transfer {
    public static Response createAccount(Class returnType) {
        return sendRequest(MethodType.POST, "/accounts/createAccount", null, returnType, true);
    }
}