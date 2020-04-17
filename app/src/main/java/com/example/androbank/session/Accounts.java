package com.example.androbank.session;

import androidx.lifecycle.MutableLiveData;

import com.example.androbank.connection.Response;
import com.example.androbank.connection.Transfer;
import com.example.androbank.containers.AccountContainer;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import static com.example.androbank.connection.Transfer.sendRequest;

public class Accounts {
    private static Accounts instance = new Accounts();

    public static Accounts getAccounts() {return instance;}
    private Accounts() {}
    private ArrayList<Account> accountList = new ArrayList<Account>();

    public MutableLiveData<Account> createAccount() {
        MutableLiveData<Account> finalResults = new MutableLiveData<Account>();
        Response response = sendRequest(Transfer.MethodType.POST, "/accounts/createAccount", null, AccountContainer.class, true);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                Integer httpCode = response.getHttpCode();
                if (httpCode < 299) {
                    AccountContainer newAccount = (AccountContainer) response.getResponse();
                    Account account = new Account(newAccount.accountId, newAccount.iban, 0, newAccount.type);
                    accountList.add(account);
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

    public ArrayList<Account> getAccountsList() {
        //TODO periodically update lists of accounts using API
        return accountList;
    }
}