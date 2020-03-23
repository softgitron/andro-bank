package com.example.androbank.session;

import androidx.lifecycle.MutableLiveData;

import com.example.androbank.connection.AccountTransfer;
import com.example.androbank.connection.Response;

import java.util.Observable;
import java.util.Observer;

public class Accounts {
    private static Accounts instance = new Accounts();

    public static Accounts getAccounts() {return instance;}
    private Accounts() {}

    public MutableLiveData<Account> createAccount() {
        MutableLiveData<Account> finalResults = new MutableLiveData<Account>();
        Response response = AccountTransfer.createAccount(NewAccount.class);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                Integer httpCode = response.getHttpCode();
                if (httpCode < 299) {
                    NewAccount newAccount = (NewAccount) response.getResponse();
                    Account account = new Account(newAccount.accountId, newAccount.iban, 0);
                    finalResults.postValue(account);
                } else {
                    Session session = Session.getSession();
                    session.setLastErrorCode(1);
                    session.setLastErrorMessage(response.getError());
                }
            }
        });
        return finalResults;
    }
}

class NewAccount {
    public Integer accountId;
    public String iban;
}