package com.example.androbank.session;

import androidx.lifecycle.MutableLiveData;

import com.example.androbank.connection.Response;
import com.example.androbank.connection.Transfer;
import com.example.androbank.containers.AccountContainer;
import com.example.androbank.containers.TransactionContainer;

import static com.example.androbank.connection.Transfer.sendRequest;
import static com.example.androbank.session.SessionUtils.genericErrorHandling;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Transactions {
    private ArrayList<Transaction> transactionsList = new ArrayList<Transaction>();

    public MutableLiveData<Account> makeTransaction(Integer fromAccountId, String toAccountIban, Integer amount) {
        MutableLiveData<Account> finalResult = new MutableLiveData<Account>();
        TransactionContainer requestContainer = new TransactionContainer();
        requestContainer.fromAccountId = fromAccountId;
        requestContainer.toAccountIban = toAccountIban;
        requestContainer.amount = amount;
        Response response = sendRequest(Transfer.MethodType.POST, "/accounts/transfer", requestContainer, AccountContainer.class, true);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                Response response = (Response) o;
                if (genericErrorHandling(response)) {return;};
                AccountContainer newAccount = (AccountContainer) response.getResponse();
                Account account = new Account(newAccount.accountId, newAccount.iban, newAccount.balance, newAccount.type);
                finalResult.postValue(account);
            }
        });
        return finalResult;
    }

    public MutableLiveData<ArrayList<Transaction>> getTransactions(Integer accountId) {
        MutableLiveData<ArrayList<Transaction>> finalResult = new MutableLiveData<ArrayList<Transaction>>();
        AccountContainer requestContainer = new AccountContainer();
        requestContainer.accountId = accountId;
        Response response = sendRequest(Transfer.MethodType.POST, "/transactions/getTransactions", requestContainer, TransactionContainer.class, true);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                Response response = (Response) o;
                if (genericErrorHandling(response)) {return;};
                ArrayList<TransactionContainer> transactionContainers = (ArrayList<TransactionContainer>) response.getResponse();
                for (TransactionContainer transactionContainer : transactionContainers) {
                    Transaction transaction = new Transaction(transactionContainer.fromAccountIban, transactionContainer.toAccountIban, transactionContainer.amount, transactionContainer.time);
                    transactionsList.add(transaction);
                }
                finalResult.postValue(transactionsList);
            }
        });
        return finalResult;
    }
}
