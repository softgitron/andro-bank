package com.example.androbank.session;

class Accounts {
    private static Accounts instance = new Accounts();

    public static Accounts getAccounts() {return instance;}
    private Accounts() {}
}
