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

    /** Used to unpack the data from the JSON which was loaded from the local device.
     * @param bankDetails BankContainer created by the gson JSON reader.
     * @return void.
     */
    private void unpackBankContainer(BankContainer bankDetails) {
        bankId = bankDetails.bankId;
        name = bankDetails.name;
        bic = bankDetails.bic;

    }

    /** Makes a new BankContainer object with the current Bank objects details for the gson JSON packer.
     * @return void
     */
    private BankContainer packBankContainer( ) {
        BankContainer bankDetails = new BankContainer();
        bankDetails.bankId = bankId;
        bankDetails.name = name;
        bankDetails.bic = bic;
        return bankDetails;
    }

    /** Dumps important bank information to the local file on the device.
     * @return void
     */
    String dump() {
        Gson gson = new Gson();
        return gson.toJson(packBankContainer());
    }

    /** Load the content of the string (JSON) from the local file on the device.
     * @param data The string JSON which is to be loaded.
     * @return Bank object created with the data from teh data JSON string.
     */
    Bank load(String data) {
        Gson gson = new Gson();
        unpackBankContainer(gson.fromJson(data, BankContainer.class));
        return this;
    }

}
