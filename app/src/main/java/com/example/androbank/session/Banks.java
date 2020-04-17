package com.example.androbank.session;

import androidx.lifecycle.MutableLiveData;

import com.example.androbank.connection.Response;
import com.example.androbank.connection.Transfer;
import com.example.androbank.containers.BankContainer;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import static com.example.androbank.connection.Transfer.sendRequest;
import static com.example.androbank.session.SessionUtils.genericErrorHandling;
import static com.example.androbank.session.SessionUtils.shouldBeUpdated;

public class Banks {
    private static Banks instance = new Banks();

    public static Banks getBanks() {return instance;}
    private Banks() {}
    private Date timestamp;
    private ArrayList<Bank> bankList = new ArrayList<Bank>();

    public MutableLiveData<ArrayList<Bank>>getBanksList() {
        MutableLiveData<ArrayList<Bank>> statusBanks = new MutableLiveData<ArrayList<Bank>>();
        if (!shouldBeUpdated(timestamp) && bankList.size() != 0) {
            statusBanks.postValue(bankList);
        } else {
            Response response = sendRequest(Transfer.MethodType.POST, "/accounts/getBanks", "", BankContainer.class, false);
            response.addObserver(new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    Response response = (Response) o;
                    if (genericErrorHandling(response)) {return;};
                    // Save user details to session
                    ArrayList<BankContainer> bankContainers = (ArrayList<BankContainer>) response.getResponse();
                    for (BankContainer bankContainer : bankContainers) {
                        Bank bank = new Bank(bankContainer.bankId, bankContainer.name, bankContainer.bic);
                        bankList.add(bank);
                    }
                    statusBanks.postValue(bankList);
                }
            });
        }
        return statusBanks;
    }
}
