package com.example.androbank.session;

import com.example.androbank.containers.BankContainer;
import com.google.gson.Gson;

public class Bank {
    public Bank(Integer bankId, String name, String bic) {
        this.bankId = bankId;
        this.name = name;
        this.bic = bic;
    }

    public Bank() {
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

    @Override
    public String toString() {
        return "BankID: " + bankId + " Name: " + name + " BIC: " + bic;
    }

    private void unpackBankContainer(BankContainer bankDetails) {
        bankId = bankDetails.bankId;
        name = bankDetails.name;
        bic = bankDetails.bic;

    }

    // Todo Add comment
    private BankContainer packBankContainer( ) {
        BankContainer bankDetails = new BankContainer();
        bankDetails.bankId = bankId;
        bankDetails.name = name;
        bankDetails.bic = bic;
        return bankDetails;
    }

    // Todo Add comment
    String dump() {
        Gson gson = new Gson();
        return gson.toJson(packBankContainer());
    }

    // Todo Add comment
    Bank load(String data) {
        Gson gson = new Gson();
        unpackBankContainer(gson.fromJson(data, BankContainer.class));
        return this;
    }

}
