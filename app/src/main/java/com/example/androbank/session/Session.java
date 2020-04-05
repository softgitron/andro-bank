package com.example.androbank.session;

import androidx.lifecycle.MutableLiveData;

public class Session {
    public Accounts accounts;
    public User user;
    public Bank bank;
    private MutableLiveData<Integer> errorCode;
    private MutableLiveData<String> errorMessage;

    private static Session instance = new Session();

    public static Session getSession() {return instance;}
    private Session() {
        accounts = Accounts.getAccounts();
        user = User.getUser();
        errorCode = new MutableLiveData<Integer>(0);
        errorMessage = new MutableLiveData<String>("");
    }

    void setLastErrorMessage(String errorMessage) {
        this.errorMessage.postValue(errorMessage);
    }

    void setLastErrorCode(Integer errorCode) {
        this.errorCode.postValue(errorCode);
    }

    public MutableLiveData<String>  getLastErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Integer> getLastErrorCode() {
        return errorCode;
    }


}
