package com.example.androbank.session;

import androidx.lifecycle.MutableLiveData;

import com.example.androbank.connection.Response;
import com.example.androbank.connection.Transfer;
import com.example.androbank.containers.BankContainer;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import static com.example.androbank.connection.Transfer.sendRequest;
import static com.example.androbank.session.SessionUtils.genericErrorHandling;

public class Banks {
    private ArrayList<Bank> bankList = new ArrayList<Bank>();
    private Bank currentBank;

    /** Gets all the banks from the backend by sending a get request to the api with the Connection package.
     * @return Bank list for callback.
     */
    public MutableLiveData<ArrayList<Bank>>getBanksList() {
        bankList = new ArrayList<>();
        currentBank = null;
        MutableLiveData<ArrayList<Bank>> statusBanks = new MutableLiveData<ArrayList<Bank>>();
        Response response = sendRequest(Transfer.MethodType.GET, "/accounts/getBanks", "", BankContainer.class, false);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Response response = (Response) o;
                if (genericErrorHandling(response)) {return;}
                // Save user details to session
                ArrayList<BankContainer> bankContainers = (ArrayList<BankContainer>) response.getResponse();
                for (BankContainer bankContainer : bankContainers) {
                    Bank bank = new Bank(bankContainer.bankId, bankContainer.name, bankContainer.bic);
                    bankList.add(bank);
                }
                statusBanks.postValue(bankList);
            }
        });
        return statusBanks;
    }


    public Integer getBankIdByName(String name) {
        for (Bank bank : bankList) {
            if (bank.name == name) {
                return bank.bankId;
            }
        }
        return null;
    }

    public void setCurrentBank(Integer bankId) {
        for (Bank bank : bankList) {
            if (bank.bankId == bankId) {
                currentBank = bank;
            }
        }
    }

    public void setCurrentBank(Bank bank) {this.currentBank = bank;}

    public Bank getCurrentBank() {
        return currentBank;
    }
}
