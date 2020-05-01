package com.example.androbank.session;

import androidx.lifecycle.MutableLiveData;

import com.example.androbank.connection.Response;
import com.example.androbank.connection.Transfer;
import com.example.androbank.containers.AccountContainer;
import com.example.androbank.containers.FutureTransactionContainer;
import com.example.androbank.containers.TransactionContainer;

import static com.example.androbank.connection.Transfer.sendRequest;
import static com.example.androbank.session.SessionUtils.genericErrorHandling;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class Transactions {


    /**Used for making a basic transfer between two accounts. Sends a post request to the backend with the connection package.
     * @param fromAccountId Account from which the transfer is to be made.
     * @param toAccountIban Account to which the money is going to be deposited.
     * @param amount Amount of money being exchanged in the transaction.
     * @return Account received from the server for callback.
     */
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
                if (genericErrorHandling(response)) {return;}
                AccountContainer newAccount = (AccountContainer) response.getResponse();
                Account account = new Account(newAccount.accountId, newAccount.iban, newAccount.balance, newAccount.type);
                finalResult.postValue(account);
            }
        });
        return finalResult;
    }

    /** Makes a new transaction which is going to happen some time in the future. Sends a post request to the backend with the relevant data by using the connection package.
     * @param fromAccountId Account from which the transfer is to be made.
     * @param toAccountIban Account to which the money is going to be deposited.
     * @param amount Amount of money being exchanged in the transaction.
     * @param paymentDate When the transaction is going to be executed.
     * @param atInterval (optional) How often in minutes transaction should occur. Default value: null, Size range: 1..
     * @param times (optional) How many times transfer should occur. Default value: null, Size range: 1..
     * @return Backend response string for callback.
     */
    public MutableLiveData<String> makeFutureTransaction(Integer fromAccountId, String toAccountIban, Integer amount, Date paymentDate, Integer atInterval, Integer times) {
        MutableLiveData<String> finalResult = new MutableLiveData<String>();
        FutureTransactionContainer requestContainer = new FutureTransactionContainer();
        requestContainer.fromAccountId = fromAccountId;
        requestContainer.toAccountIban = toAccountIban;
        requestContainer.amount = amount;
        System.out.println("Lets future with amount: " + amount);
        requestContainer.atTime = paymentDate;
        System.out.println(requestContainer.atTime);
        if (atInterval != 0) {
            requestContainer.atInterval = atInterval;
            requestContainer.times = times;
        } else {
            requestContainer.atInterval = null;
            requestContainer.times = 1;
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
                }
                finalResult.postValue(response.getResponse().toString());
            }
        });
        return finalResult;
    }

    /**Gets all the transaction on one of the users accounts. Sends a post request to the server with the accountID using the connection package.
     * @param accountId An accounts which transactions are to be fetched.
     * @return Transactions list gotten from the backend for callback.
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
                if (genericErrorHandling(response)) {return;}
                ArrayList<TransactionContainer> transactionContainers = (ArrayList<TransactionContainer>) response.getResponse();
                for (TransactionContainer transactionContainer : transactionContainers) {
                    Transaction transaction = new Transaction(transactionContainer.transferId, transactionContainer.fromAccountIban,transactionContainer.fromAccountId,
                            transactionContainer.toAccountIban, transactionContainer.amount, transactionContainer.time, transactionContainer.fromAccountBic, transactionContainer.toAccountBic);
                    transactionsList.add(transaction);
                }
                finalResult.postValue(transactionsList);
            }
        });
        return finalResult;
    }

    /**Sends a post request to the backend to add money to the given account. Uses the connection package.
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
                if (genericErrorHandling(response)) {return;}
                AccountContainer newAccount = (AccountContainer) response.getResponse();
                Account account = new Account(newAccount.accountId, newAccount.iban, newAccount.balance, newAccount.type);
                finalResult.postValue(account);
            }
        });
        return finalResult;
    }

    /**Gets all future transactions on one of the users bank accounts. Uses connection package to send a post request to the backend with the relevant data.
     * @param accountId An accounts which upcoming transactions are to be fetched.
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
                if (genericErrorHandling(response)) {return;}
                ArrayList<FutureTransactionContainer> transactionContainers = (ArrayList<FutureTransactionContainer>) response.getResponse();
                for (FutureTransactionContainer transactionContainer : transactionContainers) {
                    //System.out.println("Time is "+transactionContainer.time);
                    FutureTransaction transaction = new FutureTransaction(transactionContainer.futureTransferId, transactionContainer.fromAccountIban, transactionContainer.fromAccountId,
                            transactionContainer.toAccountIban, transactionContainer.amount, transactionContainer.atTime, transactionContainer.times, transactionContainer.fromAccountBic, transactionContainer.toAccountBic);
                    transactionsList.add(transaction);
                }
                finalResult.postValue(transactionsList);
            }
        });
        return finalResult;
    }

    /** Sends a delete request to backend with the connection package to delete one of the upcoming transactions.
     * @param futureTransactionId ID of the future transaction
     * @param fromAccountId Account from which the transfer would be made.
     * @return  1 or 200 depending on if the request was successful for callback.
     */
    public MutableLiveData<Integer> deleteFutureTransaction(Integer futureTransactionId, Integer fromAccountId) {
        MutableLiveData<Integer> finalResult = new MutableLiveData<Integer>();
        FutureTransactionContainer requestContainer = new FutureTransactionContainer();

        requestContainer.futureTransferId = futureTransactionId;
        requestContainer.fromAccountId = fromAccountId;
        System.out.println("Sending delete Future Transaction request!");
        Response response = sendRequest(Transfer.MethodType.DELETE, "/transactions/deleteFutureTransaction", requestContainer, String.class, true);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                if (genericErrorHandling(response)) {finalResult.postValue(1); return;}
                finalResult.postValue(200);
            }
        });
        return finalResult;
    }
}
