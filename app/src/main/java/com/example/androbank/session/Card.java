package com.example.androbank.session;

import java.util.List;

public class Card {
    private String cardNumber = null;
    private String accountNumber = null;
    private List<String> countryLimits = null;
    private float paymentLimit = 0;
    private float withdrawLimit = 0;

    public Card(String cardNumber, String accountNumber, List<String> countryLimits, float paymentLimit, float withdrawLimit) {
        this.cardNumber = cardNumber;
        this.accountNumber = accountNumber;
        this.countryLimits = countryLimits;
        this.paymentLimit = paymentLimit;
        this.withdrawLimit = withdrawLimit;
    }

    public String getCardNumber () { return cardNumber;}

    public String getAccountNumber() { return accountNumber;}

    public List<String> getCountryLimits() { return countryLimits;}

    public float getPaymentLimit() { return paymentLimit;}

    public float getWithdrawLimit() { return withdrawLimit;}

    public void makePayment(float amount, String toAccount) {

    }

    public void withdrawMoney(float amount) {

    }
}
