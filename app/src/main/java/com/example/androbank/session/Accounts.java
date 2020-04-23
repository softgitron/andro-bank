package com.example.androbank.session;

import androidx.constraintlayout.solver.Cache;
import androidx.lifecycle.MutableLiveData;

import com.example.androbank.connection.Response;
import com.example.androbank.connection.Transfer;
import com.example.androbank.containers.AccountContainer;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import static com.example.androbank.connection.Transfer.sendRequest;
import static com.example.androbank.session.SessionUtils.genericErrorHandling;

public class Accounts {
    private ArrayList<Account> accountList = new ArrayList<Account>();

    public ArrayList<Account> getSessionAccounts () {
        return accountList;
    }

    public MutableLiveData<Account> createAccount() {
        MutableLiveData<Account> finalResults = new MutableLiveData<Account>();
        Response response = sendRequest(Transfer.MethodType.POST, "/accounts/createAccount", null, AccountContainer.class, true);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                if (genericErrorHandling(response)) {return;};
                AccountContainer newAccount = (AccountContainer) response.getResponse();
                Account account = new Account(newAccount.accountId, newAccount.iban, 0, newAccount.type);
                accountList.add(account);
                finalResults.postValue(account);
            }
        });
        Transfer.clearCache();
        return finalResults;
    }

    public MutableLiveData<ArrayList<Account>> getAccountsList(boolean useCache) {
        //TODO periodically update lists of accounts using API
        accountList.clear();
        MutableLiveData<ArrayList<Account>> statusAccounts = new MutableLiveData<ArrayList<Account>>();
        Response response = sendRequest(Transfer.MethodType.GET, "/accounts/getAccounts", "", AccountContainer.class, true, useCache);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                System.out.println("Session.Accounts prints: " + response.getResponse().toString());
                if (genericErrorHandling(response)) {return;};
                // Save user details to session
                ArrayList<AccountContainer> accountContainers = (ArrayList<AccountContainer>) response.getResponse();
                for (AccountContainer accountContainer : accountContainers) {
                    Account account = new Account(accountContainer.accountId, accountContainer.iban, accountContainer.balance, accountContainer.type);
                    accountList.add(account);
                }
                statusAccounts.postValue(accountList);
            }
        });
        return statusAccounts;
    }

    /**
     *  Simple search function for the Accounts arrayList.
     * @param iban IBAN string used for finding account id.
     * @return Returns the account id belonging to the given IBAN or null if it does not exists.
     */
    public Integer findAccountIdByIban(String iban) {
        for (Account account: this.accountList) {
            if(account.getIban().equals(iban) ) {
                return  account.getAccountId();
            }
        }
        return null;
    }
}