package com.example.androbank.session;

import androidx.lifecycle.MutableLiveData;

import com.example.androbank.connection.Response;
import com.example.androbank.connection.Transfer;
import com.example.androbank.containers.AccountContainer;
import com.example.androbank.containers.FutureTransactionContainer;
import com.example.androbank.containers.TransactionContainer;

import static com.example.androbank.connection.Transfer.sendRequest;
import static com.example.androbank.session.SessionUtils.genericErrorHandling;

import java.net.Inet4Address;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class Transactions {


    public MutableLiveData<Account> makeTransaction(Integer fromAccountId, String toAccountIban, Integer amount) {
        MutableLiveData<Account> finalResult = new MutableLiveData<Account>();
        TransactionContainer requestContainer = new TransactionContainer();
        requestContainer.fromAccountId = fromAccountId;
        requestContainer.toAccountIban = toAccountIban;
        requestContainer.amount = amount;
        Response response = sendRequest(Transfer.MethodType.POST, "/accounts/transfer", requestContainer, AccountContainer.class, true);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                if (genericErrorHandling(response)) {return;};
                AccountContainer newAccount = (AccountContainer) response.getResponse();
                Account account = new Account(newAccount.accountId, newAccount.iban, newAccount.balance, newAccount.type);
                finalResult.postValue(account);
            }
        });
        return finalResult;
    }

    public MutableLiveData<String> makeFutureTransaction(Integer fromAccountId, String toAccountIban, Integer amount, Date paymentDate, Integer atInterval, Integer times) {
        MutableLiveData<String> finalResult = new MutableLiveData<String>();
        FutureTransactionContainer requestContainer = new FutureTransactionContainer();
        requestContainer.fromAccountId = fromAccountId;
        requestContainer.toAccountIban = toAccountIban;
        requestContainer.amount = amount;
        System.out.println("Lets future");
        requestContainer.atTime = paymentDate;
        System.out.println(requestContainer.atTime);
        if (atInterval != 0) {
            requestContainer.atInterval = atInterval;
            requestContainer.times = times;
        } else {
            requestContainer.atInterval = null;
            requestContainer.times = null;
        }
        Response response = sendRequest(Transfer.MethodType.POST, "/accounts/futureTransfer", requestContainer, String.class, true, false);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                //System.out.println(response.getResponse().toString());

                if (genericErrorHandling(response)) {
                    System.out.println(response.getError());
                    return;
                };
                finalResult.postValue(response.getResponse().toString());
            }
        });
        return finalResult;
    }

    /**
     * Gets all user's given account's transactions.
     * @param accountId
     * @return
     */
    public MutableLiveData<ArrayList<Transaction>> getTransactions(Integer accountId) {
        ArrayList<Transaction> transactionsList = new ArrayList<Transaction>();
        MutableLiveData<ArrayList<Transaction>> finalResult = new MutableLiveData<ArrayList<Transaction>>();
        AccountContainer requestContainer = new AccountContainer();
        requestContainer.accountId = accountId;
        Response response = sendRequest(Transfer.MethodType.POST, "/transactions/getTransactions", requestContainer, TransactionContainer.class, true);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                if (genericErrorHandling(response)) {return;};
                ArrayList<TransactionContainer> transactionContainers = (ArrayList<TransactionContainer>) response.getResponse();
                for (TransactionContainer transactionContainer : transactionContainers) {
                    /*try {
                        Date help = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy, m:h:s aa", new Locale("en", "US"));
                        String hello = simpleDateFormat.format(help);
                        Date date = simpleDateFormat.parse(transactionContainer.time);
                        System.out.println("Hello");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }*/
                    Transaction transaction = new Transaction(transactionContainer.transferId, transactionContainer.fromAccountIban, transactionContainer.toAccountIban, transactionContainer.amount, transactionContainer.time);
                    transactionsList.add(transaction);
                }
                finalResult.postValue(transactionsList);
            }
        });
        return finalResult;
    }

    /**
     * Sends request to the server to add money to the account.
     * @param accountId Id of the account money is to be added.
     * @param moneyToAdd Amount of money to be added.
     * @return Return the current account on which the money was added for callback.
     */
    public MutableLiveData<Account> depositMoney(Integer accountId, float moneyToAdd) {
        MutableLiveData<Account> finalResult = new MutableLiveData<Account>();
        AccountContainer requestContainer = new AccountContainer();

        requestContainer.accountId = accountId;
        requestContainer.balance = Math.round(moneyToAdd * 100);

        Response response = sendRequest(Transfer.MethodType.POST, "/accounts/deposit", requestContainer, AccountContainer.class, true);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                if (genericErrorHandling(response)) {return;};
                AccountContainer newAccount = (AccountContainer) response.getResponse();
                Account account = new Account(newAccount.accountId, newAccount.iban, newAccount.balance, newAccount.type);
                finalResult.postValue(account);
            }
        });
        return finalResult;
    }

    /**
     * Gets all of the given user's account's future transactions.
     * @param accountId
     * @return List of future transactions for callback.
     */
    public MutableLiveData<ArrayList<FutureTransaction>> getFutureTransactions(Integer accountId) {
        ArrayList<FutureTransaction> transactionsList = new ArrayList<FutureTransaction>();
        MutableLiveData<ArrayList<FutureTransaction>> finalResult = new MutableLiveData<ArrayList<FutureTransaction>>();
        AccountContainer requestContainer = new AccountContainer();
        requestContainer.accountId = accountId;
        Response response = sendRequest(Transfer.MethodType.POST, "/transactions/getFutureTransactions", requestContainer, FutureTransactionContainer.class, true);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                if (genericErrorHandling(response)) {return;};
                ArrayList<FutureTransactionContainer> transactionContainers = (ArrayList<FutureTransactionContainer>) response.getResponse();
                for (FutureTransactionContainer transactionContainer : transactionContainers) {
                    //System.out.println("Time is "+transactionContainer.time);
                    FutureTransaction transaction = new FutureTransaction(transactionContainer.futureTransferId, transactionContainer.fromAccountIban, transactionContainer.toAccountIban, transactionContainer.amount, transactionContainer.atTime, transactionContainer.times);
                    transactionsList.add(transaction);
                }
                finalResult.postValue(transactionsList);
            }
        });
        return finalResult;
    }

    // Todo Finish implementation.
    public void deleteFutureTransaction(Integer transactionId, String fromAccount) {
        System.out.println("NOT IMPLEMENTED YET!");
    }
}
