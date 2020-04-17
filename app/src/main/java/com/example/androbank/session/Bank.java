package com.example.androbank.session;

public class Bank {
    public Bank(Integer bankId, String name, String bic) {
        this.bankId = bankId;
        this.name = name;
        this.bic = bic;
    }

    public Integer bankId;
    public String name;
    public String bic;

    public Integer getBankId(){
        return bankId;
    }

    public String getName(){
        return name;
    }

    public String getBIC() {
        return bic;
    }
}
